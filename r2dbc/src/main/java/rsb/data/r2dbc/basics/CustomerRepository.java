package rsb.data.r2dbc.basics;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.SimpleCustomerRepository;

import java.util.function.BiFunction;

@Repository // <1>
@Log4j2
@Component
@RequiredArgsConstructor
class CustomerRepository implements SimpleCustomerRepository {

	// <2>
	private final ConnectionManager connectionManager;

	private final BiFunction<Row, RowMetadata, Customer> mapper = // <3>
		(row, rowMetadata) -> new Customer(row.get("id", Integer.class),
			row.get("email", String.class));

	@Override
	public Mono<Customer> update(Customer customer) {
		// <4>
		return connectionManager.inConnection(conn -> Flux
				.from(conn.createStatement("update customer set email = $1 where id = $2") //
						.bind("$1", customer.getEmail()) //
						.bind("$2", customer.getId()) //
						.execute()))
				.then(findById(customer.getId()));
	}

	@Override
	public Mono<Customer> findById(Integer id) {
		return connectionManager
				.inConnection(conn -> Flux
						.from(conn.createStatement("select * from customer where id = $1")
								.bind("$1", id).execute()))
				.flatMap(result -> result.map(this.mapper))//
				.single()//
				.log();
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		return connectionManager.inConnection(conn -> Flux
				.from(conn.createStatement("DELETE FROM customer where id = $1") //
						.bind("$1", id) //
						.execute()) //
		) //
				.then();
	}

	@Override
	public Flux<Customer> findAll() {
		return connectionManager.inConnection(connection -> Flux
				.from(connection.createStatement("select * from customer ").execute())
				.flatMap(result -> result.map(mapper)));
	}

	@Override
	public Mono<Customer> save(Customer c) {

		return connectionManager
				.inConnection(
						conn -> Flux
								.from(conn
										.createStatement(
												"INSERT INTO customer(email) VALUES($1)")
										.bind("$1", c.getEmail()) //
										.returnGeneratedValues("id").execute())
								.flatMap(r -> r.map((row, rowMetadata) -> {
									var id = row.get("id", Integer.class);
									return new Customer(id, c.getEmail());
								}))) //
				.single() //
				.log();
	}

}
