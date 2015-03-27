package com.bq.oss.lib.ws.auth;

import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @author Rubén Carrasco
 *
 */
public class CookieOAuthProvider<T> implements InjectableProvider<Auth, Parameter> {

	private final Authenticator<String, T> authenticator;

	/**
	 * Creates a new OAuthProvider with the given {@link Authenticator} and realm.
	 *
	 * @param authenticator
	 *            the authenticator which will take the OAuth2 bearer token and convert them into instances of {@code T}
	 * @param realm
	 *            the name of the authentication realm
	 */
	public CookieOAuthProvider(Authenticator<String, T> authenticator) {
		this.authenticator = authenticator;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext ic, Auth a, Parameter c) {
		return new CookieOAuthInjectable<T>(authenticator, a.required());
	}

	private static class CookieOAuthInjectable<T> extends AbstractHttpContextInjectable<T> {
		private static final Logger LOGGER = LoggerFactory.getLogger(CookieOAuthInjectable.class);
		private static final String COOKIE_KEY = "token";

		private final Authenticator<String, T> authenticator;
		private final boolean required;

		private CookieOAuthInjectable(Authenticator<String, T> authenticator, boolean required) {
			this.authenticator = authenticator;
			this.required = required;
		}

		@Override
		public T getValue(HttpContext c) {
			try {
				final Map<String, Cookie> cookies = c.getRequest().getCookies();
				if (cookies != null) {
					final Cookie cookie = cookies.get(COOKIE_KEY);
					if (cookie != null) {
						final Optional<T> result = authenticator.authenticate(cookie.getValue());
						if (result.isPresent()) {
							return result.get();
						}
					}
				}
			} catch (AuthenticationException e) {
				LOGGER.warn("Error authenticating credentials", e);
				throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
			}

			if (required) {
				throw new WebApplicationException(ErrorResponseFactory.getInstance().unauthorized());
			}
			return null;
		}
	}

}
