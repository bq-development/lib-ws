/*
 * Copyright (C) 2014 StarTIC
 */
package com.bqreaders.silkroad.common.auth;

import javax.ws.rs.WebApplicationException;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

/**
 * @author Alexander De Leon
 *
 */
public class BasicAuthProvider<T> extends com.yammer.dropwizard.auth.basic.BasicAuthProvider<T> {

	private String realm;

	public BasicAuthProvider(Authenticator<BasicCredentials, T> authenticator) {
		this(authenticator, null);
	}

	public BasicAuthProvider(Authenticator<BasicCredentials, T> authenticator, String realm) {
		super(authenticator, realm);
        this.realm = realm;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext context, Auth auth, Parameter param) {
		AbstractHttpContextInjectable<?> injectable = (AbstractHttpContextInjectable<?>) super.getInjectable(context, auth, param);
		return new AbstractHttpContextInjectable<Object>() {
			@Override
			public Object getValue(HttpContext context) {
				try {
					return injectable.getValue(context);
				} catch (WebApplicationException webApplicationException) {
					if (realm == null) {
						webApplicationException.getResponse().getMetadata().remove(HttpHeaders.WWW_AUTHENTICATE);
					}
					throw webApplicationException;
				}
			}
		};
	}
}
