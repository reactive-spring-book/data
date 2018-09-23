package rsb.data.postgresql;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlResult;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Log4j2
@Service
class CustomerService {

	private final PostgresqlConnectionFactory connectionFactory;

	CustomerService(PostgresqlConnectionFactory pgc) {
		this.connectionFactory = pgc;
	}

	Flux<PostgresqlResult> create(Long id, String email) {
		return this.connectionFactory.create()
				.flatMapMany(connection -> connection
						.createStatement("INSERT INTO customers(id,email) VALUES($1, $2)")
						.bind("$1", id).bind("$2", email).add().execute());
	}

	Flux<Customer> all() {
		return this.connectionFactory.create()
				.flatMapMany(connection -> connection
						.createStatement("select * from customers").execute()
						.flatMap(result -> result.map((row, rowMetadata) -> new Customer(
								row.get("id", Long.class),
								row.get("email", String.class)))));
	}

}
