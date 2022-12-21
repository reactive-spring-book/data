package rsb.data.r2dbc.springdata;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import rsb.data.r2dbc.Customer;

// <1>
interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

	// <2>
	@Query("select id, email from customer c where c.email = $1")
	Flux<Customer> findByEmail(String email);

}
