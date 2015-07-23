package io.corbel.lib.ws.dw.ioc;

import io.corbel.lib.ws.health.RabbitMQHealthCheck;
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
