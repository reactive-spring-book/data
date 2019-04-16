package rsb.data.mongodb;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

//<1>
interface OrderRepository extends ReactiveCrudRepository<Order, String> {

	Flux<Order> findByProductId(String productId);

}
