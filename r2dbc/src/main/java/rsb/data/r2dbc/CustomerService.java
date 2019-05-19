package rsb.data.r2dbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomerService {

	private final SimpleCustomerRepository repository;

	private final TransactionalOperator operator;

	private final CustomerDatabaseInitializer initializer;

	public Publisher<Void> resetDatabase() {
		return this.initializer.resetCustomerTable();
	}

	public Flux<Customer> upsert(String email) {
		var query = errorIfEmailsAreInvalid(this.repository.findAll()
				.filter(customer -> customer.getEmail().equalsIgnoreCase(email))
				.flatMap(match -> this.repository
						.update(new Customer(match.getId(), email)))
				.switchIfEmpty(this.repository.save(new Customer(null, email))));
		return this.operator.execute(status -> query);
	}

	@Transactional
	public Flux<Customer> normalizeEmails() {
		return errorIfEmailsAreInvalid(this.repository.findAll()
				.flatMap(x -> this.upsert(x.getEmail().toUpperCase())));
	}

	private static Flux<Customer> errorIfEmailsAreInvalid(Flux<Customer> input) {
		return input.log().filter(c -> c.getEmail().contains("@"))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(
						"the email needs to be of the form a@b.com!")));

	}

}
