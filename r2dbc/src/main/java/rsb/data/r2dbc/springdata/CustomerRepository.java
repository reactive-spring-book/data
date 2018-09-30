package rsb.data.r2dbc.springdata;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

	@Query("select id, email from customer c where c.email = $1")
	Flux<Customer> findByEmail(String email);

}
