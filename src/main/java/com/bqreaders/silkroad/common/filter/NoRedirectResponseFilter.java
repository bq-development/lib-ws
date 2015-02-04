package com.bqreaders.silkroad.common.filter;

import javax.ws.rs.core.Response;

import com.bqreaders.silkroad.common.model.CustomHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;

/**
 * Created by Alberto J. Rubio
 */
public class NoRedirectResponseFilter extends OptionalContainerResponseFilter {
	public NoRedirectResponseFilter(boolean enabled) {
		super(enabled);
	}

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		if (redirect(response.getStatus())
				&& Boolean.parseBoolean(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.toString()))) {
			response.setStatus(Response.Status.NO_CONTENT.getStatusCode());
		}
		return response;
	}

	private boolean redirect(int status) {
		return status >= 300 && status < 400;
	}
}
