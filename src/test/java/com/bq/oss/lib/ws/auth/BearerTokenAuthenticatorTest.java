/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.auth;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.bq.oss.lib.token.TokenInfo;
import com.bq.oss.lib.token.exception.TokenVerificationException;
import com.bq.oss.lib.token.model.TokenType;
import com.bq.oss.lib.token.parser.TokenParser;
import com.bq.oss.lib.token.reader.TokenReader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.dropwizard.auth.AuthenticationException;

/**
 * @author Alexander De Leon
 * 
 */
public class BearerTokenAuthenticatorTest {

	private static final String BAD_TOKEN = "bad_token";

	private static final String TEST_USER = "User";

	private static final String TEST_CLIENT = "Client";

	private static final String TEST_AUD = "audience";

	private String testToken;
	private BearerTokenAuthenticator authenticator;
	private AuthorizationRulesService authorizationRulesServiceMock;
	private TokenParser tokenParserMock;
	private TokenReader tokenReaderMock;
	private TokenInfo tokenInfoMock;

	@Before
	public void setup() throws TokenVerificationException {
		authorizationRulesServiceMock = mock(AuthorizationRulesService.class);
		tokenParserMock = mock(TokenParser.class);
		tokenReaderMock = mock(TokenReader.class);
		tokenInfoMock = mock(TokenInfo.class);
		when(tokenInfoMock.getUserId()).thenReturn(TEST_USER);
		when(tokenInfoMock.getClientId()).thenReturn(TEST_CLIENT);
		when(tokenInfoMock.getTokenType()).thenReturn(TokenType.TOKEN);

		when(tokenReaderMock.getInfo()).thenReturn(tokenInfoMock);
		authenticator = new BearerTokenAuthenticator(TEST_AUD, authorizationRulesServiceMock, tokenParserMock);

		testToken = TokenInfo.newBuilder().setType(TokenType.TOKEN).setUserId(TEST_USER).setClientId(TEST_CLIENT)
				.setOneUseToken(false).build().serialize();
		when(tokenParserMock.parseAndVerify(Mockito.eq(testToken))).thenReturn(tokenReaderMock);
		when(tokenParserMock.parseAndVerify(BAD_TOKEN)).thenThrow(new TokenVerificationException("Invalid token"));
	}

	@Test
	public void testAuthenticate() throws AuthenticationException {
		Set<JsonObject> set = new HashSet<>();
		JsonObject rule = new JsonObject();
		rule.add("a", new JsonPrimitive("1"));
		set.add(rule);
		when(authorizationRulesServiceMock.getAuthorizationRules(testToken, TEST_AUD)).thenReturn(set);
		Optional<AuthorizationInfo> info = authenticator.authenticate(testToken);
		assertThat(info.isPresent()).isTrue();
		assertThat(info.get().getTokenReader().getInfo().getClientId()).isEqualTo(TEST_CLIENT);
		assertThat(info.get().getTokenReader().getInfo().getUserId()).isEqualTo(TEST_USER);
		assertThat(info.get().getAccessRules()).isEqualTo(set);
	}

	@Test
	public void testAuthenticatWithInvalidToken() throws AuthenticationException {
		Optional<AuthorizationInfo> info = authenticator.authenticate(BAD_TOKEN);
		assertThat(info.isPresent()).isFalse();
	}

	@Test
	public void testTokenNotFound() throws AuthenticationException {
		when(authorizationRulesServiceMock.getAuthorizationRules(testToken, TEST_AUD)).thenReturn(null);
		Optional<AuthorizationInfo> info = authenticator.authenticate(testToken);
		assertThat(info.isPresent()).isFalse();
	}

	@Test
	public void testTokenNotFoundEmptyRules() throws AuthenticationException {
		when(authorizationRulesServiceMock.getAuthorizationRules(testToken, TEST_AUD)).thenReturn(
				Collections.<JsonObject> emptySet());
		Optional<AuthorizationInfo> info = authenticator.authenticate(testToken);
		assertThat(info.isPresent()).isFalse();
	}

	@Test(expected = AuthenticationException.class)
	public void testAuthenticationException() throws AuthenticationException {
		when(authorizationRulesServiceMock.getAuthorizationRules(testToken, TEST_AUD)).thenThrow(
				new RuntimeException("test exception"));
		authenticator.authenticate(testToken);
	}
}
