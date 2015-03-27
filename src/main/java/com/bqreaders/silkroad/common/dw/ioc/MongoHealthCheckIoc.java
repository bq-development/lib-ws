package com.bqreaders.silkroad.common.dw.ioc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bqreaders.silkroad.common.health.MongoHealthCheck;
import com.bqreaders.silkroad.mongo.config.DefaultMongoConfiguration;
import com.mongodb.MongoClient;

@Configuration
public abstract class MongoHealthCheckIoc {

	@Bean
	public MongoHealthCheck getMongoHealthCheck(MongoClient mongoClient) {
		return new MongoHealthCheck(mongoClient);
	}

}
