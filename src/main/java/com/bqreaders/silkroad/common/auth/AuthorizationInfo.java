/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.auth;

import java.util.Collections;
import java.util.Set;

import com.bqreaders.lib.token.reader.TokenReader;
import com.google.gson.JsonObject;

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

}
