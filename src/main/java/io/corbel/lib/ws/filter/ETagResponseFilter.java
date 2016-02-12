package io.corbel.lib.ws.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;

public class ETagResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        String rawEtag = responseContext.getHeaderString(HttpHeaders.ETAG);
        String requestedEtag = requestContext.getHeaderString(HttpHeaders.IF_NONE_MATCH);

        if (rawEtag != null && rawEtag.equals(requestedEtag)) {
            responseContext.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            responseContext.setEntity(null);
        }

    }

}
