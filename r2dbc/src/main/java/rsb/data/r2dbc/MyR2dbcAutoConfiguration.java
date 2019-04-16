package rsb.data.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.function.DatabaseClient;

import java.net.URI;

@Log4j2
@Configuration
public class MyR2dbcAutoConfiguration {

	// <1>
	@Bean
	@ConditionalOnMissingBean(ConnectionFactory.class)
	PostgresqlConnectionFactory connectionFactory(
			@Value("${spring.datasource.url}") String url) {
		// <2>

		URI uri = URI.create(url);
		String host = uri.getHost();
		String userInfo = uri.getUserInfo();
		String user = userInfo, pw = "";

		if (userInfo.contains(":")) {
			user = userInfo.split(":")[0];
			pw = userInfo.split(":")[1];
		}

		String name = uri.getPath().substring(1);

		// <3>
		PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration
				.builder() //
				.database(name) //
				.host(host) //
				.username(user) //
				.password(pw) //
				.build();

		return new PostgresqlConnectionFactory(configuration);
	}

	// <4>
	@Bean
	@ConditionalOnMissingBean
	DatabaseClient databaseClient(ConnectionFactory cf) {
		return DatabaseClient.create(cf);
	}

}
