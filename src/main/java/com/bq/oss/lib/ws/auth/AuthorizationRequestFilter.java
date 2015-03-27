/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.auth;

import com.bq.oss.lib.token.TokenInfo;
import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.oauth.OAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.util.Set;

import static java.util.stream.StreamSupport.stream;

/**
 * This class is a bit of a hack to Dropwizard(Jersey 1.17). It uses the {@link io.dropwizard.auth.oauth.OAuthProvider}
 * class to obtain an instance of {@link AuthorizationInfo}. The filter is configured
 * to only verify the set of request whose path matches the specified pattern. It validates only access rules of type
 * <b>http_access</b>. If request cannot proceed, it returns a HTTP error 401 without any error information.
 * 
 * @author Alexander De Leon
 * 
 */
public class AuthorizationRequestFilter implements ContainerRequestFilter {

	public static final String AUTHORIZATION_INFO_PROPERTIES_KEY = "AuthorizationInfo";

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationRequestFilter.class);

	private final OAuthProvider<AuthorizationInfo> oAuthProvider;
	private final CookieOAuthProvider<AuthorizationInfo> cookieOAuthProvider;
	private final String unAuthenticatedPathPattern;
	// Injected by Jersey
	@Context
	private HttpContext context;

	public AuthorizationRequestFilter(OAuthProvider<AuthorizationInfo> provider,
			CookieOAuthProvider<AuthorizationInfo> cookieOAuthProvider, String unAuthenticatedPathPattern) {
		this.oAuthProvider = provider;
		this.cookieOAuthProvider = cookieOAuthProvider;
		this.unAuthenticatedPathPattern = unAuthenticatedPathPattern;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		if (!request.getPath().matches(unAuthenticatedPathPattern)) {
			// OPTIONS is always allowed (for CORS)
			if (!request.getMethod().equals(HttpMethod.OPTIONS)) {
				// Obtain the authorization information from the bearer token.
				AuthorizationInfo info = ((AbstractHttpContextInjectable<AuthorizationInfo>) oAuthProvider
						.getInjectable(null, getAuth(false), null)).getValue(context);
				if (info == null) {
					info = ((AbstractHttpContextInjectable<AuthorizationInfo>) cookieOAuthProvider.getInjectable(null,
							getAuth(true), null)).getValue(context);
				}
				// Check rules to verify access
				checkAccessRules(info, request);
				storeAuthorizationInfoInRequestProperties(info, request);
			}
		}
		// If we have reach this point... then request is ok to proceed
		return request;
	}

	private void checkAccessRules(final AuthorizationInfo info, final ContainerRequest request) {
		Set<JsonObject> applicableRules = Sets.filter(info.getAccessRules(), new Predicate<JsonObject>() {
			@Override
			public boolean apply(JsonObject rule) {
				return matchesMethod(request.getMethod(), rule) && matchesUriPath(request.getPath(), rule)
						&& matchesMediaTypes(request, rule) && matchesTokenType(info.getTokenReader().getInfo(), rule);
			}
		});

		// if no rules apply then by default access is denied
		if (applicableRules.isEmpty()) {
			throw new WebApplicationException(ErrorResponseFactory.getInstance().unauthorized());
		}
	}

	private void storeAuthorizationInfoInRequestProperties(AuthorizationInfo info, ContainerRequest request) {
		request.getProperties().put(AUTHORIZATION_INFO_PROPERTIES_KEY, info);
	}

	private boolean matchesTokenType(TokenInfo token, JsonObject rule) {
		if (!rule.has("tokenType")) {
			// if no tokenType is defined there's nothing to check.
			return true;
		}
		String value = rule.get("tokenType").getAsString();
		switch (value) {
			case "user":
				return token.getUserId() != null;
			default:
				return false; // if we don't know what that value means then fail the rule
		}
	}

	private boolean matchesMethod(String method, JsonObject input) {
		if (!input.has("methods")) {
			LOG.warn("Http access rule without methods field: {}", input.toString());
			return false;
		}
		JsonArray methods = input.get("methods").getAsJsonArray();
		for (JsonElement jsonMethod : methods) {
			if (method.equals(jsonMethod.getAsString())) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesMediaTypes(ContainerRequest request, JsonObject input) {
		if (!input.has("mediaTypes")) {
			LOG.warn("Http access rule without mediaTypes field: {}", input.toString());
			return false;
		}
		JsonArray mediaTypesArray = input.get("mediaTypes").getAsJsonArray();

		for (MediaType mediaType : request.getAcceptableMediaTypes()) {
			if (stream(mediaTypesArray.spliterator(), true).map(
					mediatypeJsonElement -> MediaType.valueOf(mediatypeJsonElement.getAsString())).anyMatch(
					ruleMediaType -> {
						return mediaType.isCompatible(ruleMediaType);
					})) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesUriPath(String path, JsonObject input) {
		if (!input.has("uri")) {
			LOG.warn("Http access rule without uri field: {}", input.toString());
			return false;
		}
		return path.matches(input.get("uri").getAsString());
	}

	private Auth getAuth(boolean b) {
		return new Auth() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return Auth.class;
			}

			@Override
			public boolean required() {
				return b;
			}
		};
	}

}
