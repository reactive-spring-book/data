package rsb.data.mongodb;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

@Service
class OrderService {

	private final ReactiveMongoTemplate template;

	OrderService(ReactiveMongoTemplate template) {
		this.template = template;
	}

	// todo https://spring.io/blog/2019/05/16/reactive-transactions-with-spring
	// @Transactional
	public Flux<Order> createOrders(String... productIds) {
		return this.template.inTransaction() // <1>
				.execute(txTemplate -> Flux.just(productIds).map(pid -> {
					Assert.notNull(pid, "the processId can't be null"); // <2>
					return pid;
				}).map(x -> new Order(null, x)).flatMap(txTemplate::insert) // <3>
		);
	}

}
