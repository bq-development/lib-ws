package io.corbel.lib.ws.auth.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.corbel.lib.token.ioc.TokenIoc;
import io.corbel.lib.token.parser.TokenParser;
import io.corbel.lib.ws.auth.*;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.oauth.OAuthFactory;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration @Import({AuthorizationBasicIoc.class, TokenIoc.class}) public class AuthorizationCommonIoc {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCommonIoc.class);

    @Bean
    public Authenticator<String, AuthorizationInfo> authenticator(@Value("${auth.audience}") String audience, TokenParser tokenParser,
            AuthorizationRulesService authorizationRulesService) {
        return new BearerTokenAuthenticator(audience, authorizationRulesService, tokenParser);
    }

    @Bean
    public AuthorizationInfoProvider getAuthorizationInfoProvider() {
        return new AuthorizationInfoProvider();
    }

    @Bean
    public UnauthorizedHandler getUnauthorizedHandler() {
        return new JsonUnauthorizedHandler();
    }

    @Bean(name = "authProvider")
    public OAuthFactory<AuthorizationInfo> getOAuthProvider(Authenticator<String, AuthorizationInfo> authenticator,
            @Value("${auth.realm}") String realm) {
        OAuthFactory<AuthorizationInfo> oAuthFactory = new OAuthFactory<>(authenticator, realm, AuthorizationInfo.class);
        oAuthFactory.responseBuilder(getUnauthorizedHandler());
        return oAuthFactory;
    }

    @Bean(name = "cookieAuthProvider")
    public CookieOAuthFactory<AuthorizationInfo> getCookieOAuthProvider(Authenticator<String, AuthorizationInfo> authenticator,
            @Value("${auth.realm}") String realm) {
        CookieOAuthFactory<AuthorizationInfo> cookieOAuthFactory = new CookieOAuthFactory<>(authenticator, realm, AuthorizationInfo.class);
        cookieOAuthFactory.responseBuilder(getUnauthorizedHandler());
        return cookieOAuthFactory;
    }

}