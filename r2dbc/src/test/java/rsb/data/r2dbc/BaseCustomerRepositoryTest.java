package rsb.data.r2dbc;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStreamReader;
import java.io.Reader;

@Log4j2
public abstract class BaseCustomerRepositoryTest {

	// <1>
	public abstract DatabaseClient getDatabaseClient();

	// <2>
	public abstract SimpleCustomerRepository getRepository();

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

		SimpleCustomerRepository repo = getRepository();

		Mono<Void> createSchema = this.getDatabaseClient().execute().sql(this.sql).then(); // <6>

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
				.expectNextCount(2) //
				.expectNextMatches(customer -> customer.getId() != null
						&& customer.getId() > 0 && customer.getEmail() != null)
				.verifyComplete(); // <11>

		Flux<Customer> all = repo.findAll();

		StepVerifier.create(all).expectNextCount(3).verifyComplete();
	}

}
