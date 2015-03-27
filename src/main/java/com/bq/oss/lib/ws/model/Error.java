/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.net.URI;

/**
 * @author Alexander De Leon
 * 
 */
public class Error {

	private final String error;
	private final URI errorUri;
	private final String errorDescription;

	@JsonCreator
	public Error(@JsonProperty("error") String error, @JsonProperty("errorUri") URI errorUri,
			@JsonProperty("errorDescription") String errorDescription) {
		super();
		this.error = error;
		this.errorUri = errorUri;
		this.errorDescription = errorDescription;
	}

	public Error(String error, URI errorUri) {
		this(error, errorUri, null);
	}

	public Error(String error, String errorDescription) {
		this(error, null, errorDescription);
	}

	public String getError() {
		return error;
	}

	public URI getErrorUri() {
		return errorUri;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(error);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Error)) {
			return false;
		}
		Error that = (Error) obj;
		return Objects.equal(this.error, that.error) && Objects.equal(this.errorUri, that.errorUri)
				&& Objects.equal(this.errorDescription, that.errorDescription);
	}
}
