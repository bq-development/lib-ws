/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.auth.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

import com.bqreaders.lib.token.ioc.TokenIoc;
import com.bqreaders.lib.token.parser.TokenParser;
import com.bqreaders.silkroad.common.auth.AuthorizationInfo;
import com.bqreaders.silkroad.common.auth.AuthorizationRequestFilter;
import com.bqreaders.silkroad.common.auth.AuthorizationRulesService;
import com.bqreaders.silkroad.common.auth.BearerTokenAuthenticator;
import com.bqreaders.silkroad.common.auth.CookieOAuthProvider;
import com.bqreaders.silkroad.common.auth.DefaultAuthorizationRulesService;
import com.bqreaders.silkroad.common.auth.repository.AuthorizationRulesRepository;
import com.bqreaders.silkroad.common.auth.repository.RedisAuthorizationRulesRepository;
import com.bqreaders.silkroad.common.health.AuthorizationRedisHealthCheck;
import com.bqreaders.silkroad.common.redis.GsonRedisSerializer;
import com.google.gson.JsonObject;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.oauth.OAuthProvider;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration
@Import(TokenIoc.class)
public class AuthorizationIoc {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationIoc.class);

	@Bean
	public AuthorizationRulesRepository getAuthorizationRulesRepository(RedisTemplate<String, JsonObject> redisTemplate) {
		return new RedisAuthorizationRulesRepository(redisTemplate);
	}

	@Bean
	public RedisTemplate<String, JsonObject> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		final RedisTemplate<String, JsonObject> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GsonRedisSerializer<JsonObject>());
		return template;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig,
			@Value("${auth.redis.host:@null}") String host, @Value("${auth.redis.port:@null}") Integer port,
			@Value("${auth.redis.password:}") String password) {
		JedisConnectionFactory connFactory = new JedisConnectionFactory(jedisPoolConfig);
		connFactory.setPassword(password);
		if (host != null) {
			connFactory.setHostName(host);
		}
		if (port != null) {
			connFactory.setPort(port);
		}
		return connFactory;
	}

	@Bean
	public JedisPoolConfig jedisPoolConfig(@Value("${auth.redis.maxIdle:@null}") Integer maxIdle,
			@Value("${auth.redis.maxTotal:@null}") Integer maxTotal,
			@Value("${auth.redis.minIdle:@null}") Integer minIdle,
			@Value("${auth.redis.testOnBorrow:@null}") Boolean testOnBorrow,
			@Value("${auth.redis.testOnReturn:@null}") Boolean testOnReturn,
			@Value("${auth.redis.testWhileIdle:@null}") Boolean testWhileIdle,
			@Value("${auth.redis.numTestsPerEvictionRun:@null}") Integer numTestsPerEvictionRun,
			@Value("${auth.redis.maxWaitMillis:@null}") Long maxWaitMillis,
			@Value("${auth.redis.timeBetweenEvictionRunsMillis:@null}") Long timeBetweenEvictionRunsMillis,
			@Value("${auth.redis.blockWhenExhausted:@null}") Boolean blockWhenExhausted) {
		JedisPoolConfig config = new JedisPoolConfig();
		if (maxIdle != null) {
			config.setMaxIdle(maxIdle);
		}
		if (maxTotal != null) {
			config.setMaxTotal(maxTotal);
		}
		if (minIdle != null) {
			config.setMinIdle(minIdle);
		}
		if (testOnBorrow != null) {
			config.setTestOnBorrow(testOnBorrow);
		}
		if (testOnReturn != null) {
			config.setTestOnReturn(testOnReturn);
		}
		if (testWhileIdle != null) {
			config.setTestWhileIdle(testWhileIdle);
		}
		if (numTestsPerEvictionRun != null) {
			config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		}
		if (timeBetweenEvictionRunsMillis != null) {
			config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		}
		if (maxWaitMillis != null) {
			config.setMaxWaitMillis(maxWaitMillis);
		}
		if (blockWhenExhausted != null) {
			config.setBlockWhenExhausted(blockWhenExhausted);
		}
		return config;
	}

	@Bean
	public AuthorizationRulesService authorizationRulesService(AuthorizationRulesRepository authorizationRulesRepository) {
		return new DefaultAuthorizationRulesService(authorizationRulesRepository);
	}

	@Bean
	public Authenticator<String, AuthorizationInfo> authenticator(@Value("${auth.audience}") String audience,
			TokenParser tokenParser, AuthorizationRulesService authorizationRulesService) {
		return new BearerTokenAuthenticator(audience, authorizationRulesService, tokenParser);
	}

	@Bean(name = "authProvider")
	public OAuthProvider<AuthorizationInfo> getOAuthProvider(Authenticator<String, AuthorizationInfo> authenticator,
			@Value("${auth.realm}") String realm) {
		return new OAuthProvider<>(authenticator, realm);
	}

	@Bean(name = "cookieAuthProvider")
	public CookieOAuthProvider<AuthorizationInfo> getCookieOAuthProvider(
			Authenticator<String, AuthorizationInfo> authenticator) {
		return new CookieOAuthProvider<>(authenticator);
	}

	@Bean
	public ContainerRequestFilter getAuthorizationRequestFilter(OAuthProvider<AuthorizationInfo> oauthProvider,
			CookieOAuthProvider<AuthorizationInfo> cookieOauthProvider, @Value("${auth.enabled}") boolean authEnabled,
			@Value("${auth.unAuthenticatedPath}") String unAuthenticatedPath) {
		if (authEnabled) {
			return new AuthorizationRequestFilter(oauthProvider, cookieOauthProvider, unAuthenticatedPath);
		} else {
			LOG.warn("Authorization validation is disabled. The systen is in a INSECURE mode");
			return emptyFilter();
		}
	}

	@Bean
	public AuthorizationRedisHealthCheck getAuthorizationRedisHealthCheck(
			RedisTemplate<String, JsonObject> redisTemplate) {
		return new AuthorizationRedisHealthCheck(redisTemplate);
	}

	private ContainerRequestFilter emptyFilter() {
		return request -> request;
	}

}