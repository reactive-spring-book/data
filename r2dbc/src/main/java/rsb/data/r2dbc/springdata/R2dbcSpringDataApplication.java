package rsb.data.r2dbc.springdata;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class R2dbcSpringDataApplication {

	public static void main(String args[]) {
		SpringApplication.run(R2dbcSpringDataApplication.class, args);
	}

}
