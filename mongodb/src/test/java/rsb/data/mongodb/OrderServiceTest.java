package rsb.data.mongodb;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
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
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.nio.charset.Charset;

@RunWith(SpringRunner.class)
@DataMongoTest // <1>
@Log4j2
@Import(OrderService.class)
public class OrderServiceTest {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private OrderService service;

	@Autowired
	private ReactiveMongoTemplate template;

	// <2>
	@BeforeClass
	public static void warn() throws Exception {
		Resource script = new FileSystemResource(
				new File("..", "ci/bin/setup-mongodb.sh"));
		Assertions.assertThat(script.exists()).isTrue();
		Charset charset = Charset.defaultCharset();
		String instructions = StreamUtils.copyToString(script.getInputStream(), charset);
		log.warn("Be sure MongoDB supports replicas. Try:\n\n" + instructions);
	}

	// <3>
	@Before
	public void configureCollectionsBeforeTests() {
		Mono<Boolean> createIfMissing = template.collectionExists(Order.class) //
				.filter(x -> !x) //
				.flatMap(exists -> template.createCollection(Order.class)) //
				.thenReturn(true);
		StepVerifier //
				.create(createIfMissing) //
				.expectNextCount(1) //
				.verifyComplete();
	}

	// <4>
	@Test
	public void createOrders() {

		Publisher<Order> orders = this.repository //
				.deleteAll() //
				.thenMany(this.service.createOrdersExecute("1", "2", "3")) //
				.thenMany(this.repository.findAll());

		StepVerifier //
				.create(orders) //
				.expectNextCount(3) //
				.verifyComplete();
	}

	// <5>
	@Test
	public void executeRollback() {
		this.runTransactionalTest(this.service.createOrdersExecute("1", "2", null));
	}

	// <5>
	@Test
	public void transactionalOperatorRollback() {
		this.runTransactionalTest(
				this.service.createOrdersTransactionalOperator("1", "2", null));
	}

	@Test
	public void transactionalRollback() {
		this.runTransactionalTest(this.service.createOrdersTransactional("1", "2", null));
	}

	private void runTransactionalTest(Flux<Order> ordersInTx) {
		Publisher<Order> orders = this.repository //
				.deleteAll() //
				.thenMany(ordersInTx) //
				.thenMany(this.repository.findAll());

		StepVerifier //
				.create(orders) //
				.expectNextCount(0) //
				.verifyError();

		StepVerifier //
				.create(this.repository.findAll()) //
				.expectNextCount(0) //
				.verifyComplete();
	}

}