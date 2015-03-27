package com.bqreaders.silkroad.common.filter;

import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Created by Francisco Sanchez on 13/01/15.
 */
public abstract class OptionalContainerResponseFilter implements ContainerResponseFilter {
	private boolean enabled;

	public OptionalContainerResponseFilter(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
