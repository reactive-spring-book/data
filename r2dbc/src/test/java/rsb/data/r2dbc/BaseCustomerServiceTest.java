package rsb.data.r2dbc;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

abstract public class BaseCustomerServiceTest {

	abstract public SimpleCustomerRepository getCustomerRepository();

	private SimpleCustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerDatabaseInitializer initializer;

	@Before
	public void reset() throws Exception {
		this.customerRepository = getCustomerRepository();
		StepVerifier.create(this.initializer.resetCustomerTable()).verifyComplete();
	}

	@Test
	public void badUpsert() throws Exception {
		var badEmail = "bad";
		var firstWrite = this.customerService.upsert(badEmail)
				.thenMany(this.customerRepository.findAll());
		StepVerifier.create(firstWrite).expectError().verify();
		StepVerifier.create(this.customerRepository.findAll()).expectNextCount(0)
				.verifyComplete();
	}

	@Test
	public void goodUpsert() throws Exception {

		var validEmail = "a@b.com";
		var firstWrite = this.customerService.upsert(validEmail)
				.thenMany(this.customerRepository.findAll()).log();

		StepVerifier.create(firstWrite).expectNextCount(1).verifyComplete();

		var secondWrite = this.customerService.upsert(validEmail)
				.thenMany(this.customerRepository.findAll());

		StepVerifier.create(secondWrite).expectNextCount(1).verifyComplete();

	}

	@Test
	public void resetDatabase() {

		var resetAndFind = this.customerRepository.save(new Customer(null, "a@b.com"))
				.thenMany(this.customerService.resetDatabase())
				.thenMany(this.customerRepository.findAll());
		StepVerifier.create(resetAndFind).expectNextCount(0).verifyComplete();

	}

	@Test
	public void normalizeEmails() throws Exception {

		var email = "a@b.com";
		StepVerifier.create(customerRepository.save(new Customer(null, email)))
				.expectNextCount(1).verifyComplete();
		StepVerifier.create(customerRepository.findAll()).expectNextCount(1)
				.verifyComplete();
		Flux<Customer> customerFlux = customerService.normalizeEmails();
		StepVerifier.create(customerFlux).expectNextCount(1).verifyComplete();

		StepVerifier.create(customerRepository.findAll())
				.expectNextMatches(
						c -> c.getEmail().toUpperCase().equals(email.toUpperCase()))
				.verifyComplete();
	}

}
