package rsb.data.postgresql;

import io.r2dbc.postgresql.PostgresqlConnection;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;


@Log4j2
@SpringBootApplication
public class PostgresqlApplication {

	public static void main(String args[]) {
		SpringApplication.run(PostgresqlApplication.class, args);
	}

	private final String url;

	PostgresqlApplication(@Value("${spring.datasource.url}") String url) {
		this.url = url;
	}


	@PostConstruct
	public void fetchAllCustomers() {
		this.connection()
			.map(connection -> connection
				.createStatement("select * from customers")
				.execute()
				.flatMap(result -> result.map((row, rowMetadata) -> new Customer(row.get("id", Long.class), row.get("email", String.class))))
				.subscribe(customer -> log.info("customer: " + customer.getId() + " " + customer.getEmail()))
			)
			.block();
	}

	private Mono<PostgresqlConnection> connection() {
		URI uri = URI.create(url);
		String host = uri.getHost();
		String user = uri.getUserInfo().split(":")[0];
		String pw = uri.getUserInfo().split(":")[1];
		String name = uri.getPath().substring(1);
		PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration
			.builder()
			.database(name)
			.host(host)
			.username(user)
			.password(pw)
			.build();
		return new PostgresqlConnectionFactory(configuration)
			.create();
	}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
	private Long id;
	private String email;
}