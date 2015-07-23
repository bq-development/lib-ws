package io.corbel.lib.ws.filter;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.MDC;

@Priority(0) public class RequestIdFilter implements ContainerRequestFilter {

    public static final String REQUESTID = "requestId";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String requestid = UUID.randomUUID().toString();
        MDC.put(REQUESTID, requestid);
    }

}
