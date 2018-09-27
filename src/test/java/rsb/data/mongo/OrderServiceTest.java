package rsb.data.mongo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RunWith(SpringRunner.class)
@DataMongoTest
@Import(OrderService.class)
public class OrderServiceTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Before
	public void before() {
		StepVerifier.create(this.reactiveMongoTemplate.dropCollection(Order.class))
				.verifyComplete();
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