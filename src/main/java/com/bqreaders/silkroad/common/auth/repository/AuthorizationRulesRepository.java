/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.auth.repository;

import java.util.Set;

import com.google.gson.JsonObject;

/**
 * @author Alexander De Leon
 * 
 */
public interface AuthorizationRulesRepository {

	void save(String key, long timeToExpireInMillis, JsonObject... rules);

	void delete(String key);

	Set<JsonObject> get(String key);

	Long getTimeToExpire(String key);

	void addRules(String key, JsonObject... rules);

	void removeRules(String key, JsonObject... rules);

	boolean existsRules(String key);

	String getKeyForAuthorizationRules(String token, String key);

}