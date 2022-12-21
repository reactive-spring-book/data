package rsb.data.r2dbc.springdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import rsb.data.r2dbc.BaseCustomerServiceTest;
import rsb.data.r2dbc.SimpleCustomerRepository;

@SpringBootTest
@EnableTransactionManagement
public class CustomerServiceTest extends BaseCustomerServiceTest {

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Override
	public SimpleCustomerRepository getCustomerRepository() {
		return this.customerRepository;
	}

}
