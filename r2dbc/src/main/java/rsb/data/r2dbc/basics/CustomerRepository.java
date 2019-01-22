package rsb.data.r2dbc.basics;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.SimpleCustomerRepository;

import java.util.function.BiFunction;

// <1>
@Log4j2
@Service
class CustomerRepository implements SimpleCustomerRepository {

	private final ConnectionFactory connectionFactory;

	// <2>
	CustomerRepository(PostgresqlConnectionFactory pgc) {
		this.connectionFactory = pgc;
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		// <3>
		return Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("DELETE FROM customer where id = $1")
						.bind("$1", id) //
						.execute())
				.then();
	}

	@Override
	public Flux<Customer> findAll() {
		// <4>
		return Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> Flux.from(
						connection.createStatement("select * from customer ").execute())
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

		// TODO https://github.com/r2dbc/r2dbc-mssql/issues/17
		// Statement.returnGeneratedKeys is available in R2DBC M7

		Flux<Result> mapMany = Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("INSERT INTO customer(email) VALUES($1)")
						.bind("$1", c.getEmail()) //
						.execute());

		return mapMany.then(Mono.just(new Customer(c.getId(), c.getEmail())));
	}

}
