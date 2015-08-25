package io.corbel.lib.ws.filter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francisco Sanchez
 */
public class ProxyLocationResponseRewriteFilter extends OptionalContainerResponseFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyLocationResponseRewriteFilter.class);

    public ProxyLocationResponseRewriteFilter(boolean enabled) {
        super(enabled);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        if (!(FilterUtil.redirect(response.getStatus()) || FilterUtil.hasNoRedirectHeader(request))) {
            try {
                Optional<URI> locationOptional = Optional.ofNullable((URI) response.getHeaders().getFirst("Location"));
                locationOptional.ifPresent(responseLocation -> {
                    Optional.ofNullable(request.getHeaderString("X-Forwarded-Uri")).ifPresent(
                            originalUri -> setLocationWithProxyPassPath(request, response, responseLocation, originalUri));
                });
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void setLocationWithProxyPassPath(ContainerRequestContext request, ContainerResponseContext response, URI responseLocation,
            String originalUri) {
        if (responseLocation.getHost().equals(request.getUriInfo().getAbsolutePath().getHost())) {
            String proxyPassPath = originalUri.split("/v.*/")[0];
            try {
                response.getHeaders().putSingle(
                        "Location",
                        new URI(responseLocation.getScheme(), responseLocation.getHost(), proxyPassPath + responseLocation.getPath(),
                                responseLocation.getFragment()));
            } catch (URISyntaxException e) {
                LOG.error(e.getMessage());
            }
        }
    }

}
