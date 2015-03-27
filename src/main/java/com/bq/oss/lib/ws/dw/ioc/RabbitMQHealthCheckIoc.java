package com.bq.oss.lib.ws.dw.ioc;

import com.bq.oss.lib.ws.health.RabbitMQHealthCheck;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQHealthCheckIoc {

	@Bean
	public RabbitMQHealthCheck getRabbitMQHealthCheck(AmqpAdmin amqpAdmin) {
		return new RabbitMQHealthCheck(amqpAdmin);
	}
}
