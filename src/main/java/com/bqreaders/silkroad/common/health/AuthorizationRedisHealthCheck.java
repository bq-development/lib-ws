package com.bqreaders.silkroad.common.health;

import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.JsonObject;
import com.yammer.metrics.core.HealthCheck;

/**
 * @author Rubén Carrasco
 *
 */
public class AuthorizationRedisHealthCheck extends HealthCheck {

	private final RedisTemplate<String, JsonObject> redisTemplate;

	public AuthorizationRedisHealthCheck(RedisTemplate<String, JsonObject> redisTemplate) {
		super("redis");
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
