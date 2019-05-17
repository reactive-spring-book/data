package rsb.data.r2dbc.dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.SimpleCustomerRepository;

@Component
@RequiredArgsConstructor
public class CustomerRepository implements SimpleCustomerRepository {

	private final DatabaseClient databaseClient;

	// <1>
	@Override
	public Flux<Customer> findAll() {
		return databaseClient.select() //
				.from(Customer.class) //
				.fetch() //
				.all();
	}

	// <2>
	@Override
	public Mono<Customer> save(Customer c) {
		return this.databaseClient.insert() //
				.into(Customer.class) //
				.table("customer") //
				.using(c) //
				.map((row, rowMetadata) -> new Customer(Integer.class.cast(row.get("id")),
						c.getEmail()))//
				.first();
	}

	// <3>
	@Override
	public Mono<Void> deleteById(Integer id) {
		return this.databaseClient.execute() //
				.sql("DELETE FROM customer where id = $1") //
				.bind("$1", id) //
				.then();
	}

}
