package io.corbel.lib.ws.redis;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author Alexander De Leon
 * 
 */
public class GsonRedisSerializer<T extends JsonElement> implements RedisSerializer<T> {

	private final JsonParser parser = new JsonParser();

	@Override
	public byte[] serialize(T t) throws SerializationException {
		return t.toString().getBytes();
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		String jsonString = new String(bytes);
		return (T) parser.parse(jsonString);
	}

}
