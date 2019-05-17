package rsb.data.r2dbc.springdata;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsb.data.r2dbc.BaseRepositoryTest;
import rsb.data.r2dbc.Customer;
import rsb.data.r2dbc.SimpleCustomerRepository;

// <1>
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private CustomerRepository repo;

	@Autowired
	private DatabaseClient databaseClient;

	@Override
	public DatabaseClient databaseClient() {
		return databaseClient;
	}

	@Override
	public SimpleCustomerRepository repository() {
		return this.repository;
	}

	// <2>
	private final SimpleCustomerRepository repository = new SimpleCustomerRepository() {

		@Override
		public Mono<Void> deleteById(Integer id) {
			return repo.deleteById(id);
		}

		@Override
		public Mono<Customer> save(Customer c) {
			return repo.save(c);
		}

		@Override
		public Flux<Customer> findAll() {
			return repo.findAll();
		}
	};

}