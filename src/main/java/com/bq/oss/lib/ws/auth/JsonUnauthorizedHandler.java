package com.bq.oss.lib.ws.auth;

import io.dropwizard.auth.UnauthorizedHandler;

import javax.ws.rs.core.Response;

import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;

public class JsonUnauthorizedHandler implements UnauthorizedHandler {

    @Override
    public Response buildResponse(String prefix, String realm) {
        return ErrorResponseFactory.getInstance().unauthorized();
    }

}
