package io.corbel.lib.ws.filter;

import javax.ws.rs.container.ContainerResponseFilter;


/**
 * @author Francisco Sanchez
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
