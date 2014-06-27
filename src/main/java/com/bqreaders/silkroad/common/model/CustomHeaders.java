package com.bqreaders.silkroad.common.model;

/**
 * Created by alberto on 6/26/14.
 */
public enum CustomHeaders {

    NO_REDIRECT_HEADER("No-Redirect");

    private final String value;

    private CustomHeaders(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
