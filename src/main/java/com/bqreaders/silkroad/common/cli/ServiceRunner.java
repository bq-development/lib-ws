/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.ExceptionMapper;

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
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.HttpConfiguration;
import com.yammer.dropwizard.config.LoggingConfiguration;
import com.yammer.dropwizard.config.LoggingFactory;
import com.yammer.dropwizard.jersey.InvalidEntityExceptionMapper;
import com.yammer.dropwizard.jersey.JsonProcessingExceptionMapper;
import com.yammer.dropwizard.json.ObjectMapperFactory;
import com.yammer.dropwizard.util.Generics;

/**
 * @author Alexander De Leon
 * 
 */
public abstract class ServiceRunner<T> {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceRunner.class);

	private final Service<Configuration> service = new Service<Configuration>() {

		@Override
		public void initialize(Bootstrap<Configuration> bootstrap) {
			String name = getName();
			bootstrap.setName(name);
			LOG.info("Initializing ${conf.namespace} as {}", name);
			System.setProperty("conf.namespace", name);
			configureObjectMapperFactory(bootstrap.getObjectMapperFactory());
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
		service.run(arguments);
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
	 * Override by subclasses to configure {@link com.yammer.dropwizard.json.ObjectMapperFactory}
	 */
	protected void configureObjectMapperFactory(ObjectMapperFactory objectMapperFactory) {
		objectMapperFactory.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	protected ApplicationContext loadSpringContext() {
		Class<T> ioc = getIocConfigurationClass();
		LOG.info("Loading spring context from class {}", ioc);
		return new AnnotationConfigApplicationContext(ioc);
	}

	private void configureDefaultProviders(Environment environment) {
		environment.addProvider(new GsonMessageReaderWriterProvider());
	}

	private void configureDropWizzard(Configuration configuration, ApplicationContext applicationContext) {
		configuration.setHttpConfiguration(applicationContext.getBean(HttpConfiguration.class));
		configuration.setLoggingConfiguration(applicationContext.getBean(LoggingConfiguration.class));
		new LoggingFactory(configuration.getLoggingConfiguration(), getName()).configure();
	}

	private void configureFiltersAndInterceptors(Environment environment, ApplicationContext applicationContext) {
		// Replace exception mappers with custom implementations
		replaceExceptionMapper(environment, InvalidEntityExceptionMapper.class, new JsonValidationExceptionMapper());
		replaceExceptionMapper(environment, JsonProcessingExceptionMapper.class,
				new JsonValidationExceptionMapper().new JacksonAdapter());
		environment.addProvider(NotFoundExceptionMapper.class);
		environment.addProvider(GenericExceptionMapper.class);

		GZIPContentEncodingFilter gzipFilter = new GZIPContentEncodingFilter();

		// Configure filters
		List<ContainerRequestFilter> requestFilters = new ArrayList<ContainerRequestFilter>(applicationContext
				.getBeansOfType(ContainerRequestFilter.class).values());
		requestFilters.add(gzipFilter);
		environment.setJerseyProperty("com.sun.jersey.spi.container.ContainerRequestFilters", requestFilters);

		List<ContainerResponseFilter> responseFilters = new ArrayList<ContainerResponseFilter>(applicationContext
				.getBeansOfType(ContainerResponseFilter.class).values());
		responseFilters.add(gzipFilter);
		environment.setJerseyProperty("com.sun.jersey.spi.container.ContainerResponseFilters", responseFilters);

		Boolean etagEnabled = applicationContext.getEnvironment().getProperty("etag.enabled", Boolean.class);
		if (etagEnabled == null || etagEnabled.equals(true)) {
			environment.addFilter(ShallowEtagHeaderFilter.class, "*");
		}

		// Configure injectable providers
		@SuppressWarnings("rawtypes")
		Map<String, InjectableProvider> providers = applicationContext.getBeansOfType(InjectableProvider.class);
		if (providers != null) {
			for (@SuppressWarnings("rawtypes")
			Map.Entry<String, InjectableProvider> entry : providers.entrySet()) {
				LOG.info("Registering provider: {}", entry.getKey());
				environment.addProvider(entry.getValue());
			}
		}
	}

	private void replaceExceptionMapper(Environment environment,
			Class<? extends ExceptionMapper<? extends Throwable>> exceptionMapperToBeReplaced,
			ExceptionMapper<? extends Throwable> customExceptionMapper) {
		Object exceptionMapper = null;
		for (Object singleton : environment.getJerseyResourceConfig().getSingletons()) {
			if (exceptionMapperToBeReplaced.isInstance(singleton)) {
				exceptionMapper = singleton;
				break;
			}
		}
		environment.getJerseyResourceConfig().getSingletons().remove(exceptionMapper);
		environment.addProvider(customExceptionMapper);
	}

	private final Class<T> getIocConfigurationClass() {
		return Generics.getTypeParameter(getClass(), Object.class);
	}
}
