package com.bq.oss.lib.ws.api.error;

import java.text.MessageFormat;

/**
 * @author Cristian del Cerro
 */
public enum ErrorMessage {

    BAD_REQUEST("Bad Request"), BAD_GATEWAY("Bad Gateway"), NOT_FOUND("Not found"), NOT_ALLOWED("Method not allowed"), CONFLICT("Conflict"), INVALID_ENTITY(
            "Unprocessable entity: {0}"), UNAUTHORIZE("Unauthorized"), FORBIDDEN("Forbidden"), PRECONDITION_FAILED(
            "Precondition failed: {0}"), INVALID_PAGE("Invalid api:page param : {0}, must be a natural number."), INVALID_PAGE_SIZE(
            "Invalid api:pageSize param : {0}, must be an integer in (0,{1}]"), INVALID_SORT("Invalid api:sort param: {0}, {1}"), INVALID_SEARCH(
            "Invalid api:search param: {0}, {1}"), INVALID_QUERY("Invalid api:query param: {0}, {1}"), INVALID_AGGREGATION(
            "Invalid api:aggregation param: {0}, {1}");

    private final String pattern;

    ErrorMessage(String pattern) {
        this.pattern = pattern;
    }

    public String getMessage(Object... params) {
        return MessageFormat.format(pattern, params);
    }

}