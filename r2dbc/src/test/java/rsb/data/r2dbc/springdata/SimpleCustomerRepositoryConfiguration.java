package rsb.data.r2dbc.springdata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SimpleCustomerRepositoryConfiguration {

	@Bean
	SpringDataCustomerRepository springDataCustomerRepository(
			CustomerRepository repository) {
		return new SpringDataCustomerRepository(repository);
	}

}
