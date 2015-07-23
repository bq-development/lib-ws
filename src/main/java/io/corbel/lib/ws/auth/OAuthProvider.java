package io.corbel.lib.ws.auth;

import io.corbel.lib.ws.SpringJerseyProvider;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

public class OAuthProvider implements SpringJerseyProvider {

    private static AuthFactory factory;

    public OAuthProvider(AuthFactory factory) {
        this.factory = factory;
    }

    public static class AuthInjectionResolver extends ParamInjectionResolver<Auth> {
        public AuthInjectionResolver() {
            super(AuthFactoryProvider.class);
        }
    }

    @Provider public static class AuthFactoryProvider extends AbstractValueFactoryProvider {


        @Inject
        public AuthFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Parameter.Source.UNKNOWN);
        }


        @Override
        protected Factory<?> createValueFactory(final Parameter parameter) {
            final Class<?> classType = parameter.getRawType();
            final Auth auth = parameter.getAnnotation(Auth.class);

            if (auth != null && classType.isAssignableFrom(factory.getGeneratedClass())) {
                return factory.clone(auth.required());
            } else {
                return null;
            }
        }
    }

    @Override
    public org.glassfish.hk2.utilities.Binder getBinder() {
        return new Binder();
    }

    public static class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(factory).to(AuthFactory.class);
            bind(AuthFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(AuthInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Auth>>() {}).in(Singleton.class);
        }

    }

}
