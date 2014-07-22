package com.bqreaders.silkroad.common.health;

import com.mongodb.MongoClient;
import com.yammer.metrics.core.HealthCheck;

public class MongoHealthCheck extends HealthCheck {

	private final MongoClient mongoClient;

	public MongoHealthCheck(MongoClient mongoClient) {
		super("mongo");
		this.mongoClient = mongoClient;
	}

	@Override
	protected Result check() throws Exception {
		mongoClient.getDatabaseNames();
		return Result.healthy();
	}

}
