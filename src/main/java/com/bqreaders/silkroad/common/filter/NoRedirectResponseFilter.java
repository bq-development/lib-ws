package com.bqreaders.silkroad.common.filter;

import com.bqreaders.silkroad.common.model.CustomHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.Response;

/**
 * Created by Alberto J. Rubio
 */
public class NoRedirectResponseFilter implements ContainerResponseFilter {

    private final boolean enabled;

    public NoRedirectResponseFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        if (enabled && redirect(response.getStatus())
                && Boolean.parseBoolean(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.getValue()))) {
            response.setStatus(Response.Status.NO_CONTENT.getStatusCode());
        }
        return response;
    }

    private boolean redirect(int status) {
        return status >= 300 && status < 400;
    }
}
