package rsb.data.r2dbc.basics;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsb.data.r2dbc.BaseRepositoryTest;
import rsb.data.r2dbc.Customer;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private CustomerRepository repo;

	@Autowired
	private DatabaseClient databaseClient;

	@Override
	public DatabaseClient databaseClient() {
		return this.databaseClient;
	}

	@Override
	public Flux<Customer> findAll() {
		return this.repo.findAll();
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		return this.repo.deleteById(id);
	}

	@Override
	public Mono<Customer> save(Customer c) {
		return this.repo.save(c);
	}

}