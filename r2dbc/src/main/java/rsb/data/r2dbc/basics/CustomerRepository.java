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
	public Mono<Customer> update(Customer customer) {
		Mono<Connection> connection = connection();
		Flux<? extends Result> flatMapMany = connection.flatMapMany(conn -> conn
				.createStatement("update customer  set email = $1 where id = $2")
				.bind("$1", customer.getEmail()).bind("$2", customer.getId()).execute());
		return flatMapMany.then(findById(customer.getId()));
	}

	private Mono<Customer> doFindById(Mono<Connection> connection, Integer id) {
		return connection
				.flatMapMany(conn -> conn
						.createStatement("select * from customer where id = $1")
						.bind("$1", id).execute())
				.flatMap(result -> result.map(this.mapper)).single().log();
	}

	@Override
	public Mono<Customer> findById(Integer id) {
		return doFindById(connection(), id);
	}

	private final BiFunction<Row, RowMetadata, Customer> mapper = // <3>
			(row, rowMetadata) -> new Customer(row.get("id", Integer.class),
					row.get("email", String.class));

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
			var customer = new Customer(id, c.getEmail());
			if (log.isDebugEnabled()) {
				log.debug("the id is " + customer.getId() + " and the email is "
						+ customer.getEmail());
			}
			return customer;
		}));
		return results.single().log();
	}

}
