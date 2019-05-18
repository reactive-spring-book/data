package rsb.data.r2dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final SimpleCustomerRepository repository;

	private final TransactionalOperator operator;

	@Transactional
	public Flux<Customer> saveAllEmailsTransactional(String... emails) {
		return this.doSaveAllEmails(emails);
	}

	public Flux<Customer> saveAllEmailsTransactionalOperator(String... emails) {
		return this.doSaveAllEmails(emails) //
				.as(this.operator::transactional);
	}

	public Flux<Customer> saveAllEmailsExecute(String... emails) {
		return this.doSaveAllEmails(emails);
	}

	private Flux<Customer> doSaveAllEmails(String... emails) {
		Assert.isTrue(emails.length > 0, "you must provide more than one email");
		return Flux //
				.just(emails) //
				.map(c -> { //
					var good = (c != null && c.contains("@"));
					Assert.isTrue(good, "you must provide a valid, non-null email");
					return (c);
				})//
				.map(e -> new Customer(null, e))//
				.flatMap(this.repository::save);
	}

}
