package rsb.data.postgresql;

import io.r2dbc.postgresql.PostgresqlConnection;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Log4j2
@Service
class CustomerService {

	private final String url;

	CustomerService(@Value("${spring.datasource.url}") String url) {
		this.url = url;
	}

	Flux<Customer> all() {
		return this.connection()
				.flatMapMany(connection -> connection
						.createStatement("select * from customers").execute()
						.flatMap(result -> result.map((row, rowMetadata) -> new Customer(
								row.get("id", Long.class),
								row.get("email", String.class)))));
	}

	private Mono<PostgresqlConnection> connection() {
		URI uri = URI.create(url);
		String host = uri.getHost();
		String user = uri.getUserInfo().split(":")[0];
		String pw = uri.getUserInfo().split(":")[1];
		String name = uri.getPath().substring(1);
		PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration
				.builder().database(name).host(host).username(user).password(pw).build();
		return new PostgresqlConnectionFactory(configuration).create();
	}

}
