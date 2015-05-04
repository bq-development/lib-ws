package com.bq.oss.lib.ws.api.error;

import com.bq.oss.lib.ws.model.Error;

public class ApiRequestException extends RuntimeException {

	private static final long serialVersionUID = 5800945984349202321L;

	private final com.bq.oss.lib.ws.model.Error error;

	public ApiRequestException(String error, String errorDescription, Throwable e) {
		super(errorDescription, e);
		this.error = new Error(error, errorDescription);
	}

	public ApiRequestException(String error, String errorDescription) {
		super(errorDescription);
		this.error = new Error(error, errorDescription);
	}

	public Error getError() {
		return error;
	}
}
