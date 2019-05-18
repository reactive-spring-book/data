package rsb.data.r2dbc.basics;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.SimpleCustomerRepository;

@Log4j2
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerServiceTest {

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Autowired
	private ReactiveTransactionManager transactionManager;

	@Test
	public void execute() {

		StepVerifier
				.create(this.customerRepository.findAll()
						.flatMap(cus -> this.customerRepository.deleteById(cus.getId()))
						.thenMany(this.customerRepository.findAll()))
				.expectNextCount(0).verifyComplete();

		var tm = TransactionalOperator.create(this.transactionManager);
		var data = tm.execute(
				status -> Flux.just(new Customer(null, "1"), new Customer(null, "2"))
						.flatMap(c -> customerRepository.save(c))
						.thenMany(Flux.error(new IllegalArgumentException())));

		StepVerifier.create(data).expectError().verify();

		StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
				.verifyComplete();
	}

}
