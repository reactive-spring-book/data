package rsb.data.r2dbc;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.InputStreamReader;

@Component
class CustomerDatabaseInitializer {

	private final TransactionalOperator transactionalOperator;

	private final DatabaseClient client;

	private final String sql;

	private final SimpleCustomerRepository repository;

	CustomerDatabaseInitializer(@Value("classpath:/schema.sql") Resource resource, DatabaseClient client,
			SimpleCustomerRepository repository, TransactionalOperator operator) throws Exception {
		this.client = client;
		this.repository = repository;
		this.transactionalOperator = operator;
		try (var in = new InputStreamReader(resource.getInputStream())) {
			this.sql = FileCopyUtils.copyToString(in);
		}
	}

	public Publisher<Void> resetCustomerTable() {
		Mono<Void> createSchema = client.sql(this.sql).then();
		Flux<Void> findAndDelete = repository.findAll().flatMap(customer -> repository.deleteById(customer.id())); // <7>
		return createSchema.thenMany(this.transactionalOperator.execute(status -> findAndDelete));
	}

}
