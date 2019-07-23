package rsb.data.mongodb;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

@DataMongoTest
@RunWith(SpringRunner.class)
public class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	private final Collection<Order> orders = Arrays.asList(
			new Order(UUID.randomUUID().toString(), "1"),
			new Order(UUID.randomUUID().toString(), "2"),
			new Order(UUID.randomUUID().toString(), "2"));

	private final Predicate<Order> predicate = order -> //
	this.orders //
			.stream() //
			.filter(candidateOrder -> candidateOrder.getId()
					.equalsIgnoreCase(order.getId()))//
			.anyMatch(candidateOrder -> candidateOrder.getProductId()
					.equalsIgnoreCase(order.getProductId()));

	@Before
	public void before() {

		Flux<Order> saveAll = this.orderRepository.deleteAll()
				.thenMany(this.orderRepository.saveAll(this.orders));

		StepVerifier // <1>
				.create(saveAll) //
				.expectNextMatches(this.predicate) //
				.expectNextMatches(this.predicate) //
				.expectNextMatches(this.predicate) //
				.verifyComplete();
	}

	@Test
	public void findAll() {
		StepVerifier // <2>
				.create(this.orderRepository.findAll()) //
				.expectNextMatches(this.predicate) //
				.expectNextMatches(this.predicate) //
				.expectNextMatches(this.predicate) //
				.verifyComplete();
	}

	@Test
	public void findByProductId() {
		StepVerifier // <3>
				.create(this.orderRepository.findByProductId("2")) //
				.expectNextCount(2) //
				.verifyComplete();
	}

}