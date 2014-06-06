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

import com.bqreaders.lib.token.ioc.TokenIoc;
import com.bqreaders.lib.token.parser.TokenParser;
import com.bqreaders.silkroad.common.auth.AuthorizationInfo;
import com.bqreaders.silkroad.common.auth.AuthorizationRequestFilter;
import com.bqreaders.silkroad.common.auth.AuthorizationRulesService;
import com.bqreaders.silkroad.common.auth.BearerTokenAuthenticator;
import com.bqreaders.silkroad.common.auth.DefaultAuthorizationRulesService;
import com.bqreaders.silkroad.common.auth.repository.AuthorizationRulesRepository;
import com.bqreaders.silkroad.common.auth.repository.RedisAuthorizationRulesRepository;
import com.bqreaders.silkroad.common.redis.GsonRedisSerializer;
import com.google.gson.JsonObject;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.oauth.OAuthProvider;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration
@Import(TokenIoc.class)
public class AuthorizationIoc {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationIoc.class);

	@Bean
	public AuthorizationRulesRepository getAuthorizationRulesRepository() {
		return new RedisAuthorizationRulesRepository(redisTemplate());
	}

	@Bean
	public RedisTemplate<String, JsonObject> redisTemplate() {
		final RedisTemplate<String, JsonObject> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GsonRedisSerializer<JsonObject>());
		return template;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	public AuthorizationRulesService authorizationRulesService() {
		return new DefaultAuthorizationRulesService(getAuthorizationRulesRepository());
	}

	@Bean
	public Authenticator<String, AuthorizationInfo> authenticator(@Value("${auth.audience}") String audience,
			TokenParser tokenParser) {
		return new BearerTokenAuthenticator(audience, authorizationRulesService(), tokenParser);
	}

	@Bean(name = "authProvider")
	public OAuthProvider<AuthorizationInfo> getOAuthProvider(Authenticator<String, AuthorizationInfo> authenticator,
			@Value("${auth.realm}") String realm) {
		return new OAuthProvider<>(authenticator, realm);
	}

	@Bean
	public ContainerRequestFilter getAuthorizationRequestFileter(OAuthProvider<AuthorizationInfo> oauthProvider,
			@Value("${auth.enabled}") boolean authEnabled, @Value("${auth.securePath}") String securePath) {
		if (authEnabled) {
			return new AuthorizationRequestFilter(oauthProvider, securePath);
		} else {
			LOG.warn("Authorization validation is disabled. The systen is in a INSECURE mode");
			return emptyFilter();
		}
	}

	private ContainerRequestFilter emptyFilter() {
		return request -> request;
	}

}
