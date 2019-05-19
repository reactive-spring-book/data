package rsb.data.r2dbc;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private CustomerDatabaseInitializer initializer;

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

		StepVerifier.create(this.initializer.resetCustomerTable()).verifyComplete();

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

	@Test
	public void update() throws Exception {
		log.info(getClass().getName());
		StepVerifier.create(this.initializer.resetCustomerTable()).verifyComplete();
		var repository = getRepository();
		var email = "test@again.com";
		var save = repository.save(new Customer(null, email));
		StepVerifier.create(save).expectNextMatches(p -> p.getId() != null)
				.verifyComplete();
		StepVerifier.create(repository.findAll()).expectNextCount(1).verifyComplete();
		var updateFlux = repository.findAll()
				.map(c -> new Customer(c.getId(), c.getEmail().toUpperCase()))
				.flatMap(repository::update);
		StepVerifier.create(updateFlux)
				.expectNextMatches(c -> c.getEmail().equals(email.toUpperCase()))
				.verifyComplete();
	}

}
