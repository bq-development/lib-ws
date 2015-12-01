package io.corbel.lib.ws.auth;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.corbel.eventbus.service.EventBus;
import io.corbel.lib.token.exception.TokenVerificationException;
import io.dropwizard.auth.AuthenticationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Alberto J. Rubio
 *
 */
public class DefaultPublicAccessServiceTest {

    private static final Integer WAIT_TIME_FOR_PUBLISH_PUBLIC_SCOPES = 200;
    private static final String TEST_DOMAIN = "test-domain";
    private static final String TEST_AUDIENCE = "audience";

    private DefaultPublicAccessService publicAccessService;
    private AuthorizationRulesService authorizationRulesServiceMock;
    private EventBus eventBus;

    @Before
    public void setup() throws TokenVerificationException {
        authorizationRulesServiceMock = mock(AuthorizationRulesService.class);
        eventBus = mock(EventBus.class);

        publicAccessService = new DefaultPublicAccessService(authorizationRulesServiceMock, WAIT_TIME_FOR_PUBLISH_PUBLIC_SCOPES,
                eventBus, TEST_AUDIENCE);
    }

    @Test
    public void testDomainWithEmptyPublicScopes() {
        Set<JsonObject> publicRules = publicAccessService.getDomainPublicRules(TEST_DOMAIN);
        assertThat(publicRules.size()).isEqualTo(0);
    }

    @Test
    public void testDomainWithPublicScopes() {
        Set<JsonObject> set = new HashSet<>();
        JsonObject rule = new JsonObject();
        rule.add("a", new JsonPrimitive("1"));
        set.add(rule);
        when(authorizationRulesServiceMock.getAuthorizationRules(TEST_DOMAIN +
                DefaultPublicAccessService.PUBLIC_SCOPES_SUFFIX, TEST_AUDIENCE)).thenReturn(set);
        Set<JsonObject> publicRules = publicAccessService.getDomainPublicRules(TEST_DOMAIN);
        assertThat(publicRules).isEqualTo(set);
    }

    @Test
    public void testDomainWithPublicScopesNotPublished() {
        when(authorizationRulesServiceMock.existsRulesForToken(TEST_DOMAIN +
                DefaultPublicAccessService.PUBLIC_SCOPES_SUFFIX, TEST_AUDIENCE)).thenReturn(false);
        when(authorizationRulesServiceMock.getAuthorizationRules(TEST_DOMAIN +
                DefaultPublicAccessService.PUBLIC_SCOPES_SUFFIX, TEST_AUDIENCE)).thenReturn(Collections.emptySet());
        Set<JsonObject> publicRules = publicAccessService.getDomainPublicRules(TEST_DOMAIN);
        assertThat(publicRules.size()).isEqualTo(0);
    }

}
