package rsb.data.r2dbc.springdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class R2dbcSpringDataApplication {

	public static void main(String args[]) {
		SpringApplication.run(R2dbcSpringDataApplication.class, args);
	}

}
