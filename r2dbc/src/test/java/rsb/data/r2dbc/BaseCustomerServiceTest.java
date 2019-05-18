package rsb.data.r2dbc;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

abstract public class BaseCustomerServiceTest {

	abstract public SimpleCustomerRepository getCustomerRepository();

	private SimpleCustomerRepository customerRepository;

	@Autowired
	private ReactiveTransactionManager transactionManager;

	private TransactionalOperator transactionalOperator;

	@Autowired
	private CustomerService customerService;

	@Before
	public void reset() {

		this.customerRepository = getCustomerRepository();

		this.transactionalOperator = TransactionalOperator
				.create(this.transactionManager);

		StepVerifier
				.create(this.customerRepository.findAll()
						.flatMap(cus -> this.customerRepository.deleteById(cus.getId()))
						.thenMany(this.customerRepository.findAll()))
				.expectNextCount(0).verifyComplete();
	}

	@Test
	public void transactional() {

		var data = this.customerService
				.transactional(Flux.just(new Customer(null, "1"), new Customer(null, "2"))
						.flatMap(c -> customerRepository.save(c))
						.thenMany(Flux.error(new IllegalArgumentException())));

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
				.verifyComplete();
	}

	@Test
	public void execute() {

		var data = customerService
				.execute(Flux.just(new Customer(null, "1"), new Customer(null, "2"))
						.flatMap(c -> customerRepository.save(c))
						.thenMany(Flux.error(new IllegalArgumentException())));

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
				.verifyComplete();
	}

	@Test
	public void operator() {

		var data = customerService
				.operator(Flux.just(new Customer(null, "1"), new Customer(null, "2"))
						.flatMap(c -> customerRepository.save(c))
						.thenMany(Flux.error(new IllegalArgumentException())));

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
				.verifyComplete();
	}

}
