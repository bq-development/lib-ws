package com.bq.oss.lib.ws.health;


import com.codahale.metrics.health.HealthCheck;

public class BasicHealthCheck extends HealthCheck {

	public BasicHealthCheck() {}

	@Override
	protected Result check() {
		return Result.healthy();
	}
}
