package rsb.data.r2dbc.basics;

import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.CustomerService;
import rsb.data.r2dbc.SimpleCustomerRepository;

@Log4j2
@SpringBootTest
@EnableTransactionManagement
@RunWith(SpringRunner.class)
public class CustomerServiceTest {

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Autowired
	private ReactiveTransactionManager transactionManager;

	private TransactionalOperator transactionalOperator;

	@Autowired
	private CustomerService customerService;

	@Before
	public void reset() {

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
			.execute(Flux.just(new Customer(null, "1"), new Customer(null, "2"))
				.flatMap(c -> customerRepository.save(c))
				.thenMany(Flux.error(new IllegalArgumentException())));

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
			.verifyComplete();
	}

	@Test
	public void execute() {

		var data = transactionalOperator.execute(
			status -> Flux.just(new Customer(null, "1"), new Customer(null, "2"))
				.flatMap(c -> customerRepository.save(c))
				.thenMany(Flux.error(new IllegalArgumentException())));

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
			.verifyComplete();
	}

	@Test
	public void operator() {

		var data = Flux.just(new Customer(null, "1"), new Customer(null, "2"))
			.flatMap(c -> customerRepository.save(c))
			.thenMany(Flux.error(new IllegalArgumentException()))
			.as(transactionalOperator::transactional);

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
			.verifyComplete();
	}

}
