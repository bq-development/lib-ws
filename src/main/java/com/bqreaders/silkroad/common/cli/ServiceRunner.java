/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.cli;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.ws.rs.ext.ExceptionMapper;

import com.bqreaders.silkroad.common.filter.TransformNullBodiesToEmptyObjectsFilter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.jersey.validation.ConstraintViolationExceptionMapper;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import com.bqreaders.silkroad.common.api.error.GenericExceptionMapper;
import com.bqreaders.silkroad.common.api.error.JsonValidationExceptionMapper;
import com.bqreaders.silkroad.common.api.error.NotFoundExceptionMapper;
import com.bqreaders.silkroad.common.gson.GsonMessageReaderWriterProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @author Alexander De Leon
 * 
 */
public abstract class ServiceRunner<T> {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceRunner.class);

	private final Application<Configuration> application = new Application<Configuration>() {

		@Override
		public void initialize(Bootstrap<Configuration> bootstrap) {
			configureObjectMapper(bootstrap.getObjectMapper());
			bootstrap(bootstrap);
		}

		@Override
		public void run(Configuration configuration, Environment environment) {
			configureDefaultProviders(environment);
			ApplicationContext applicationContext = loadSpringContext();
			configureDropWizzard(configuration, applicationContext);
			configureFiltersAndInterceptors(environment, applicationContext);
			configureService(environment, applicationContext);
		}
	};

	public final void run(String[] arguments) throws Exception {
		LOG.info("Initializing ${conf.namespace} as {}", getName());
		System.setProperty("conf.namespace", getName());
		application.run(arguments);
	}

	protected abstract String getName();

	protected abstract void configureService(Environment environment, ApplicationContext context);

	/**
	 * Override by subclasses to add bootstrap logic
	 */
	protected void bootstrap(Bootstrap<Configuration> bootstrap) {
		// empty
	}

	/**
	 * Override by subclasses to configure {@link com.fasterxml.jackson.databind.ObjectMapper}
	 */
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
	}

	private void configureDropWizzard(Configuration configuration, ApplicationContext applicationContext) {
		configuration.setServerFactory(applicationContext.getBean(ServerFactory.class));
		configuration.setLoggingFactory(applicationContext.getBean(LoggingFactory.class));
	}

	private void configureFiltersAndInterceptors(Environment environment, ApplicationContext applicationContext) {
		// Replace exception mappers with custom implementations
		replaceExceptionMapper(environment, ConstraintViolationExceptionMapper.class, new JsonValidationExceptionMapper());
		replaceExceptionMapper(environment, JsonProcessingExceptionMapper.class, new JsonValidationExceptionMapper().new JacksonAdapter());
		environment.jersey().register(NotFoundExceptionMapper.class);
		environment.jersey().register(GenericExceptionMapper.class);

		GZIPContentEncodingFilter gzipFilter = new GZIPContentEncodingFilter();
		TransformNullBodiesToEmptyObjectsFilter transformNullBodiesToEmptyObjectsFilter = new TransformNullBodiesToEmptyObjectsFilter();

		// Configure filters
		List<ContainerRequestFilter> requestFilters = new ArrayList<>(applicationContext
				.getBeansOfType(ContainerRequestFilter.class).values());
		requestFilters.add(gzipFilter);
		requestFilters.add(transformNullBodiesToEmptyObjectsFilter);
		environment.jersey().property("com.sun.jersey.spi.container.ContainerRequestFilters", requestFilters);

		List<ContainerResponseFilter> responseFilters = new ArrayList<>(applicationContext
				.getBeansOfType(ContainerResponseFilter.class).values());
		responseFilters.add(gzipFilter);
		environment.jersey().property("com.sun.jersey.spi.container.ContainerResponseFilters", responseFilters);

		Boolean etagEnabled = applicationContext.getEnvironment().getProperty("etag.enabled", Boolean.class);
		if (etagEnabled == null || etagEnabled.equals(true)) {
			environment.getApplicationContext().addFilter(ShallowEtagHeaderFilter.class, "*", EnumSet.of(DispatcherType.REQUEST));
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

	private final Class<T> getIocConfigurationClass() {
		return Generics.getTypeParameter(getClass(), Object.class);
	}
}
