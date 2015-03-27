/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.cors.ioc;

import com.bq.oss.lib.ws.cors.CorsResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * By adding this configuration to your applicaation IcO, you adding the {@link CorsResponseFilter}
 * 
 * @author Alexander De Leon
 * 
 */
@Configuration
public class CorsIoc {

	private static final Logger LOG = LoggerFactory.getLogger(CorsIoc.class);

	@Autowired
	private Environment env;

	@Bean
	public ContainerResponseFilter getCorsResponseFilter() {
		boolean enabled = env.getProperty("cors.enabled", Boolean.class, false);
		String allowedOrigins = env.getProperty("cors.allowedOrigins", "");
		int preflightRequestMaxAge = env.getProperty("cors.preflightRequestMaxAge", Integer.class, /* one day */
				3600 * 24);
		if (enabled) {
			if (!allowedOrigins.isEmpty()) {
				if (allowedOrigins.equalsIgnoreCase("any")) {
					LOG.warn("CORS enabled for ANY origin");
					return CorsResponseFilter.anyOrigin(preflightRequestMaxAge);

				} else {
					LOG.info("CORS enabled for the following origins {}", allowedOrigins);
					return CorsResponseFilter.onlyAllowedOrigins(preflightRequestMaxAge, allowedOrigins.split(","));
				}
			} else {
				LOG.warn("CORS is enabled but the property cors.allowedOrigins is not defined. Disabling CORS!");
			}
		}
		return CorsResponseFilter.disabled();
	}
}
