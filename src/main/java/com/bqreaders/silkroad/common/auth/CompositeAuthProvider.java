/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.auth;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.auth.oauth.OAuthProvider;

/**
 * @author Alexander De Leon
 * 
 */
public class CompositeAuthProvider implements InjectableProvider<Auth, Parameter> {

	private static final Logger LOG = LoggerFactory.getLogger(CompositeAuthProvider.class);

	private final Map<Class<?>, InjectableProvider<Auth, Parameter>> authProviders = new HashMap<>();

	public <E> void addAuthProvider(BasicAuthProvider<E> provider, Class<E> c) {
		addAuthProvider((InjectableProvider<Auth, Parameter>) provider, c);
	}

	public <E> void addAuthProvider(OAuthProvider<E> provider, Class<E> c) {
		addAuthProvider((InjectableProvider<Auth, Parameter>) provider, c);
	}

	public <E> void addAuthProvider(InjectableProvider<Auth, Parameter> provider, Class<E> c) {
		LOG.debug("Adding auth provider {} for parameter class {}", provider.getClass().getName(), c.getName());
		assertNotAlreadyRegistered(c);
		authProviders.put(c, provider);
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext componentContext, Auth authAnnotation, Parameter parameter) {
		InjectableProvider<Auth, Parameter> provider = authProviders.get(parameter.getParameterClass());
		if (provider != null) {
			return provider.getInjectable(componentContext, authAnnotation, parameter);
		}
		if (authAnnotation.required()) {
			throw new IllegalStateException("No auth provider registered for required parameter of type "
					+ parameter.getParameterClass());
		}
		return null;
	}

	private <E> void assertNotAlreadyRegistered(Class<E> c) {
		if (authProviders.containsKey(c)) {
			throw new IllegalStateException("Registering multiple providers for the same class: " + c.getName());
		}
	}

}
