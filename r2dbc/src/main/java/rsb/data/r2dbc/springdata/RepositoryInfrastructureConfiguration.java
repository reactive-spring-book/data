package rsb.data.r2dbc.springdata;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

@Log4j2
@Configuration
class RepositoryInfrastructureConfiguration {

	// <1>
	@Bean
	CustomerRepository customerRepository(R2dbcRepositoryFactory factory) {
		return factory.getRepository(CustomerRepository.class);
	}

	// <2>
	@Bean
	R2dbcRepositoryFactory r2dbcRepositoryFactory(DatabaseClient client) {
		RelationalMappingContext context = new RelationalMappingContext();
		context.afterPropertiesSet();
		return new R2dbcRepositoryFactory(client, context);
	}

	// <3> this is the equivalent of the `JdbcTemplate`
	@Bean
	DatabaseClient databaseClient(ConnectionFactory factory) {
		return DatabaseClient.builder().connectionFactory(factory).build();
	}

}
