package rsb.data.r2dbc.springdata;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import rsb.data.r2dbc.BaseCustomerServiceTest;
import rsb.data.r2dbc.CustomerService;
import rsb.data.r2dbc.SimpleCustomerRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerServiceTest extends BaseCustomerServiceTest {

	@Autowired
	private SimpleCustomerRepository repository;

	@Autowired
	private CustomerService customerService;

	protected CustomerService getService() {
		return this.customerService;
	}

	protected SimpleCustomerRepository getRepository() {
		return this.repository;
	}

}
