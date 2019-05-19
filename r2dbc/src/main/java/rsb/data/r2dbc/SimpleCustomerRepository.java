package rsb.data.r2dbc;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SimpleCustomerRepository {

	Mono<Customer> save(Customer c);

	Mono<Customer> update(Customer c);

	Mono<Customer> findById(Integer id);

	Mono<Void> deleteById(Integer id);

	Flux<Customer> findAll();

}
