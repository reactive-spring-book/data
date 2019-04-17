package rsb.data.r2dbc;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
public abstract class BaseRepositoryTest {

	// <1>
	public abstract DatabaseClient databaseClient();

	// <2>
	public abstract SimpleCustomerRepository repository();

	// <3>
	@Value("classpath:/schema.sql")
	private Resource resource;

	// <4>
	private String sql;

	@Before
	public void setupResource() throws Exception {
		Assert.assertTrue(this.resource.exists());
		// <5>
		try (Reader in = new InputStreamReader(this.resource.getInputStream())) {
			this.sql = FileCopyUtils.copyToString(in);
		}
	}

	@Test
	public void all() throws Exception {

		SimpleCustomerRepository repo = repository();

		Mono<Void> createSchema = this.databaseClient().execute().sql(this.sql).then(); // <6>

		Flux<Void> findAndDelete = repo.findAll()
				.flatMap(customer -> repo.deleteById(customer.getId())); // <7>

		StepVerifier //
				.create(createSchema.thenMany(findAndDelete)) // <8>
				.expectNextCount(0) //
				.verifyComplete();

		// <9>
		Flux<Customer> insert = Flux.just( //
				new Customer(null, "first@email.com"), //
				new Customer(null, "second@email.com"), //
				new Customer(null, "third@email.com")) //
				.flatMap(repo::save); // <10>

		StepVerifier //
				.create(insert) //
				.expectNextCount(3) //
				.verifyComplete(); // <11>

		Flux<Customer> all = repo.findAll();

		StepVerifier.create(all).expectNextCount(3).verifyComplete();
	}

}
