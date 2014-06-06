package com.bqreaders.silkroad.common.health;

import com.yammer.metrics.core.HealthCheck;

public class BasicHealthCheck extends HealthCheck {

	public BasicHealthCheck() {
		super("basic");
	}

	@Override
	protected Result check() {
		return Result.healthy();
	}
}
