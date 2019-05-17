package rsb.data.r2dbc.dbc;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rsb.data.r2dbc.BaseCustomerServiceTest;
import rsb.data.r2dbc.CustomerService;
import rsb.data.r2dbc.SimpleCustomerRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerServiceTest extends BaseCustomerServiceTest {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SimpleCustomerRepository repository;

	@Override
	protected CustomerService getService() {
		return this.customerService;
	}

	@Override
	protected SimpleCustomerRepository getRepository() {
		return this.repository;
	}

}
