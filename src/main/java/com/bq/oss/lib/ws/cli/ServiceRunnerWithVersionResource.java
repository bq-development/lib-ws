/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.cli;

import org.springframework.context.ApplicationContext;

import com.bq.oss.lib.ws.api.ArtifactIdVersionResource;
import io.dropwizard.setup.Environment;

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
        super.configureService(environment, context);
        environment.jersey().register(new ArtifactIdVersionResource(getArtifactId()));
    }

    protected abstract String getArtifactId();

}
