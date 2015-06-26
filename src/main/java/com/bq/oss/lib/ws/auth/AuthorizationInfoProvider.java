package com.bq.oss.lib.ws.auth;

import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Parameter.Source;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import com.bq.oss.lib.ws.SpringJerseyProvider;

public class AuthorizationInfoProvider implements SpringJerseyProvider {

    public static class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(AuthorizationInfoContextFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(AuthorizationInfoInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Auth>>() {}).in(Singleton.class);
        }
    }

    public static class AuthorizationInfoInjectionResolver extends ParamInjectionResolver<Auth> {
        public AuthorizationInfoInjectionResolver() {
            super(AuthorizationInfoContextFactoryProvider.class);
        }
    }

    @Provider public static class AuthorizationInfoContextFactoryProvider extends AbstractValueFactoryProvider {

        @Inject
        protected AuthorizationInfoContextFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Source.UNKNOWN);
        }

        public AuthorizationInfoContextFactoryProvider() {
            super(null, null, Source.UNKNOWN);
        }

        @Override
        public Factory<?> createValueFactory(Parameter parameter) {
            if (parameter.getRawType().equals(AuthorizationInfo.class) && parameter.getAnnotation(Auth.class) != null) {
                return new AbstractContainerRequestValueFactory<AuthorizationInfo>() {
                    @Override
                    public AuthorizationInfo provide() {
                        return (AuthorizationInfo) getContainerRequest().getProperty(
                                AuthorizationRequestFilter.AUTHORIZATION_INFO_PROPERTIES_KEY);
                    }
                };
            }
            return null;
        }
    }

    @Override
    public org.glassfish.hk2.utilities.Binder getBinder() {
        return new Binder();
    }

}
