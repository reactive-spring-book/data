package rsb.data.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RunWith(SpringRunner.class)
@DataMongoTest
@Log4j2
@Import(OrderService.class)
public class OrderServiceTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@BeforeClass
	public static void warn() throws Exception {
		File here = new File(".");
		log.info("here: " + here.getAbsoluteFile().toString());
		File mongodbSetupScript = new File(new File(here, ".."),
				"ci/bin/setup-mongodb.sh");
		Resource script = new FileSystemResource(mongodbSetupScript);
		Assertions.assertThat(script.exists()).isTrue();
		try (Reader r = new BufferedReader(
				new InputStreamReader(script.getInputStream()))) {
			String contents = FileCopyUtils.copyToString(r);
			log.warn("in order to run this test you need to be talking to"
					+ " a MongoDB instance that supports replicas! "
					+ "To test, I usually use a script like this to start up my instance:\n\n"
					+ contents);
		}
	}

	@Before
	public void configureCollectionsBeforeTests() {

		Mono<Boolean> collectionExists = this.reactiveMongoTemplate
				.collectionExists(Order.class);
		Mono<MongoCollection<Document>> createIfMissing = collectionExists.filter(x -> !x)
				.flatMap(doesNotExist -> this.reactiveMongoTemplate
						.createCollection(Order.class));

		StepVerifier.create(createIfMissing).verifyComplete();

		/*
		 * Flux<MongoCollection<Document>> setup = this.reactiveMongoTemplate
		 * .dropCollection(Order.class)
		 * .thenMany(this.reactiveMongoTemplate.createCollection(Order.class));
		 * StepVerifier.create(setup).verifyComplete();
		 */
	}

	@Test
	public void createOrders() {

		Publisher<Order> orders = this.orderRepository.deleteAll()
				.thenMany(this.orderService.createOrders("1", "2", "3"))
				.thenMany(this.orderRepository.findAll());

		StepVerifier.create(orders).expectNextCount(3).verifyComplete();
	}

	@Test
	public void createOrdersAndFail() {

		Publisher<Order> orders = this.orderRepository.deleteAll()
				.thenMany(this.orderService.createOrders("1", "2",
						OrderService.BOOM_EXCEPTION))
				.thenMany(this.orderRepository.findAll());

		StepVerifier.create(orders).expectNextCount(0).verifyError();

		StepVerifier.create(this.orderRepository.findAll()).expectNextCount(0)
				.verifyComplete();
	}

}