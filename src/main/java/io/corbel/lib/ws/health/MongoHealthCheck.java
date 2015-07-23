package io.corbel.lib.ws.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;

public class MongoHealthCheck extends HealthCheck {

	private final MongoClient mongoClient;

	public MongoHealthCheck(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	@Override
	protected Result check() throws Exception {
		mongoClient.getDatabaseNames();
		return Result.healthy();
	}
}
