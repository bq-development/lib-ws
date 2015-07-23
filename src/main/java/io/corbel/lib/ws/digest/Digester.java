package io.corbel.lib.ws.digest;

/**
 * A {@link Digester} hashes a given string using the underling implementation algorithm.
 * 
 * @author Alexander De Leon
 * 
 */
public interface Digester {

	String digest(String text);

}
