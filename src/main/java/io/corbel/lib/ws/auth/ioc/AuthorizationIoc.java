package io.corbel.lib.ws.auth.ioc;

import javax.ws.rs.container.ContainerRequestFilter;

import io.corbel.eventbus.ioc.EventBusIoc;
import io.corbel.eventbus.service.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

import com.google.gson.JsonObject;

import io.corbel.lib.token.ioc.TokenIoc;
import io.corbel.lib.token.parser.TokenParser;
import io.corbel.lib.ws.auth.*;
import io.corbel.lib.ws.auth.repository.AuthorizationRulesRepository;
import io.corbel.lib.ws.auth.repository.RedisAuthorizationRulesRepository;
import io.corbel.lib.ws.filter.InformationResponseFilter;
import io.corbel.lib.ws.health.AuthorizationRedisHealthCheck;
import io.corbel.lib.ws.redis.GsonRedisSerializer;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.oauth.OAuthFactory;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration @Import({TokenIoc.class, EventBusIoc.class}) public class AuthorizationIoc {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationIoc.class);

    @Bean
    public AuthorizationRulesRepository getAuthorizationRulesRepository(RedisTemplate<String, JsonObject> redisTemplate) {
        return new RedisAuthorizationRulesRepository(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, JsonObject> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, JsonObject> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GsonRedisSerializer<JsonObject>());
        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig, @Value("${auth.redis.host:@null}") String host,
            @Value("${auth.redis.port:@null}") Integer port, @Value("${auth.redis.password:}") String password) {
        JedisConnectionFactory connFactory = new JedisConnectionFactory(jedisPoolConfig);
        connFactory.setPassword(password);
        if (host != null) {
            connFactory.setHostName(host);
        }
        if (port != null) {
            connFactory.setPort(port);
        }
        return connFactory;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(@Value("${auth.redis.maxIdle:@null}") Integer maxIdle,
            @Value("${auth.redis.maxTotal:@null}") Integer maxTotal, @Value("${auth.redis.minIdle:@null}") Integer minIdle,
            @Value("${auth.redis.testOnBorrow:@null}") Boolean testOnBorrow,
            @Value("${auth.redis.testOnReturn:@null}") Boolean testOnReturn,
            @Value("${auth.redis.testWhileIdle:@null}") Boolean testWhileIdle,
            @Value("${auth.redis.numTestsPerEvictionRun:@null}") Integer numTestsPerEvictionRun,
            @Value("${auth.redis.maxWaitMillis:@null}") Long maxWaitMillis,
            @Value("${auth.redis.timeBetweenEvictionRunsMillis:@null}") Long timeBetweenEvictionRunsMillis,
            @Value("${auth.redis.blockWhenExhausted:@null}") Boolean blockWhenExhausted) {
        JedisPoolConfig config = new JedisPoolConfig();
        if (maxIdle != null) {
            config.setMaxIdle(maxIdle);
        }
        if (maxTotal != null) {
            config.setMaxTotal(maxTotal);
        }
        if (minIdle != null) {
            config.setMinIdle(minIdle);
        }
        if (testOnBorrow != null) {
            config.setTestOnBorrow(testOnBorrow);
        }
        if (testOnReturn != null) {
            config.setTestOnReturn(testOnReturn);
        }
        if (testWhileIdle != null) {
            config.setTestWhileIdle(testWhileIdle);
        }
        if (numTestsPerEvictionRun != null) {
            config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        }
        if (timeBetweenEvictionRunsMillis != null) {
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if (maxWaitMillis != null) {
            config.setMaxWaitMillis(maxWaitMillis);
        }
        if (blockWhenExhausted != null) {
            config.setBlockWhenExhausted(blockWhenExhausted);
        }
        return config;
    }

    @Bean
    public AuthorizationRulesService authorizationRulesService(AuthorizationRulesRepository authorizationRulesRepository) {
        return new DefaultAuthorizationRulesService(authorizationRulesRepository);
    }

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

    @Bean
    public PublicAccessService getPublicAccessService(AuthorizationRulesService authorizationRulesService,
            @Value("${auth.waitTimeForPublishPublicScopes:1000}") Integer waitTimeForPublishPublicScopes, EventBus eventBus,
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

    @Bean
    public AuthorizationRedisHealthCheck getAuthorizationRedisHealthCheck(RedisTemplate<String, JsonObject> redisTemplate) {
        return new AuthorizationRedisHealthCheck(redisTemplate);
    }

    @Bean
    public InformationResponseFilter getRequestInformationRequestFilter() {
        return new InformationResponseFilter();
    }

    private ContainerRequestFilter emptyFilter() {
        return request -> {};
    }

}