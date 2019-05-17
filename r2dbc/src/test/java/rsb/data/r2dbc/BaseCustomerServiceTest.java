package rsb.data.r2dbc;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Log4j2
public abstract class BaseCustomerServiceTest {

	protected abstract CustomerService getService();

	protected abstract SimpleCustomerRepository getRepository();

	private final String[] emails = "a@a.com,b@b.com,foo".split(",");

	@Test
	public void executeRollback() {
		var customers = this.getService().saveAllEmailsExecute(this.emails);
		this.runTransactionalTest(customers);
	}

	@Test
	public void transactionalOperatorRollback() {
		var customers = this.getService().saveAllEmailsTransactionalOperator(this.emails);
		this.runTransactionalTest(customers);
	}

	@Test
	public void transactionalRollback() {
		var customers = this.getService().saveAllEmailsTransactional(this.emails);
		this.runTransactionalTest(customers);
	}

	private void runTransactionalTest(Flux<Customer> customers) {
		Publisher<Customer> orders = this.getRepository() //
				.findAll().flatMap(c -> this.getRepository().deleteById(c.getId())) //
				.thenMany(customers) //
				.thenMany(this.getRepository().findAll());

		StepVerifier //
				.create(orders) //
				.expectNextCount(0) //
				.verifyError();

		StepVerifier //
				.create(this.getRepository().findAll()) //
				.expectNextCount(0) //
				.verifyComplete();
	}

}