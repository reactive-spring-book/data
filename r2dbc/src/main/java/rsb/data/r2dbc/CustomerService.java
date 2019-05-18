package rsb.data.r2dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final SimpleCustomerRepository repository;

	private final TransactionalOperator operator;

	/*
	 * @Transactional public Flux<Customer> transactional(Flux<Customer> customerFlux) {
	 * return customerFlux; }
	 *
	 * public Flux<Customer> execute(Flux<Customer> customerFlux) { return
	 * this.operator.execute(status -> customerFlux); }
	 */

	public Flux<Customer> upsert(String email) {
		var query = errorIfEmailsAreInvalid(this.repository.findAll()
				.filter(p -> p.getEmail().equalsIgnoreCase(email))
				.switchIfEmpty(this.repository.save(new Customer(null, email))));
		return this.operator.execute(status -> query);
	}

	@Transactional
	public Flux<Customer> normalizeEmails() {
		return errorIfEmailsAreInvalid(this.repository.findAll()
				.map(c -> new Customer(c.getId(), c.getEmail().toUpperCase()))
				.flatMap(this.repository::save));

	}

	private static Flux<Customer> errorIfEmailsAreInvalid(Flux<Customer> input) {
		return input.filter(c -> c.getEmail().contains("@"))
				.switchIfEmpty(Mono.error(new IllegalArgumentException(
						"the email needs to be of the form a@b.com!")));
	}

}
