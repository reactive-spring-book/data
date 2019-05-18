package rsb.data.r2dbc.basics;

import io.r2dbc.spi.ConnectionFactories;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.connectionfactory.TransactionAwareConnectionFactoryProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import rsb.data.r2dbc.CustomerService;
import rsb.data.r2dbc.SimpleCustomerRepository;

@Log4j2
@SpringBootApplication
@EnableTransactionManagement
@RequiredArgsConstructor
public class R2dbcBasicsApplication {

	private final Environment environment;

	public static void main(String args[]) {
		SpringApplication.run(R2dbcBasicsApplication.class, args);
	}

	@Bean
	R2dbcTransactionManager r2dbcTransactionManager() {
		return new R2dbcTransactionManager(postgresConnectionFactory());
	}

	@Bean
	TransactionAwareConnectionFactoryProxy postgresConnectionFactory() {
		var url = this.environment.getProperty("spring.r2dbc.url");
		return new TransactionAwareConnectionFactoryProxy(ConnectionFactories.get(url));
	}

	@Bean
	CustomerService customerService(SimpleCustomerRepository customerRepository,
			TransactionalOperator transactionalOperator) {
		return new CustomerService(customerRepository, transactionalOperator);
	}

}