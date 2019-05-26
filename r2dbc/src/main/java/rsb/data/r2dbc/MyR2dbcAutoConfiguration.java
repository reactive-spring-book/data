package rsb.data.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Log4j2
@Configuration
@AutoConfigureAfter(ConnectionFactoryAutoConfiguration.class)
@Import(CustomerDatabaseInitializer.class)
@RequiredArgsConstructor
public class MyR2dbcAutoConfiguration {

	// @Bean
	// TransactionalOperator defaultTransactionalOperator(R2dbcTransactionManager txm) {
	// return TransactionalOperator.create(txm);
	// }

	// @Bean
	// R2dbcTransactionManager defaultR2dbcTransactionManager(ConnectionFactory cf) {
	// return new R2dbcTransactionManager(cf);
	// }

	@Bean
	CustomerService defaultCustomerService(SimpleCustomerRepository cr, //
			TransactionalOperator to, //
			CustomerDatabaseInitializer dbi) {
		return new CustomerService(cr, to, dbi);
	}

}

// private final Environment environment;
// private final @Value("${spring.r2dbc.url}") String url;

/*
 * @Bean ConnectionFactory defaultConnectionFactory() { return
 * ConnectionFactories.get(url); }
 */
/*
 * @Bean TransactionAwareConnectionFactoryProxy defaultPostgresConnectionFactory() { var
 * url = this.environment.getProperty("spring.r2dbc.url"); return new
 * TransactionAwareConnectionFactoryProxy(ConnectionFactories.get(url)); }
 */
