/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.auth;

import com.google.gson.JsonObject;

import java.util.Set;

/**
 * @author Alexander De Leon
 * 
 */
public interface AuthorizationRulesService {

	/**
	 * Simple interface to obtain the set of authorization rules for a token and a audience
	 */
	Set<JsonObject> getAuthorizationRules(String token, String audience);

}
