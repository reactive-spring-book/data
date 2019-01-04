package rsb.data.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SimpleCustomerRepository {

	Mono<Void> deleteById(Integer id);

	Mono<Customer> save(Customer c);

	Flux<Customer> findAll();

}
