package rsb.data.r2dbc.basics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Exercises the hand-rolled repository.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository repo;

	@Test
	public void all() {

		Flux<Void> deleteEverything = this.repo.findAll()
				.flatMap(customer -> repo.deleteById(customer.getId()));

		StepVerifier.create(deleteEverything).expectNextCount(0).verifyComplete();

		Flux<Customer> insert = Flux.just(new Customer("first@email.com"),
				new Customer("second@email.com"), new Customer("third@email.com"))
				.flatMap(c -> this.repo.save(c));

		StepVerifier.create(insert).expectNextCount(3).verifyComplete();

		Flux<Customer> all = this.repo.findAll();

		StepVerifier.create(all).expectNextCount(3).verifyComplete();
	}

}