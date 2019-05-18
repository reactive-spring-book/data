package rsb.data.r2dbc.springdata;

import lombok.extern.log4j.Log4j2;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import rsb.data.r2dbc.BaseCustomerServiceTest;
import rsb.data.r2dbc.SimpleCustomerRepository;

@Log4j2
@SpringBootTest
@EnableTransactionManagement
@RunWith(SpringRunner.class)
public class CustomerServiceTest extends BaseCustomerServiceTest {

	@Autowired
	private SimpleCustomerRepository customerRepository;

	@Override
	public SimpleCustomerRepository getCustomerRepository() {
		return this.customerRepository;
	}

}
