package rsb.data.r2dbc.basics;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import rsb.data.r2dbc.BaseRepositoryTest;
import rsb.data.r2dbc.SimpleCustomerRepository;

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
		return repo;
	}

}