package io.corbel.lib.ws.digest;

import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;

/**
 * @author Alexander De Leon
 * 
 */
public class GuavaHashFunctionDigester implements Digester {

	private final HashFunction hashFunction;

	public GuavaHashFunctionDigester(HashFunction hashFunction) {
		this.hashFunction = hashFunction;
	}

	@Override
	public String digest(String text) {
		return text != null ? hashFunction.hashString(text, Charset.forName("utf8")).toString() : null;
	}

}
