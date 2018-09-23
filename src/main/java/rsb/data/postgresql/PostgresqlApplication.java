package rsb.data.postgresql;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URI;

@Log4j2
@SpringBootApplication
public class PostgresqlApplication {

	public static void main(String args[]) {
		SpringApplication.run(PostgresqlApplication.class, args);
	}

	@Bean
	PostgresqlConnectionFactory connectionFactory(
			@Value("${spring.datasource.url}") String url) {

		URI uri = URI.create(url);
		String host = uri.getHost();
		String user = uri.getUserInfo().split(":")[0];
		String pw = uri.getUserInfo().split(":")[1];
		String name = uri.getPath().substring(1);
		PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration
				.builder() //
				.database(name) //
				.host(host) //
				.username(user) //
				.password(pw) //
				.build();
		return new PostgresqlConnectionFactory(configuration);
	}

}