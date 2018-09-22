package rsb.data.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class MongoApplication {

	public static void main(String args[]) throws IOException {
		SpringApplication.run(MongoApplication.class, args);
	}
}

interface OrderRepository extends ReactiveMongoRepository<Order, String> {

	@Tailable
	Flux<Order> findByProductId(String pid);
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {

	@Id
	private String id;

	private String productId;
}