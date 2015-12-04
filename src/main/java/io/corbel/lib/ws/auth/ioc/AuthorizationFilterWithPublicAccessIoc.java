package io.corbel.lib.ws.auth.ioc;

import javax.ws.rs.container.ContainerRequestFilter;

import io.corbel.eventbus.ioc.EventBusIoc;
import io.corbel.eventbus.service.EventBus;
import io.corbel.lib.ws.auth.ioc.condition.PublicAccessEnabledCondition;
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
@Configuration
@Import({AuthorizationCommonIoc.class, EventBusIoc.class})
@Conditional(PublicAccessEnabledCondition.class)
public class AuthorizationFilterWithPublicAccessIoc {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationFilterWithPublicAccessIoc.class);

    @Bean
    public PublicAccessService getPublicAccessService(AuthorizationRulesService authorizationRulesService,
            @Value("${auth.waitTimeForPublishPublicScopes:500}") Integer waitTimeForPublishPublicScopes, EventBus eventBus,
            @Value("${auth.audience}") String audience) {
        return new DefaultPublicAccessService(authorizationRulesService, waitTimeForPublishPublicScopes, eventBus, audience);
    }

    @Bean
    public ContainerRequestFilter getAuthorizationRequestFilter(OAuthFactory<AuthorizationInfo> oauthProvider,
            CookieOAuthFactory<AuthorizationInfo> cookieOauthProvider, PublicAccessService publicAccessService,
            @Value("${auth.enabled}") boolean authEnabled, @Value("${auth.unAuthenticatedPath}") String unAuthenticatedPath,
            @Value("${auth.checkDomain.enabled:false}") boolean checkDomain) {
        if (authEnabled) {
            return new AuthorizationRequestFilter(oauthProvider, cookieOauthProvider, publicAccessService, unAuthenticatedPath,
                    checkDomain);
        } else {
            LOG.warn("Authorization validation is disabled. The system runs in INSECURE mode");
            return emptyFilter();
        }
    }

    private ContainerRequestFilter emptyFilter() {
        return request -> {};
    }
}