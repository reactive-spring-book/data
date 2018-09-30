package rsb.data.r2dbc.springdata;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@Configuration
class RepositoryInfrastructureConfiguration {

	@Bean
	CustomerRepository customerRepository(R2dbcRepositoryFactory factory) {
		return factory.getRepository(CustomerRepository.class);
	}

	@Bean
	R2dbcRepositoryFactory repositoryFactory(DatabaseClient client) {
		RelationalMappingContext context = new RelationalMappingContext();
		context.afterPropertiesSet();
		return new R2dbcRepositoryFactory(client, context);
	}

	@Bean
	DatabaseClient databaseClient(ConnectionFactory factory) {
		return DatabaseClient.builder().connectionFactory(factory).build();
	}

}
