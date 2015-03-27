/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.digest;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alexander De Leon
 * 
 */

//TODO: I don't understand why mock(HashCode.class) throws a NullPointerException
@Ignore
public class GuavaHashFunctionDigesterTest {

	private static final String TEST_INPUT = "input";
	private static final String TEST_HASH = "123";

	@Test
	public void testDigest() {
		HashFunction hashFunctionMock = mock(HashFunction.class);
		Digester digester = new GuavaHashFunctionDigester(hashFunctionMock);
		HashCode hash = mock(HashCode.class);
		when(hash.toString()).thenReturn(TEST_HASH);
		when(hashFunctionMock.hashString(TEST_INPUT, Charset.forName("utf8"))).thenReturn(hash);
		assertThat(digester.digest(TEST_INPUT)).isEqualTo(TEST_HASH);
	}

	@Test
	public void testDigestOfNullIsNull() {
		HashFunction hashFunctionMock = mock(HashFunction.class);
		Digester digester = new GuavaHashFunctionDigester(hashFunctionMock);
		HashCode hash = mock(HashCode.class);
		when(hash.toString()).thenReturn(TEST_HASH);
		when(hashFunctionMock.hashString(TEST_INPUT, Charset.forName("utf8"))).thenReturn(hash);
		assertThat(digester.digest(null)).isNull();
	}

}
