package rsb.data.mongodb;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@Testcontainers
@DataMongoTest // <1>
@Import({ TransactionConfiguration.class, OrderService.class })
public class OrderServiceTest {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.3");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	private OrderRepository repository;

	@Autowired
	private OrderService service;

	@Autowired
	private ReactiveMongoTemplate template;

	// <2>
	@BeforeEach
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

	// <3>
	@Test
	public void createOrders() {

		Publisher<Order> orders = this.repository //
				.deleteAll() //
				.thenMany(this.service.createOrders("1", "2", "3")) //
				.thenMany(this.repository.findAll());

		StepVerifier //
				.create(orders) //
				.expectNextCount(3) //
				.verifyComplete();
	}

	// <4>
	@Test
	public void transactionalOperatorRollback() {
		this.runTransactionalTest(this.service.createOrders("1", "2", null));
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