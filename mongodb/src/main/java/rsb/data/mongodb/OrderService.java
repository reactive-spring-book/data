package rsb.data.mongodb;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
class OrderService {

	private final ReactiveMongoTemplate template;

	OrderService(ReactiveMongoTemplate template) {
		this.template = template;
	}

	// todo https://spring.io/blog/2019/05/16/reactive-transactions-with-spring
	// todo @Transactional
	// todo ReactiveTransactionManager
	public Flux<Order> createOrdersInTransaction(String... productIds) {
		return this.template.inTransaction()
				.execute(txTemplate -> buildOrderFlux(txTemplate::insert, productIds));
	}

	@Transactional
	public Flux<Order> createOrdersTransactional(String... productIds) {
		return this.buildOrderFlux(this.template::save, productIds);
	}

	private Flux<Order> buildOrderFlux(Function<Order, Mono<Order>> callback,
			String[] productIds) {
		return Flux //
				.just(productIds) //
				.map(pid -> {
					Assert.notNull(pid, "the processId can't be null");
					return pid;
				}) //
				.map(x -> new Order(null, x)) //
				.flatMap(callback::apply);
	}

}
