package rsb.data.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MongoApplication {

	public static void main(String args[]) throws IOException {
		SpringApplication.run(MongoApplication.class, args);
	}

}
