package com.bqreaders.silkroad.common.health;


import com.codahale.metrics.health.HealthCheck;

public class BasicHealthCheck extends HealthCheck {

	public BasicHealthCheck() {}

	@Override
	protected Result check() {
		return Result.healthy();
	}
}
