package rsb.data.r2dbc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.FileCopyUtils;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStreamReader;
import java.util.Locale;

@Slf4j
@Testcontainers
public abstract class BaseCustomerRepositoryTest {

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.sql.init.mode", () -> "always");
		registry.add("spring.r2dbc.url", () -> "r2dbc:tc:postgresql://rsbhost/rsb?TC_IMAGE_TAG=9.6.8");
	}

	// <1>
	public abstract SimpleCustomerRepository getRepository();

	// <2>
	@Autowired
	private CustomerDatabaseInitializer initializer;

	// <3>
	@Value("classpath:/schema.sql")
	private Resource resource;

	private String sql;

	@BeforeEach
	public void setupResource() throws Exception {
		Assertions.assertTrue(this.resource.exists());
		try (var in = new InputStreamReader(this.resource.getInputStream())) {
			this.sql = FileCopyUtils.copyToString(in);
		}
	}

	@Test
	public void delete() {

		var repository = this.getRepository();
		var data = repository //
				.findAll() //
				.flatMap(c -> repository.deleteById(c.id()))//
				.thenMany(Flux.just( //
						new Customer(null, "first@email.com"), //
						new Customer(null, "second@email.com"), //
						new Customer(null, "third@email.com"))) //
				.flatMap(repository::save); //

		StepVerifier //
				.create(data) //
				.expectNextCount(3) //
				.verifyComplete();
		log.info("there are (1) " + repository.findAll().collectList().block().size());
		StepVerifier //
				.create(repository.findAll().take(1).doOnNext(c -> log.info("the customer is " + c))
						.flatMap(customer -> repository.deleteById(customer.id())).then())

				.verifyComplete(); //
		log.info("there are (2) " + repository.findAll().collectList().block().size());
		StepVerifier //
				.create(repository.findAll()) //
				.expectNextCount(2) //
				.verifyComplete();
	}

	@Test
	public void saveAndFindAll() {

		var repository = this.getRepository();

		StepVerifier.create(this.initializer.resetCustomerTable()).verifyComplete();

		var insert = Flux.just( //
				new Customer(null, "first@email.com"), //
				new Customer(null, "second@email.com"), //
				new Customer(null, "third@email.com")) //
				.flatMap(repository::save); //

		StepVerifier //
				.create(insert) //
				.expectNextCount(2) //
				.expectNextMatches(customer -> customer.id() != null && customer.id() > 0 && customer.email() != null)
				.verifyComplete(); //

	}

	@Test
	public void findById() {

		var repository = this.getRepository();

		var insert = Flux.just( //
				new Customer(null, "first@email.com"), //
				new Customer(null, "second@email.com"), //
				new Customer(null, "third@email.com")) //
				.flatMap(repository::save); //
		var all = repository.findAll().flatMap(c -> repository.deleteById(c.id()))
				.thenMany(insert.thenMany(repository.findAll()));

		StepVerifier.create(all).expectNextCount(3).verifyComplete();

		var recordsById = repository.findAll()
				.flatMap(customer -> Mono.zip(Mono.just(customer), repository.findById(customer.id())))
				.filterWhen(tuple2 -> Mono.just(tuple2.getT1().equals(tuple2.getT2())));

		StepVerifier.create(recordsById).expectNextCount(3).verifyComplete();
	}

	@Test
	public void update() {
		var repository = this.getRepository();
		StepVerifier //
				.create(this.initializer.resetCustomerTable()) //
				.verifyComplete(); //
		var email = "test@again.com";
		var save = repository.save(new Customer(null, email));
		StepVerifier //
				.create(save.log()) //
				.expectNextMatches(p -> p.id() != null) //
				.verifyComplete();
		StepVerifier //
				.create(repository.findAll()) //
				.expectNextCount(1) //
				.verifyComplete();
		var updateFlux = repository //
				.findAll() //
				.map(c -> new Customer(c.id(), c.email().toLowerCase(Locale.ROOT))) //
				.flatMap(repository::update);
		StepVerifier //
				.create(updateFlux) //
				.expectNextMatches(c -> c.email().equals(email.toLowerCase(Locale.ROOT))) //
				.verifyComplete();
	}

}
