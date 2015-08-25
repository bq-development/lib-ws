package io.corbel.lib.ws.filter;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

import io.corbel.lib.ws.auth.priority.CorbelPriorities;
import org.slf4j.MDC;

@PreMatching
@Priority(CorbelPriorities.REQUEST_ID_FILTER)
public class RequestIdFilter implements ContainerRequestFilter {

    public static final String REQUESTID = "requestId";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final String requestid = UUID.randomUUID().toString();
        MDC.put(REQUESTID, requestid);
    }

}
