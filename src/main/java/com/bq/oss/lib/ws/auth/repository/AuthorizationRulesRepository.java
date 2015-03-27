/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.auth.repository;

import com.google.gson.JsonObject;

import java.util.Set;

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

    void deleteByToken(String token);
}