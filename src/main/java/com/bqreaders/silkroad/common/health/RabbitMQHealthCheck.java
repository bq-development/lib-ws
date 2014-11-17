package com.bqreaders.silkroad.common.health;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.amqp.core.AmqpAdmin;

public class RabbitMQHealthCheck extends HealthCheck {

	private final AmqpAdmin amqpAdmin;

	public RabbitMQHealthCheck(AmqpAdmin amqpAdmin) {
		this.amqpAdmin = amqpAdmin;
	}

	@Override
	protected Result check() throws Exception {
		amqpAdmin.getQueueProperties("fake");
		return Result.healthy();
	}
}
