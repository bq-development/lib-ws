package com.bq.oss.lib.ws.api.error;

import java.text.MessageFormat;

/**
 * @author Cristian del Cerro
 */
public enum ErrorMessage {

	BAD_REQUEST("Bad Request"),
	BAD_GATEWAY("Bad Gateway"),
	NOT_FOUND("Not found"),
	NOT_ALLOWED("Method not allowed"),
	CONFLICT("Conflict"),
	INVALID_ENTITY("Unprocessable entity: {0}"),
	UNAUTHORIZE("Unauthorized"),
	FORBIDDEN("Forbidden"),
	PRECONDITION_FAILED("Precondition failed: {0}");

	private final String pattern;

	ErrorMessage(String pattern) {
		this.pattern = pattern;
	}

	public String getMessage(Object... params) {
		return MessageFormat.format(pattern, params);
	}

}