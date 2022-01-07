package rsb.data.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
class OrderService {

	private final ReactiveMongoTemplate template;

	private final TransactionalOperator operator;

	// <1>
	public Flux<Order> createOrders(String... productIds) {
		return this.operator.execute(status -> buildOrderFlux(template::insert, productIds));
	}

	private Flux<Order> buildOrderFlux(Function<Order, Mono<Order>> callback, String[] productIds) {
		return Flux //
				.just(productIds) //
				.map(pid -> {
					Assert.notNull(pid, "the product ID shouldn't be null");
					return pid;
				}) //
				.map(x -> new Order(null, x)) //
				.flatMap(callback);
	}

}
