package rsb.data.r2dbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

	private final SimpleCustomerRepository repository;

	private final TransactionalOperator operator;

	private final CustomerDatabaseInitializer initializer;

	public Publisher<Void> resetDatabase() {
		return this.initializer.resetCustomerTable();
	}

	// <1>
	public Flux<Customer> upsert(String email) {
		var customers = this.repository//
				.findAll() //
				.filter(customer -> customer.email().equalsIgnoreCase(email)) //
				.flatMap(match -> this.repository.update(new Customer(match.id(), email))) //
				.switchIfEmpty(this.repository.save(new Customer(null, email)));//
		var validatedResults = errorIfEmailsAreInvalid(customers);
		return this.operator.transactional(validatedResults);
	}

	// <2>
	@Transactional
	public Flux<Customer> normalizeEmails() {
		return errorIfEmailsAreInvalid(this.repository.findAll().flatMap(x -> this.upsert(x.email().toUpperCase())));
	}

	private static Flux<Customer> errorIfEmailsAreInvalid(Flux<Customer> input) {
		return input.filter(c -> c.email().contains("@")) //
				.switchIfEmpty(Mono.error(new IllegalArgumentException("the email needs to be of the form a@b.com!")));
	}

}
