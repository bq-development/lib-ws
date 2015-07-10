package com.bq.oss.lib.ws.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bq.oss.lib.ws.auth.AuthorizationInfo;
import com.bq.oss.lib.ws.auth.AuthorizationRequestFilter;

@Priority(Priorities.USER) public class RequestInformationRequestFilter implements ContainerRequestFilter {


    private static final Logger LOG = LoggerFactory.getLogger(RequestInformationRequestFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Object property = requestContext.getProperty(AuthorizationRequestFilter.AUTHORIZATION_INFO_PROPERTIES_KEY);
        if (property != null) {
            AuthorizationInfo authorizationInfo = (AuthorizationInfo) property;
            LOG.info("Request to {} from domain: {}, client: {}, user: {}", requestContext.getUriInfo().getAbsolutePath(),
                    authorizationInfo.getDomainId(), authorizationInfo.getClientId(), authorizationInfo.getUserId());
        }
    }

}
