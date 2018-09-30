package rsb.data.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MongoDbApplication {

	public static void main(String args[]) throws IOException {
		SpringApplication.run(MongoDbApplication.class, args);
	}

}
