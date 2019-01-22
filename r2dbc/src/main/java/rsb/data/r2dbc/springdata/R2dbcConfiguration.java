package rsb.data.r2dbc.springdata;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

// <1>
@Configuration
@EnableR2dbcRepositories
class R2dbcConfiguration extends AbstractR2dbcConfiguration {

	private final ConnectionFactory connectionFactory;

	// <2>
	R2dbcConfiguration(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	// <3>
	@Override
	public ConnectionFactory connectionFactory() {
		return this.connectionFactory;
	}

}