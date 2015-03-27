/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.auth;

import com.google.gson.JsonObject;

import java.util.Set;

/**
 * @author Alexander De Leon
 * 
 */
public interface AuthorizationRulesService {

	Set<JsonObject> getAuthorizationRules(String token, String audience);

}
