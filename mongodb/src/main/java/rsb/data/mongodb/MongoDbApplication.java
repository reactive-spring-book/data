package rsb.data.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication
@EnableTransactionManagement
public class MongoDbApplication {

	public static void main(String args[]) throws IOException {
		SpringApplication.run(MongoDbApplication.class, args);
	}

	@Bean
	ReactiveTransactionManager reactiveMongoTransactionManager(
			ReactiveMongoDatabaseFactory rdf) {
		return new ReactiveMongoTransactionManager(rdf);
	}

}
