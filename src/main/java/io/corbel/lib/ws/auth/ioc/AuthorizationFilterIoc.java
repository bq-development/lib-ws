package io.corbel.lib.ws.auth.ioc;

import javax.ws.rs.container.ContainerRequestFilter;

import io.corbel.lib.ws.auth.ioc.condition.PublicAccessDisabledCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import io.corbel.lib.ws.auth.*;
import io.dropwizard.auth.oauth.OAuthFactory;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration @Import({
        AuthorizationCommonIoc.class}) @Conditional(PublicAccessDisabledCondition.class) public class AuthorizationFilterIoc {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilterIoc.class);

    @Bean
    public ContainerRequestFilter getAuthorizationRequestFilter(OAuthFactory<AuthorizationInfo> oauthProvider,
            CookieOAuthFactory<AuthorizationInfo> cookieOauthProvider, @Value("${auth.enabled}") boolean authEnabled,
            @Value("${auth.unAuthenticatedPath}") String unAuthenticatedPath,
            @Value("${auth.checkDomain.enabled:false}") boolean checkDomain,
            @Value("${filter.allowRequestWithoutDomainInUri.endpoints:}") String endpoints) {
        if (authEnabled) {
            return new AuthorizationRequestFilter(oauthProvider, cookieOauthProvider, unAuthenticatedPath, checkDomain, endpoints);
        } else {
            LOG.warn("Authorization validation is disabled. The system runs in INSECURE mode");
            return emptyFilter();
        }
    }

    private ContainerRequestFilter emptyFilter() {
        return request -> {};
    }
}