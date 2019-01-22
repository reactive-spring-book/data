package rsb.data.mongodb;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoDbApplication {

	public static void main(String args[]) throws IOException {
		SpringApplication.run(MongoDbApplication.class, args);
	}

}
