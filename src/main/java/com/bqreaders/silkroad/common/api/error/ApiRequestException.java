package com.bqreaders.silkroad.common.api.error;

import com.bqreaders.silkroad.common.model.Error;

public class ApiRequestException extends RuntimeException {

	private static final long serialVersionUID = 5800945984349202321L;

	private final Error error;

	public ApiRequestException(String error, String errorDescription, Exception e) {
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
