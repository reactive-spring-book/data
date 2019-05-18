package rsb.data.r2dbc;

import io.r2dbc.spi.ConnectionFactories;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.connectionfactory.TransactionAwareConnectionFactoryProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Log4j2
@Configuration
@EnableTransactionManagement
@AutoConfigureBefore(ConnectionFactoryAutoConfiguration.class)
@Import(CustomerDatabaseInitializer.class)
@RequiredArgsConstructor
public class MyR2dbcAutoConfiguration {

	private final Environment environment;

	@Bean
	TransactionalOperator defaultTransactionalOperator() {
		return TransactionalOperator.create(defaultR2dbcTransactionManager());
	}

	@Bean
	R2dbcTransactionManager defaultR2dbcTransactionManager() {
		return new R2dbcTransactionManager(defaultPostgresConnectionFactory());
	}

	@Bean
	TransactionAwareConnectionFactoryProxy defaultPostgresConnectionFactory() {
		var url = this.environment.getProperty("spring.r2dbc.url");
		return new TransactionAwareConnectionFactoryProxy(ConnectionFactories.get(url));
	}

	@Bean
	CustomerService customerService(SimpleCustomerRepository cr,
			TransactionalOperator to) {
		return new CustomerService(cr, to);
	}

}
