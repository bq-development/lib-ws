package com.bq.oss.lib.ws.filter;

import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Created by Francisco Sanchez on 13/01/15.
 */
public abstract class OptionalContainerRequestFilter implements ContainerRequestFilter {
	private boolean enabled;

	public OptionalContainerRequestFilter(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
