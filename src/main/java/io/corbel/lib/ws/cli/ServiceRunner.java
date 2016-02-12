package io.corbel.lib.ws.cli;

import io.corbel.lib.ws.SpringJerseyProvider;
import io.corbel.lib.ws.api.error.GenericExceptionMapper;
import io.corbel.lib.ws.api.error.JsonValidationExceptionMapper;
import io.corbel.lib.ws.api.error.NotFoundExceptionMapper;
import io.corbel.lib.ws.api.error.URISyntaxExceptionMapper;
import io.corbel.lib.ws.filter.ChunkedAwaredShallowEtagHeaderFilter;
import io.corbel.lib.ws.filter.OptionalContainerRequestFilter;
import io.corbel.lib.ws.filter.OptionalContainerResponseFilter;
import io.corbel.lib.ws.gson.GsonMessageReaderWriterProvider;
import io.corbel.lib.ws.json.serialization.EmptyEntitiesAllowedJacksonMessageBodyProvider;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Generics;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;

import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.HttpMethodOverrideFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Alexander De Leon
 * 
 */
public abstract class ServiceRunner<T> {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceRunner.class);
    private final ServiceRunnerApplication application = new ServiceRunnerApplication();

    public final void run(String[] arguments) throws Exception {
        LOG.info("Initializing ${conf.namespace} as {}", getName());
        System.setProperty("conf.namespace", getName());
        application.run(arguments);
        // Rewrite Dropwizard configuration in order to use a logback.xml file.
        resetLogConfig();
    }

    public void setCommandLine(CommandLineI commandLine) {
        application.setCommandLine(commandLine);
    }

    protected abstract String getName();

    protected void configureService(Environment environment, ApplicationContext context) {
        environment.jersey().property(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, false);
    }

    protected void bootstrap(Bootstrap<Configuration> bootstrap) {}

    protected void configureObjectMapper(ObjectMapper objectMapperFactory) {
        objectMapperFactory.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapperFactory.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    protected ApplicationContext loadSpringContext() {
        Class<T> ioc = getIocConfigurationClass();
        LOG.info("Loading spring context from class {}", ioc);
        return new AnnotationConfigApplicationContext(ioc);
    }

    private void configureDefaultProviders(Environment environment) {
        environment.jersey().register(new GsonMessageReaderWriterProvider());
        environment.jersey().register(
                new EmptyEntitiesAllowedJacksonMessageBodyProvider(environment.getObjectMapper(), environment.getValidator()));
    }

    private void configureDropWizard(Configuration configuration, ApplicationContext applicationContext) {
        configuration.setServerFactory(applicationContext.getBean(ServerFactory.class));
        configuration.setLoggingFactory(applicationContext.getBean(LoggingFactory.class));
    }

    private void configureFiltersAndInterceptors(Environment environment, ApplicationContext applicationContext) {
        // Replace exception mappers with custom implementations
        environment.jersey().register(new JsonValidationExceptionMapper());
        environment.jersey().register(new JsonValidationExceptionMapper().new JacksonAdapter());
        environment.jersey().register(NotFoundExceptionMapper.class);
        environment.jersey().register(URISyntaxExceptionMapper.class);
        environment.jersey().register(GenericExceptionMapper.class);


        environment.jersey().getResourceConfig().registerClasses(EncodingFilter.class, GZipEncoder.class, DeflateEncoder.class);


        Boolean etagEnabled = applicationContext.getEnvironment().getProperty("etag.enabled", Boolean.class);
        if (etagEnabled == null || etagEnabled.equals(true)) {
            if (applicationContext.getEnvironment().getProperty("etag.chunked.enabled", Boolean.class, false)) {
                environment.getApplicationContext().addFilter(ChunkedAwaredShallowEtagHeaderFilter.class, "*",
                        EnumSet.of(DispatcherType.REQUEST));
            } else {
                environment.getApplicationContext().addFilter(ShallowEtagHeaderFilter.class, "*", EnumSet.of(DispatcherType.REQUEST));
            }
        }

        // Configure filters
        Boolean httpMethodOverrideEnabled = applicationContext.getEnvironment().getProperty("filter.httpTunnelingFilter.enabled",
                Boolean.class, true);
        if (httpMethodOverrideEnabled) {
            environment.jersey().register(HttpMethodOverrideFilter.class);
        }

        List<OptionalContainerRequestFilter> disabledRequestFilters = new ArrayList<>(applicationContext.getBeansOfType(
                OptionalContainerRequestFilter.class).values()).stream().filter(filter -> !filter.isEnabled()).collect(Collectors.toList());

        List<ContainerRequestFilter> requestFilters = new ArrayList<>(applicationContext.getBeansOfType(ContainerRequestFilter.class)
                .values());
        requestFilters.removeAll(disabledRequestFilters);
        requestFilters.forEach(filter -> {
            environment.jersey().register(filter);
        });

        List<OptionalContainerResponseFilter> disabledResponseFilters = new ArrayList<>(applicationContext.getBeansOfType(
                OptionalContainerResponseFilter.class).values()).stream().filter(filter -> !filter.isEnabled())
                .collect(Collectors.toList());

        List<ContainerResponseFilter> responseFilters = new ArrayList<>(applicationContext.getBeansOfType(ContainerResponseFilter.class)
                .values());
        responseFilters.removeAll(disabledResponseFilters);
        responseFilters.forEach(filter -> {
            environment.jersey().register(filter);
        });

        Map<String, SpringJerseyProvider> providers = applicationContext.getBeansOfType(SpringJerseyProvider.class);
        if (providers != null) {
            for (@SuppressWarnings("rawtypes")
            Map.Entry<String, SpringJerseyProvider> entry : providers.entrySet()) {
                LOG.info("Registering provider: {}", entry.getKey());
                environment.jersey().register(entry.getValue().getBinder());
            }
        }
    }

    private Class<T> getIocConfigurationClass() {
        return Generics.getTypeParameter(getClass(), Object.class);
    }

    class ServiceRunnerApplication extends Application<Configuration> {

        private final CliCommand cliCommand = new CliCommand("cli", "Command line shell.");

        @Override
        public void initialize(Bootstrap<Configuration> bootstrap) {
            configureObjectMapper(bootstrap.getObjectMapper());
            bootstrap(bootstrap);
            bootstrap.addCommand(cliCommand);
        }

        @Override
        public void run(Configuration configuration, Environment environment) {
            configureDefaultProviders(environment);
            ApplicationContext applicationContext = loadSpringContext();
            configureDropWizard(configuration, applicationContext);
            configureFiltersAndInterceptors(environment, applicationContext);
            configureService(environment, applicationContext);
        }

        public void setCommandLine(CommandLineI commandLine) {
            cliCommand.setCommandLine(commandLine);
        }
    }

    private void resetLogConfig() throws JoranException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();
        ContextInitializer initializer = new ContextInitializer(context);
        initializer.autoConfig();
    }
}
