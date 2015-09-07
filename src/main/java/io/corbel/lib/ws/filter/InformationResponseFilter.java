package io.corbel.lib.ws.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.corbel.lib.ws.auth.AuthorizationInfo;
import io.corbel.lib.ws.auth.AuthorizationRequestFilter;

public class InformationResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(InformationResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext)
            throws IOException {
        String authorizationMsg = getAuthorizationLog(containerRequestContext);
        LOG.info("Request {} {} {} result: {}, location: {}, length: {} {}",
                    containerRequestContext.getMethod(), containerRequestContext.getUriInfo().getAbsolutePath(),
                    containerRequestContext.getMediaType(), containerResponseContext.getStatus(), containerResponseContext.getLocation(),
                    containerResponseContext.getLength(),authorizationMsg);
    }

    private String getAuthorizationLog(ContainerRequestContext containerRequestContext) {
        Object property = containerRequestContext.getProperty(AuthorizationRequestFilter.AUTHORIZATION_INFO_PROPERTIES_KEY);
        if (property != null) {
            AuthorizationInfo authorizationInfo = (AuthorizationInfo) property;
            return ", from domain: " + authorizationInfo.getDomainId() +
                    ", client: " + authorizationInfo.getClientId() +
                    ", user: " +authorizationInfo.getUserId();
        } else {
            return "";
        }
    }

}
