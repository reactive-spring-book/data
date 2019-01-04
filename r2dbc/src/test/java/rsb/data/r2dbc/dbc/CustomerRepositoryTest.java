package rsb.data.r2dbc.dbc;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rsb.data.r2dbc.BaseRepositoryTest;
import rsb.data.r2dbc.Customer;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private DatabaseClient databaseClient;

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public DatabaseClient databaseClient() {
		return this.databaseClient;
	}

	@Override
	public Flux<Customer> findAll() {
		return customerRepository.findAll();
	}

	@Override
	public Mono<Void> deleteById(Integer id) {
		return customerRepository.deleteById(id);
	}

	@Override
	public void all() throws Exception {
		super.all();
	}

	@Override
	public Mono<Customer> save(Customer c) {
		return customerRepository.save(c);
	}

}