package rsb.data.postgresql;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
class CustomerService {

	private final ConnectionFactory connectionFactory;

	CustomerService(PostgresqlConnectionFactory pgc) {
		this.connectionFactory = pgc;
	}

	Flux<Result> delete(Long id) {
		return Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("DELETE FROM customers where id = $1")
						.bind("$1", id) //
						.execute());
	}

	Flux<Result> create(Long id, String email) {
		return Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("INSERT INTO customers(id,email) VALUES($1, $2)")
						.bind("$1", id) //
						.bind("$2", email) //
						.add().execute());
	}

	Flux<Customer> all() {

		return Mono.from(this.connectionFactory.create()).flatMapMany(connection -> Flux
				.from(connection.createStatement("select * from customers").execute())
				.flatMap(result -> result
						.map((row, rowMetadata) -> new Customer(row.get("id", Long.class),
								row.get("email", String.class)))));
	}

}
