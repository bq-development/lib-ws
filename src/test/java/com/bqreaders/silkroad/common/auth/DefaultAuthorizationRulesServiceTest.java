/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.auth;

import com.bqreaders.silkroad.common.auth.repository.AuthorizationRulesRepository;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander De Leon
 * 
 */
public class DefaultAuthorizationRulesServiceTest {

	private static final String TEST_TOKEN = "i_m_a_token";
	private static final String TEST_AUD = "i_m_a_audience";

	private DefaultAuthorizationRulesService service;
	private AuthorizationRulesRepository authorizationRulesRepositoryMock;

	@Before
	public void setup() {
		authorizationRulesRepositoryMock = mock(AuthorizationRulesRepository.class);
		service = new DefaultAuthorizationRulesService(authorizationRulesRepositoryMock);
	}

	@Test
	public void testGetAuthenticationRules() {
		Set<JsonObject> set = new HashSet<>();
		String key = "key";
		when(authorizationRulesRepositoryMock.getKeyForAuthorizationRules(TEST_TOKEN, TEST_AUD)).thenReturn(key);
		when(authorizationRulesRepositoryMock.get(key)).thenReturn(set);
		assertThat(service.getAuthorizationRules(TEST_TOKEN, TEST_AUD)).isSameAs(set);
	}
}
