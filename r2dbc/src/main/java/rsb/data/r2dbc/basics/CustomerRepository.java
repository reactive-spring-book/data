package rsb.data.r2dbc.basics;

import io.r2dbc.spi.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.r2dbc.connectionfactory.ConnectionFactoryUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.SimpleCustomerRepository;

import java.util.function.BiFunction;

// <1>
@Log4j2
@Component
@RequiredArgsConstructor
class CustomerRepository implements SimpleCustomerRepository {

	private final ConnectionFactory connectionFactory;

	private Mono<Connection> connection() {
		return ConnectionFactoryUtils.getConnection(connectionFactory).map(Tuple2::getT1);
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		// <3>
		return connection().flatMapMany(connection -> connection
				.createStatement("DELETE FROM customer where id = $1").bind("$1", id) //
				.execute()).then();
	}

	@Override
	public Flux<Customer> findAll() {
		// <4>
		return connection().flatMapMany(connection -> Flux
				.from(connection.createStatement("select * from customer ").execute())
				.flatMap(result -> {

					BiFunction<Row, RowMetadata, Customer> mapper = // <3>
							(row, rowMetadata) -> new Customer(
									row.get("id", Integer.class),
									row.get("email", String.class));

					return result.map(mapper);

				}));
	}

	@Override
	public Mono<Customer> save(Customer c) {

		Flux<Result> mapMany = connection().flatMapMany(connection -> connection
				.createStatement("INSERT INTO customer(email) VALUES($1)")
				.bind("$1", c.getEmail()) //
				.returnGeneratedValues("id").execute());

		Flux<Customer> results = mapMany.flatMap(r -> r.map((row, rowMetadata) -> {
			var id = Integer.class.cast(row.get("id"));
			return new Customer(id, c.getEmail());
		}));
		return results.single();
	}

}
