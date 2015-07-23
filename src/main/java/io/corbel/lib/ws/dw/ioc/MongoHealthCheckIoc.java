package io.corbel.lib.ws.dw.ioc;

import io.corbel.lib.ws.health.MongoHealthCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;

@Configuration
public abstract class MongoHealthCheckIoc {

	@Bean
	public MongoHealthCheck getMongoHealthCheck(MongoClient mongoClient) {
		return new MongoHealthCheck(mongoClient);
	}

}
