package io.corbel.lib.ws.model;

/**
 * @author Alberto J. Rubio
 */
public enum CustomHeaders {

	NO_REDIRECT_HEADER("No-Redirect"),
	REQUEST_COOKIE_HEADER("RequestCookie");

	private final String value;

	private CustomHeaders(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
