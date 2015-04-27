package com.bq.oss.lib.ws.api.provider;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.bq.oss.lib.ws.annotation.RemoteAddress;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;import java.lang.Override;import java.lang.String;

@Provider
public class RemoteAddressProvider implements InjectableProvider<RemoteAddress, Parameter> {

	@Context
	private HttpServletRequest request;

	public RemoteAddressProvider() {
	}

	public RemoteAddressProvider(HttpServletRequest request) {
		super();
		this.request = request;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<String> getInjectable(ComponentContext ic, RemoteAddress a, Parameter c) {
		if (String.class != c.getParameterClass()) {
			return null;
		}
		return new Injectable<String>() {

			@Override
			public String getValue() {

				return request.getRemoteAddr();
			}
		};
	}
}
