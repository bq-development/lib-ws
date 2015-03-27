package com.bq.oss.lib.ws.dw.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bq.oss.lib.ws.health.MongoHealthCheck;
import com.mongodb.MongoClient;

@Configuration
public abstract class MongoHealthCheckIoc {

	@Bean
	public MongoHealthCheck getMongoHealthCheck(MongoClient mongoClient) {
		return new MongoHealthCheck(mongoClient);
	}

}
