package com.bqreaders.silkroad.common.filter;

import com.bqreaders.silkroad.common.api.error.ErrorResponseFactory;
import com.bqreaders.silkroad.common.model.Error;
import com.sun.jersey.spi.container.ContainerRequest;

import javax.ws.rs.WebApplicationException;
import java.util.List;

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
	public ContainerRequest filter(ContainerRequest request) {
		if (methods.contains(request.getMethod().toUpperCase())) {

				if (!request.getQueryParameters().isEmpty()) {
					throw new WebApplicationException(ErrorResponseFactory.getInstance().badRequest(
							new Error("bad_request", request.getMethod()
									+ " request should not contains query parameters")));
				}

			}
		return request;
	}


}
