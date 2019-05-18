package rsb.data.r2dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final TransactionalOperator operator;

	@Transactional
	public Flux<Customer> transactional(Flux<Customer> customerFlux) {
		return customerFlux;
	}

	public Flux<Customer> execute(Flux<Customer> customerFlux) {
		return this.operator.execute(status -> customerFlux);
	}

	public Flux<Customer> operator(Flux<Customer> customerFlux) {
		return customerFlux.as(this.operator::transactional);
	}

}
