package com.bq.oss.lib.ws.health;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.JsonObject;

/**
 * @author Rubén Carrasco
 *
 */
public class AuthorizationRedisHealthCheck extends HealthCheck {

	private final RedisTemplate<String, JsonObject> redisTemplate;

	public AuthorizationRedisHealthCheck(RedisTemplate<String, JsonObject> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	protected Result check() throws Exception {
		if (redisTemplate.getConnectionFactory().getConnection().ping().equals("PONG")) {
			return Result.healthy();
		}
		return Result.unhealthy("Redis is down");
	}
}
