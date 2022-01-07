package rsb.data.r2dbc.dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rsb.data.r2dbc.BaseCustomerRepositoryTest;
import rsb.data.r2dbc.SimpleCustomerRepository;

@SpringBootTest
public class CustomerRepositoryTest extends BaseCustomerRepositoryTest {

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Override
	public SimpleCustomerRepository getRepository() {
		return this.customerRepository;
	}

}