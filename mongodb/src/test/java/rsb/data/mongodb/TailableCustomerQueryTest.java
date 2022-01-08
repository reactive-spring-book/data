package rsb.data.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Testcontainers
@DataMongoTest
public class TailableCustomerQueryTest {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.3");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	private ReactiveMongoTemplate operations;

	@Autowired
	private CustomerRepository repository;

	@BeforeEach
	public void before() {

		// <1>
		CollectionOptions capped = CollectionOptions.empty().size(1024 * 1024).maxDocuments(100).capped();

		Mono<MongoCollection<Document>> recreateCollection = operations.collectionExists(Order.class)
				.flatMap(exists -> exists ? operations.dropCollection(Customer.class) : Mono.just(exists))
				.then(operations.createCollection(Customer.class, capped));

		StepVerifier.create(recreateCollection).expectNextCount(1).verifyComplete();
	}

	@Test
	public void tail() throws InterruptedException {
		// <2>
		var people = new ConcurrentLinkedQueue<Customer>();

		// <3>
		StepVerifier //
				.create(this.write().then(this.write())) //
				.expectNextCount(1) //
				.verifyComplete();

		// <4>
		this.repository.findByName("1") //
				.doOnNext(people::add) //
				.doOnComplete(() -> log.info("complete")) //
				.doOnTerminate(() -> log.info("terminated")) //
				.subscribe();

		Assertions.assertThat(people).hasSize(2);

		// <5>
		StepVerifier.create(this.write().then(this.write())) //
				.expectNextCount(1) //
				.verifyComplete(); //

		// <6>
		Thread.sleep(1000);
		Assertions.assertThat(people).hasSize(4);
	}

	private Mono<Customer> write() {
		return repository.save(new Customer(UUID.randomUUID().toString(), "1"));
	}

}
