/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.auth;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bqreaders.lib.token.exception.TokenVerificationException;
import com.bqreaders.lib.token.parser.TokenParser;
import com.bqreaders.lib.token.reader.TokenReader;
import com.google.common.base.Optional;
import com.google.gson.JsonObject;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;

/**
 * Implements a the dropwizard's {@link com.yammer.dropwizard.auth.Authenticator} interface to provide an {@link com.bqreaders.silkroad.common.auth.AuthorizationInfo} from the bearer
 * token. A resource method can be injected with an instance of {@link com.bqreaders.silkroad.common.auth.AuthorizationInfo} by just annotating the
 * parameter with the {@link com.yammer.dropwizard.auth.Auth} annotation.
 * 
 * @author Alexander De Leon
 * 
 */
public class BearerTokenAuthenticator implements Authenticator<String, AuthorizationInfo> {

	private static final Logger LOG = LoggerFactory.getLogger(BearerTokenAuthenticator.class);

	private final String audience;
	private final AuthorizationRulesService authorizationRulesService;
	private final TokenParser tokenParser;

	public BearerTokenAuthenticator(String audience, AuthorizationRulesService authorizationRulesService,
			TokenParser tokenParser) {
		this.audience = audience;
		this.authorizationRulesService = authorizationRulesService;
		this.tokenParser = tokenParser;

	}

	@Override
	public Optional<AuthorizationInfo> authenticate(String token) throws AuthenticationException {
		try {
			TokenReader tokenReader = tokenParser.parseAndVerify(token);
			Set<JsonObject> accessRules = authorizationRulesService.getAuthorizationRules(token, audience);
			// If we can not find authorization rules then no authorization exists.
			if (accessRules != null && !accessRules.isEmpty()) {
				return Optional.of(new AuthorizationInfo(tokenReader, accessRules));
			}
		} catch (IllegalArgumentException | TokenVerificationException e) {
			LOG.trace("Invalid access token {}", token, e);
		} catch (Exception e) {
			LOG.error("Unexpected error when validating token", e);
			throw new AuthenticationException(e);
		}
		return Optional.absent();
	}
}
