package io.corbel.lib.ws.repository;

import io.corbel.lib.ws.auth.repository.RedisAuthorizationRulesRepository;
import io.corbel.lib.ws.auth.repository.AuthorizationRulesRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.concurrent.TimeUnit;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Alberto J. Rubio
 */
public class AuthorizationRulesRepositoryTest {

	private SetOperations<String, JsonObject> operations;
	private RedisTemplate<String, JsonObject> template;
	private AuthorizationRulesRepository repository;

	private JsonObject testRule;
	private static final String TEST_KEY = "testKey";
	private static final long TEST_TTL = 1000;

	@Before
	public void setUp() {

		operations = Mockito.mock(SetOperations.class);
		template = Mockito.mock(RedisTemplate.class);
		when(template.opsForSet()).thenReturn(operations);

		repository = new RedisAuthorizationRulesRepository(template);

		testRule = new JsonObject();
		testRule.add("_id", new JsonPrimitive("testId"));
	}

	@Test
	public void testSave() {
		repository.save(TEST_KEY, TEST_TTL, testRule);
		Mockito.verify(operations).add(TEST_KEY, testRule);
		Mockito.verify(template).expire(TEST_KEY, TEST_TTL, TimeUnit.MILLISECONDS);
	}

	@Test
	public void testDelete() {
		repository.delete(TEST_KEY);
		Mockito.verify(template).delete(TEST_KEY);
	}

	@Test
	public void testGet() {
		repository.get(TEST_KEY);
		Mockito.verify(operations).members(TEST_KEY);
	}

	@Test
	public void testGetTimeToExpire() {
		repository.getTimeToExpire(TEST_KEY);
		Mockito.verify(template).getExpire(TEST_KEY);
	}

	@Test
	public void testAddRules() {
		repository.addRules(TEST_KEY, testRule);
		Mockito.verify(operations).add(TEST_KEY, testRule);
	}

	@Test
	public void testRemoveRules() {
		repository.removeRules(TEST_KEY, testRule);
		Mockito.verify(operations).remove(TEST_KEY, testRule);
	}

	@Test
	public void testGetKey() {
		assertThat(repository.getKeyForAuthorizationRules("token", "aud")).isEqualTo("token|aud");
	}
}
