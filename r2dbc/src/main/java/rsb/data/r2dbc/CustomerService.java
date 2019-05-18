package rsb.data.r2dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final SimpleCustomerRepository repository;

	private final TransactionalOperator operator;

	@Transactional
	public Flux<Customer> execute(Flux<Customer> customerFlux) {
		return customerFlux;
	}

}
