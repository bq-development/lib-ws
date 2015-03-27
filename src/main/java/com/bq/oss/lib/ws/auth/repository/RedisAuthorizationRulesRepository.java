package com.bq.oss.lib.ws.auth.repository;

import com.google.gson.JsonObject;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Alberto J. Rubio
 */
public class RedisAuthorizationRulesRepository implements AuthorizationRulesRepository {

	private static final String AUTHORIZATION_RULES_KEY = "{0}|{1}";

	private final RedisTemplate<String, JsonObject> redisTemplate;

	public RedisAuthorizationRulesRepository(RedisTemplate<String, JsonObject> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void save(String key, long timeToExpireInMillis, JsonObject... rules) {
		redisTemplate.opsForSet().add(key, rules);
		redisTemplate.expire(key, timeToExpireInMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void delete(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public Set<JsonObject> get(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	@Override
	public Long getTimeToExpire(String key) {
		return redisTemplate.getExpire(key);
	}

	@Override
	public void addRules(String key, JsonObject... rules) {
		redisTemplate.opsForSet().add(key, rules);
	}

	@Override
	public void removeRules(String key, JsonObject... rules) {
		redisTemplate.opsForSet().remove(key, rules);
	}

	@Override
	public String getKeyForAuthorizationRules(String token, String key) {
		return MessageFormat.format(AUTHORIZATION_RULES_KEY, token, key);
	}

	@Override
	public boolean existsRules(String key) {
		return !redisTemplate.keys(key).isEmpty();
	}

    @Override
    public void deleteByToken(String token) {
        String patternToken = MessageFormat.format(AUTHORIZATION_RULES_KEY, token, "*");
        Set<String> keysToDelete = redisTemplate.keys(patternToken);
        redisTemplate.delete(keysToDelete);
    }
}
