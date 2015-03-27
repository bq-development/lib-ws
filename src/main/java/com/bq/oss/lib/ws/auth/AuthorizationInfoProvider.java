package com.bq.oss.lib.ws.auth;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

/**
 * @author Francisco Sanchez
 */
@Provider
public class AuthorizationInfoProvider extends AbstractHttpContextInjectable<AuthorizationInfo> implements
		InjectableProvider<Context, Type> {

	@Override
	public Injectable getInjectable(ComponentContext componentContext, Context context, Type type) {
		return type.equals(AuthorizationInfo.class) ? this : null;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public AuthorizationInfo getValue(HttpContext c) {
		return (AuthorizationInfo) c.getProperties().get(AuthorizationRequestFilter.AUTHORIZATION_INFO_PROPERTIES_KEY);
	}

}
