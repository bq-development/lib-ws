package io.corbel.lib.ws.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.Response;

/**
 * @author Alberto J. Rubio
 */
public class NoRedirectResponseFilter extends OptionalContainerResponseFilter {
    public NoRedirectResponseFilter(boolean enabled) {
        super(enabled);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (FilterUtil.redirect(response.getStatus()) && FilterUtil.hasNoRedirectHeader(request)) {
            response.setStatus(Response.Status.OK.getStatusCode());
        }
    }

}
