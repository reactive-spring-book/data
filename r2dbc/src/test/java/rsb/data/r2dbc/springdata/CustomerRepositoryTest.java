package rsb.data.r2dbc.springdata;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import rsb.data.r2dbc.BaseRepositoryTest;
import rsb.data.r2dbc.SimpleCustomerRepository;

// <1>
@SpringBootTest
@Import(R2dbcSpringDataApplication.class)
@RunWith(SpringRunner.class)
public class CustomerRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private CustomerRepository repo;

	@Autowired
	private DatabaseClient databaseClient;

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Override
	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	@Override
	public SimpleCustomerRepository getRepository() {
		return this.customerRepository;
	}

}
