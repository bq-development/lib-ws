/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.digest;

/**
 * A {@link com.bqreaders.silkroad.common.digest.Digester} hashes a given string using the underling implementation algorithm.
 * 
 * @author Alexander De Leon
 * 
 */
public interface Digester {

	String digest(String text);

}
