package com.bq.oss.lib.ws.filter;

import javax.ws.rs.container.ContainerRequestFilter;


/**
 * Created by Francisco Sanchez on 13/01/15.
 */
public abstract class OptionalContainerRequestFilter implements ContainerRequestFilter {
    private final boolean enabled;

    public OptionalContainerRequestFilter(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
