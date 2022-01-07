package rsb.data.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

	@Tailable // <1>
	Flux<Customer> findByName(String name);

}
