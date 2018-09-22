package rsb.data.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

interface OrderRepository extends ReactiveMongoRepository<Order, String> {

	@Tailable
	Flux<Order> findByProductId(String pid);

}
