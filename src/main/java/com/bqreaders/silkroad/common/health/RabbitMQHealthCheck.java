package com.bqreaders.silkroad.common.health;

import org.springframework.amqp.core.AmqpAdmin;

import com.yammer.metrics.core.HealthCheck;

public class RabbitMQHealthCheck extends HealthCheck {

	private final AmqpAdmin amqpAdmin;

	public RabbitMQHealthCheck(AmqpAdmin amqpAdmin) {
		super("rabbit");
		this.amqpAdmin = amqpAdmin;
	}

	@Override
	protected Result check() throws Exception {
		amqpAdmin.getQueueProperties("fake");
		return Result.healthy();
	}
}
