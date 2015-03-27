/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.auth;

import com.bq.oss.lib.token.reader.TokenReader;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Set;

/**
 * @author Alexander De Leon
 * 
 */
public class AuthorizationInfo {

	private final TokenReader tokenReader;
	private final Set<JsonObject> accessRules;

	public AuthorizationInfo(TokenReader tokenReader, Set<JsonObject> accessRules) {
		super();
		this.tokenReader = tokenReader;
		this.accessRules = accessRules;
	}

	public TokenReader getTokenReader() {
		return tokenReader;
	}

	public Set<JsonObject> getAccessRules() {
		return Collections.unmodifiableSet(accessRules);
	}

	public String getUserId() {
		return getTokenReader().getInfo().getUserId();
	}

	public String getClientId() {
		return getTokenReader().getInfo().getClientId();
	}

	public String getDomainId() {
		return getTokenReader().getInfo().getDomainId();
	}

	public String getToken() {
		return getTokenReader().getToken();
	}

}
