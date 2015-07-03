/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.cli;

import com.bq.oss.lib.ws.api.ArtifactIdVersionResource;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.ApplicationContext;

/**
 * @author Alexander De Leon
 * 
 */
public abstract class ServiceRunnerWithVersionResource<T> extends ServiceRunner<T> {

	@Override
	protected final String getName() {
		return getArtifactId();
	}

	/**
	 * Subclasses have to call super.configureService(...) to register version resource
	 */
	@Override
	protected void configureService(Environment environment, ApplicationContext context) {
        environment.jersey().property(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, false);
        environment.jersey().register(new ArtifactIdVersionResource(getArtifactId()));
	}

	protected abstract String getArtifactId();

}
