package io.corbel.lib.ws.filter;

import io.corbel.lib.ws.model.CustomHeaders;

import javax.ws.rs.container.ContainerRequestContext;

/**
 * @author Francisco Sanchez
 */
public class FilterUtil {
    public static boolean hasNoRedirectHeader(ContainerRequestContext request) {
        return Boolean.parseBoolean(request.getHeaderString(CustomHeaders.NO_REDIRECT_HEADER));
    }

    public static boolean redirect(int status) {
        return status >= 300 && status < 400;
    }
}
