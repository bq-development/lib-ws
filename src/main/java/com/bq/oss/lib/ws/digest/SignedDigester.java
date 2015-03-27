/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.digest;

/**
 * A {@link SignedDigester} hashes the given string together with a secret (SALT)
 * 
 * @author Alexander De Leon
 * 
 */
public class SignedDigester implements Digester {

	private final Digester delegate;
	private final String secret;

	SignedDigester(Digester digester, String secret) {
		this.delegate = digester;
		this.secret = secret;
	}

	@Override
	public String digest(String text) {
		return delegate.digest(text + secret);
	}

}
