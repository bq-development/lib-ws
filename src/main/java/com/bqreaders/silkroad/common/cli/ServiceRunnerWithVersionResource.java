/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.cli;

import com.bqreaders.silkroad.common.api.ArtifactIdVersionResource;
import com.google.common.util.concurrent.AbstractService;
import io.dropwizard.setup.Environment;
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
		environment.jersey().register(new ArtifactIdVersionResource(getArtifactId()));
	}

	protected abstract String getArtifactId();

}
