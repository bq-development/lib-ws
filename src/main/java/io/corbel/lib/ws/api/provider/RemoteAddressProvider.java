package io.corbel.lib.ws.api.provider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import io.corbel.lib.ws.SpringJerseyProvider;
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

import io.corbel.lib.ws.annotation.RemoteAddress;

public class RemoteAddressProvider implements SpringJerseyProvider {


    public static class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(RemoteAddressFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(RemoteAddressInjectionResolver.class).to(new TypeLiteral<InjectionResolver<RemoteAddress>>() {}).in(Singleton.class);
        }
    }

    public static class RemoteAddressInjectionResolver extends ParamInjectionResolver<RemoteAddress> {
        public RemoteAddressInjectionResolver() {
            super(RemoteAddressFactoryProvider.class);
        }
    }

    @Provider public static class RemoteAddressFactoryProvider extends AbstractValueFactoryProvider {

        @Context private HttpServletRequest request;

        @Inject
        protected RemoteAddressFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Source.UNKNOWN);
        }

        @Override
        public Factory<?> createValueFactory(Parameter parameter) {
            if (parameter.getAnnotation(RemoteAddress.class) != null) {
                return new AbstractContainerRequestValueFactory<String>() {
                    @Override
                    public String provide() {
                        return request.getRemoteAddr();
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
