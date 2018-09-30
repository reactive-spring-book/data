package rsb.data.r2dbc.basics;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
class CustomerRepository {

	private final ConnectionFactory connectionFactory;

	CustomerRepository(PostgresqlConnectionFactory pgc) {
		this.connectionFactory = pgc;
	}

	Mono<Void> deleteById(Integer id) {
		return Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("DELETE FROM customer  where id = $1")
						.bind("$1", id) //
						.execute())
				.then();
	}

	Flux<Customer> save(Customer c) {
		Flux<Result> mapMany = Mono.from(this.connectionFactory.create())
				.flatMapMany(connection -> connection
						.createStatement("INSERT INTO customer(email) VALUES($1)")
						.bind("$1", c.getEmail()) //
						.add().execute());
		return mapMany
				.switchMap(result -> Flux.just(new Customer(c.getId(), c.getEmail())));
	}

	Flux<Customer> findAll() {

		return Mono.from(this.connectionFactory.create()).flatMapMany(connection -> Flux
				.from(connection.createStatement("select * from customer ").execute())
				.flatMap(result -> result.map((row, rowMetadata) -> new Customer(
						row.get("id", Integer.class), row.get("email", String.class)))));
	}

}
