package rsb.data.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@DataMongoTest
@RunWith(SpringRunner.class)
public class TailableCustomerQueryTest {

	@Autowired
	private ReactiveMongoTemplate operations;

	@Autowired
	private CustomerRepository repository;

	@Before
	public void before() {

		CollectionOptions capped = CollectionOptions.empty().size(1024 * 1024)
				.maxDocuments(100).capped();

		Mono<MongoCollection<Document>> recreateCollection = operations
				.collectionExists(Order.class)
				.flatMap(exists -> exists ? operations.dropCollection(Customer.class)
						: Mono.just(exists))
				.then(operations.createCollection(Customer.class, capped));

		StepVerifier.create(recreateCollection).expectNextCount(1).verifyComplete();
	}

	@Test
	public void tail() {
		Queue<Customer> people = new ConcurrentLinkedQueue<>();
		Mono<Customer> then = this.writeAndWait().then(this.writeAndWait());
		StepVerifier.create(then).expectNextCount(1).verifyComplete();
		this.repository.findByName("1") //
				.doOnNext(people::add) //
				.doOnComplete(() -> log.info("complete")) //
				.doOnTerminate(() -> log.info("terminated")) //
				.subscribe();
		Mono<Customer> thenAgain = this.writeAndWait().then(this.writeAndWait());
		StepVerifier.create(thenAgain).expectNextCount(1).verifyComplete();
		Assertions.assertThat(people).hasSize(4);
	}

	private Mono<Customer> writeAndWait() {
		return repository.save(new Customer(UUID.randomUUID().toString(), "1"));
	}

}
