package com.bqreaders.silkroad.common.dw.ioc;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bqreaders.silkroad.common.health.RabbitMQHealthCheck;

@Configuration
public class RabbitMQHealthCheckIoc {

	@Bean
	public RabbitMQHealthCheck getRabbitMQHealthCheck(AmqpAdmin amqpAdmin) {
		return new RabbitMQHealthCheck(amqpAdmin);
	}
}
