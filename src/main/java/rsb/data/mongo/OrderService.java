package rsb.data.mongo;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class OrderService {

	public final static String BOOM_EXCEPTION = "boom";

	private final ReactiveMongoTemplate template;

	OrderService(ReactiveMongoTemplate template) {
		this.template = template;
	}

	public Flux<Order> createOrders(String... productIds) {
		Flux<Order> orders = Flux.just(productIds).map(x -> new Order(null, x))
				.flatMap(order -> {
					if (order.getProductId().equalsIgnoreCase(BOOM_EXCEPTION)) {
						throw new IllegalArgumentException(BOOM_EXCEPTION);
					}
					return template.insert(order);
				});
		return this.template.inTransaction().execute(template -> orders);
	}

}
