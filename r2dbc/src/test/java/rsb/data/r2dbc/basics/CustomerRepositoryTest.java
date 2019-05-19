package rsb.data.r2dbc.basics;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import rsb.data.r2dbc.BaseCustomerRepositoryTest;
import rsb.data.r2dbc.SimpleCustomerRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerRepositoryTest extends BaseCustomerRepositoryTest {

	@Autowired
	private DatabaseClient databaseClient;

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Override
	public DatabaseClient getDatabaseClient() {
		return this.databaseClient;
	}

	@Override
	public SimpleCustomerRepository getRepository() {
		return this.customerRepository;
	}

}