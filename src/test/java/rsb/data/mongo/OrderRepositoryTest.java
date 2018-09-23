package rsb.data.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Predicate;

@DataMongoTest
@RunWith(SpringRunner.class)
public class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Test
	public void writeAndRead() {

		Order saved1 = new Order(UUID.randomUUID().toString(), "1");
		Order saved2 = new Order(UUID.randomUUID().toString(), "2");
		Flux<Order> saveAll = this.orderRepository.saveAll(Flux.just(saved1, saved2));

		Predicate<Order> predicate = p -> p.getId().equalsIgnoreCase(saved1.getId())
				|| p.getId().equalsIgnoreCase(saved2.getId());

		StepVerifier //
				.create(saveAll) //
				.expectNextMatches(predicate) //
				.expectNextMatches(predicate) //
				.verifyComplete();

		StepVerifier.create(this.orderRepository.findAll()) //
				.expectNextMatches(predicate) //
				.expectNextMatches(predicate) //
				.verifyComplete();
	}

}