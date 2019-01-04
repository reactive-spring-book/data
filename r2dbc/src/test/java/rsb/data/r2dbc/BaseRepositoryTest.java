package rsb.data.r2dbc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
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
public abstract class BaseRepositoryTest {

	public abstract DatabaseClient databaseClient();

	public abstract Flux<Customer> findAll();

	public abstract Mono<Void> deleteById(Integer id);

	public abstract Mono<Customer> save(Customer c);

	@Value("classpath:/schema.sql")
	private Resource resource;

	private String sql;

	@Before
	public void setupResource() throws Exception {
		Assert.assertTrue(this.resource.exists());
		try (Reader in = new InputStreamReader(this.resource.getInputStream())) {
			this.sql = FileCopyUtils.copyToString(in);
		}
	}

	private Publisher<Void> begin() throws Exception {
		return this.databaseClient().execute().sql(this.sql).then();
	}

	@Test
	public void all() throws Exception {

		Flux<Void> deleteEverything = Mono.from(this.begin())
				.thenMany(findAll().flatMap(customer -> deleteById(customer.getId())));

		StepVerifier.create(deleteEverything).expectNextCount(0).verifyComplete();

		Flux<Customer> insert = Flux.just(new Customer(null, "first@email.com"),
				new Customer(null, "second@email.com"),
				new Customer(null, "third@email.com")).flatMap(this::save);

		StepVerifier.create(insert).expectNextCount(3).verifyComplete();

		Flux<Customer> all = findAll();

		StepVerifier.create(all).expectNextCount(3).verifyComplete();
	}

}
