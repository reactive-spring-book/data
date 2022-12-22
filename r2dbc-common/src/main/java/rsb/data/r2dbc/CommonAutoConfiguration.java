package rsb.data.r2dbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.reactive.TransactionalOperator;

@Slf4j
@Configuration
@Import(CustomerDatabaseInitializer.class)
public class CommonAutoConfiguration {

	@Bean
	CustomerService defaultCustomerService(SimpleCustomerRepository cr, TransactionalOperator to,
			CustomerDatabaseInitializer dbi) {
		return new CustomerService(cr, to, dbi);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> demo(SimpleCustomerRepository repository) {
		return event -> log.info(String.valueOf(repository.save(new Customer(null, "josh@joshlong.com")) //
				.thenMany(repository.findAll()) //
				.blockFirst()));
	}

}
