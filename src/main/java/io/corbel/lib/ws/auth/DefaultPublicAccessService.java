package io.corbel.lib.ws.auth;

import com.google.gson.JsonObject;
import io.corbel.event.DomainPublicScopesNotPublishedEvent;
import io.corbel.eventbus.service.EventBus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Alberto J. Rubio
 *
 */
public class DefaultPublicAccessService implements PublicAccessService {

    public static final String PUBLIC_SCOPES_SUFFIX = "_public_scopes";

    private final AuthorizationRulesService authorizationRulesService;
    private final Integer waitTimeForPublishPublicScopes;
    private final EventBus eventBus;
    private final String audience;


    public DefaultPublicAccessService(AuthorizationRulesService authorizationRulesService, Integer waitTimeForPublishPublicScopes, EventBus eventBus, String audience) {
        this.authorizationRulesService = authorizationRulesService;
        this.waitTimeForPublishPublicScopes = waitTimeForPublishPublicScopes;
        this.eventBus = eventBus;
        this.audience = audience;
    }

    @Override
    public Set<JsonObject> getDomainPublicRules(String domainId) {
        if (domainId != null) {
            String token = domainId + PUBLIC_SCOPES_SUFFIX;
            if(!authorizationRulesService.existsRulesForToken(token, audience)) {
                eventBus.dispatch(new DomainPublicScopesNotPublishedEvent(domainId));
                waitPublicScopesArePublished();
            }
            return authorizationRulesService.getAuthorizationRules(token, audience);
        }
        return Collections.EMPTY_SET;
    }

    private void waitPublicScopesArePublished() {
        try {
            TimeUnit.MILLISECONDS.sleep(waitTimeForPublishPublicScopes);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
