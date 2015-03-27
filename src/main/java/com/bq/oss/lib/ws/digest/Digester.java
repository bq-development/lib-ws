/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.digest;

/**
 * A {@link Digester} hashes a given string using the underling implementation algorithm.
 * 
 * @author Alexander De Leon
 * 
 */
public interface Digester {

	String digest(String text);

}
