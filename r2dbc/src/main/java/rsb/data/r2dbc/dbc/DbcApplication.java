package rsb.data.r2dbc.dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DbcApplication {

	public static void main(String args[]) {
		SpringApplication.run(DbcApplication.class, args);
	}

}
