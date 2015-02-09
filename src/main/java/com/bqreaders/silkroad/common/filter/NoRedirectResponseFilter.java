package com.bqreaders.silkroad.common.filter;

import javax.ws.rs.core.Response;

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
		if (FilterUtil.redirect(response.getStatus())
				&& FilterUtil.hasNoRedirectHeader(request)) {
			response.setStatus(Response.Status.NO_CONTENT.getStatusCode());
		}
		return response;
	}

}
