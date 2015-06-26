package com.bq.oss.lib.ws.filter;

import javax.ws.rs.container.ContainerResponseFilter;


/**
 * Created by Francisco Sanchez on 13/01/15.
 */
public abstract class OptionalContainerResponseFilter implements ContainerResponseFilter {
    private final boolean enabled;

    public OptionalContainerResponseFilter(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

}
