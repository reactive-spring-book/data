package rsb.data.r2dbc.dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class DbcApplication {

	public static void main(String args[]) {
		SpringApplication.run(DbcApplication.class, args);
	}

}
