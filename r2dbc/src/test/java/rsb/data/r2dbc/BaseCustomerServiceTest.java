package rsb.data.r2dbc;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Log4j2
public abstract class BaseCustomerServiceTest {

	/*
	 * @Autowired private TransactionalOperator operator;
	 *
	 * protected abstract CustomerService getService();
	 *
	 * protected abstract SimpleCustomerRepository getRepository();
	 *
	 * private final String[] emails = "a@a.com,b@b.com,foo".split(",");
	 *
	 * @Test public void executeRollback() { var customers = this.operator .execute(status
	 * -> core(getService().saveAllEmailsExecute(emails)));
	 *
	 * this.runTransactionalTest(customers); }
	 *
	 * @Test public void transactionalOperatorRollback() { var customers = core(
	 * this.getService().saveAllEmailsTransactionalOperator(this.emails));
	 * this.runTransactionalTest(customers); }
	 *
	 * @Test public void transactionalRollback() { var customers =
	 * core(this.getService().saveAllEmailsTransactional(this.emails));
	 * this.runTransactionalTest(customers); }
	 *
	 * Flux<Customer> core(Flux<Customer> queryResults) { Flux<Customer> customerFlux =
	 * this.getRepository() // .findAll()// .flatMap(c ->
	 * this.getRepository().deleteById(c.getId())) // .thenMany(queryResults) //
	 * .thenMany(this.getRepository().findAll()).log();
	 *
	 * return customerFlux; }
	 *
	 * private void runTransactionalTest(Flux<Customer> core) {
	 *
	 * // Flux<Customer> core = core(customers);
	 *
	 * StepVerifier // .create(core) // .expectNextCount(0) // .verifyError();
	 *
	 * StepVerifier // .create(this.getRepository().findAll()) // .expectNextCount(0) //
	 * .verifyComplete(); }
	 */

}