/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.cli;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;
import javax.ws.rs.ext.ExceptionMapper;

import com.bq.oss.lib.ws.api.error.JsonValidationExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.bq.oss.lib.ws.api.error.GenericExceptionMapper;
import com.bq.oss.lib.ws.api.error.NotFoundExceptionMapper;
import com.bq.oss.lib.ws.api.error.URISyntaxExceptionMapper;
import com.bq.oss.lib.ws.auth.AuthorizationInfoProvider;
import com.bq.oss.lib.ws.filter.OptionalContainerRequestFilter;
import com.bq.oss.lib.ws.filter.OptionalContainerResponseFilter;
import com.bq.oss.lib.ws.gson.GsonMessageReaderWriterProvider;
import com.bq.oss.lib.ws.json.serialization.EmptyEntitiesAllowedJacksonMessageBodyProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.inject.InjectableProvider;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.ConstraintViolationExceptionMapper;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Generics;

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
	}

	public void setCommandLine(CommandLineI commandLine) {
		application.setCommandLine(commandLine);
	}

	protected abstract String getName();

	protected abstract void configureService(Environment environment, ApplicationContext context);

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
				new EmptyEntitiesAllowedJacksonMessageBodyProvider(environment.getObjectMapper(), environment
						.getValidator()));
		environment.jersey().register(new AuthorizationInfoProvider());
	}

	private void configureDropWizard(Configuration configuration, ApplicationContext applicationContext) {
		configuration.setServerFactory(applicationContext.getBean(ServerFactory.class));
		configuration.setLoggingFactory(applicationContext.getBean(LoggingFactory.class));
	}

	private void configureFiltersAndInterceptors(Environment environment, ApplicationContext applicationContext) {
		// Replace exception mappers with custom implementations
		replaceExceptionMapper(environment, ConstraintViolationExceptionMapper.class,
				new JsonValidationExceptionMapper());
		replaceExceptionMapper(environment, JsonProcessingExceptionMapper.class,
				new JsonValidationExceptionMapper().new JacksonAdapter());
		environment.jersey().register(NotFoundExceptionMapper.class);
		environment.jersey().register(URISyntaxExceptionMapper.class);
		environment.jersey().register(GenericExceptionMapper.class);

		GZIPContentEncodingFilter gzipFilter = new GZIPContentEncodingFilter();

		// Configure filters
		List<OptionalContainerRequestFilter> disabledRequestFilters = new ArrayList<>(applicationContext
				.getBeansOfType(OptionalContainerRequestFilter.class).values()).stream()
				.filter(filter -> !filter.isEnabled()).collect(Collectors.toList());

		List<ContainerRequestFilter> requestFilters = new ArrayList<>(applicationContext.getBeansOfType(
				ContainerRequestFilter.class).values());
		requestFilters.add(gzipFilter);
		requestFilters.removeAll(disabledRequestFilters);
		environment.jersey().property("com.sun.jersey.spi.container.ContainerRequestFilters", requestFilters);

		List<OptionalContainerResponseFilter> disabledResponseFilters = new ArrayList<>(applicationContext
				.getBeansOfType(OptionalContainerResponseFilter.class).values()).stream()
				.filter(filter -> !filter.isEnabled()).collect(Collectors.toList());

		List<ContainerResponseFilter> responseFilters = new ArrayList<>(applicationContext.getBeansOfType(
				ContainerResponseFilter.class).values());
		responseFilters.add(gzipFilter);
		responseFilters.removeAll(disabledResponseFilters);
		environment.jersey().property("com.sun.jersey.spi.container.ContainerResponseFilters", responseFilters);

		Boolean etagEnabled = applicationContext.getEnvironment().getProperty("etag.enabled", Boolean.class);
		if (etagEnabled == null || etagEnabled.equals(true)) {
			environment.getApplicationContext().addFilter(ShallowEtagHeaderFilter.class, "*",
					EnumSet.of(DispatcherType.REQUEST));
		}

		// Configure injectable providers
		@SuppressWarnings("rawtypes")
		Map<String, InjectableProvider> providers = applicationContext.getBeansOfType(InjectableProvider.class);
		if (providers != null) {
			for (@SuppressWarnings("rawtypes")
			Map.Entry<String, InjectableProvider> entry : providers.entrySet()) {
				LOG.info("Registering provider: {}", entry.getKey());
				environment.jersey().register(entry.getValue());
			}
		}
	}

	private void replaceExceptionMapper(Environment environment,
			Class<? extends ExceptionMapper<? extends Throwable>> exceptionMapperToBeReplaced,
			ExceptionMapper<? extends Throwable> customExceptionMapper) {
		Object exceptionMapper = null;
		for (Object singleton : environment.jersey().getResourceConfig().getSingletons()) {
			if (exceptionMapperToBeReplaced.isInstance(singleton)) {
				exceptionMapper = singleton;
				break;
			}
		}
		environment.jersey().getResourceConfig().getSingletons().remove(exceptionMapper);
		environment.jersey().register(customExceptionMapper);
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
}
