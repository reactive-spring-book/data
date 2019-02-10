package rsb.data.mongodb;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

interface OrderRepository extends ReactiveCrudRepository<Order, String> {

	Flux<Order> findByProductId(String productId);

}
