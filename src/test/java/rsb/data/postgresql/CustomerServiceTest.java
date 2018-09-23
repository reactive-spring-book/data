package rsb.data.postgresql;

import io.r2dbc.postgresql.PostgresqlResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerServiceTest {

	@Autowired
	private CustomerService customerService;

	@Test
	public void all() {

		Flux<PostgresqlResult> resultFlux = Flux
				.from(this.customerService.create(1L, "first@email.com"))
				.thenMany(this.customerService.create(2L, "second@email.com"))
				.thenMany(this.customerService.create(3L, "third@email.com"));

		Flux<Customer> all = resultFlux.thenMany(this.customerService.all());

		StepVerifier.create(all).expectNextCount(3).verifyComplete();
	}

}