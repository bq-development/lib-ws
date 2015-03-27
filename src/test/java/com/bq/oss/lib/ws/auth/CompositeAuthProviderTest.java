/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.auth.oauth.OAuthProvider;

/**
 * @author Alexander De Leon
 * 
 */
public class CompositeAuthProviderTest {

	private ComponentContext componentContext;
	private Auth authAnnotation;
	private Parameter parameter;
	private BasicAuthProvider<TestClass> basicProvider;
	private OAuthProvider<TestClass2> oauthProvider;
	private CompositeAuthProvider provider;

	@Before
	public void setup() {
		componentContext = mock(ComponentContext.class);
		authAnnotation = mock(Auth.class);
		parameter = mock(Parameter.class);

		basicProvider = mock(BasicAuthProvider.class);
		oauthProvider = mock(OAuthProvider.class);

		provider = new CompositeAuthProvider();
		provider.addAuthProvider(oauthProvider, TestClass2.class);
		provider.addAuthProvider(basicProvider, TestClass.class);
	}

	@Test
	public void testAddBasicAuthenticator() {
		when(parameter.getParameterClass()).thenReturn((Class) TestClass.class);

		provider.getInjectable(componentContext, authAnnotation, parameter);
		verify(basicProvider).getInjectable(componentContext, authAnnotation, parameter);
	}

	@Test
	public void testAddOauthAuthenticator() {
		when(parameter.getParameterClass()).thenReturn((Class) TestClass2.class);

		provider.getInjectable(componentContext, authAnnotation, parameter);
		verify(oauthProvider).getInjectable(componentContext, authAnnotation, parameter);
	}

	@Test(expected = IllegalStateException.class)
	public void testMultipleRegistration() {
		provider.addAuthProvider(provider, (Class) TestClass2.class);
	}

	@Test(expected = IllegalStateException.class)
	public void testNoProvider() {
		when(authAnnotation.required()).thenReturn(true);
		when(parameter.getParameterClass()).thenReturn((Class) TestClass3.class);
		provider.getInjectable(componentContext, authAnnotation, parameter);
	}

	private static class TestClass {

	}

	private static class TestClass2 {

	}

	private static class TestClass3 {

	}
}
