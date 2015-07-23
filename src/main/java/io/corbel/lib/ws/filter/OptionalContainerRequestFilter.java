package io.corbel.lib.ws.filter;

import javax.ws.rs.container.ContainerRequestFilter;

/**
 * @author Francisco Sanchez
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
