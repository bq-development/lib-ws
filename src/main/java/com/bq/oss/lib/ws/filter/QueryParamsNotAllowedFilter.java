package com.bq.oss.lib.ws.filter;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;

import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.bq.oss.lib.ws.model.Error;

/**
 * Created by Francisco Sanchez on 13/01/15.
 */
public class QueryParamsNotAllowedFilter extends OptionalContainerRequestFilter {
    private final List<String> methods;

    public QueryParamsNotAllowedFilter(boolean enabled, List methods) {
        super(enabled);
        this.methods = methods;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        if (methods.contains(request.getMethod().toUpperCase())) {

            if (!request.getUriInfo().getQueryParameters().isEmpty()) {
                throw new WebApplicationException(ErrorResponseFactory.getInstance().badRequest(
                        new Error("bad_request", request.getMethod() + " request should not contains query parameters")));
            }

        }
    }


}
