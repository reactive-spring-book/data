package rsb.data.r2dbc;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

abstract public class BaseCustomerServiceTest {

	abstract public SimpleCustomerRepository getCustomerRepository();

	private SimpleCustomerRepository customerRepository;

	@Autowired
	private ReactiveTransactionManager transactionManager;

	private TransactionalOperator transactionalOperator;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerDatabaseInitializer initializer;

	@Before
	public void reset() throws Exception {

		this.customerRepository = getCustomerRepository();

		Publisher<Void> voidPublisher = this.initializer.resetCustomerTable();
		StepVerifier.create(voidPublisher).verifyComplete();
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
				.thenMany(this.customerRepository.findAll());

		StepVerifier.create(firstWrite).expectNextCount(1).verifyComplete();

		var secondWrite = this.customerService.upsert(validEmail)
				.thenMany(this.customerRepository.findAll());

		StepVerifier.create(secondWrite).expectNextCount(1).verifyComplete();

	}

	/*
	 *
	 * @Before public void resetCustomerTable() {
	 *
	 * this.customerRepository = getCustomerRepository();
	 *
	 * this.transactionalOperator = TransactionalOperator
	 * .create(this.transactionManager);
	 *
	 * StepVerifier .create(this.customerRepository.findAll() .flatMap(cus ->
	 * this.customerRepository.deleteById(cus.getId()))
	 * .thenMany(this.customerRepository.findAll())) .expectNextCount(0).verifyComplete();
	 * }
	 *
	 * @Test public void transactional() {
	 *
	 * var data = this.customerService .transactional(Flux.just(new Customer(null, "1"),
	 * new Customer(null, "2")) .flatMap(c -> customerRepository.save(c))
	 * .thenMany(Flux.error(new IllegalArgumentException())));
	 *
	 * StepVerifier.create(data).expectError().verify();
	 *
	 * StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
	 * .verifyComplete(); }
	 *
	 * @Test public void execute() {
	 *
	 * var data = customerService .execute(Flux.just(new Customer(null, "1"), new
	 * Customer(null, "2")) .flatMap(c -> customerRepository.save(c))
	 * .thenMany(Flux.error(new IllegalArgumentException())));
	 *
	 * StepVerifier.create(data).expectError().verify();
	 *
	 * StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
	 * .verifyComplete(); }
	 *
	 * @Test public void operator() {
	 *
	 * var data = customerService .operator(Flux.just(new Customer(null, "1"), new
	 * Customer(null, "2")) .flatMap(c -> customerRepository.save(c))
	 * .thenMany(Flux.error(new IllegalArgumentException())));
	 *
	 * StepVerifier.create(data).expectError().verify();
	 *
	 * StepVerifier.create(customerRepository.findAll()).expectNextCount(0)
	 * .verifyComplete(); }
	 */

}
