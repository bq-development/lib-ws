package com.bq.oss.lib.ws.filter;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Created by Francisco Sanchez on 4/02/15.
 */
public class ProxyLocationResponseRewriteFilter extends OptionalContainerResponseFilter {

	private static final Logger LOG = LoggerFactory.getLogger(ProxyLocationResponseRewriteFilter.class);

	public ProxyLocationResponseRewriteFilter(boolean enabled) {
		super(enabled);
	}

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		if (!(FilterUtil.redirect(response.getStatus()) || FilterUtil.hasNoRedirectHeader(request))) {
			try {
				Optional<URI> locationOptional = Optional.ofNullable((URI) response.getHttpHeaders().getFirst(
						"Location"));
				locationOptional.ifPresent(responseLocation -> {
					Optional.ofNullable((String) request.getHeaderValue("X-Forwarded-Uri")).ifPresent(
							originalUri -> setLocationWithProxyPassPath(request, response, responseLocation,
									originalUri));
				});
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
		return response;
	}

	private void setLocationWithProxyPassPath(ContainerRequest request, ContainerResponse response,
			URI responseLocation, String originalUri) {
		if (responseLocation.getHost().equals(request.getAbsolutePath().getHost())) {
			String proxyPassPath = originalUri.substring(0, originalUri.indexOf(request.getAbsolutePath().getPath()));
			try {
				response.getHttpHeaders().putSingle(
						"Location",
						new URI(responseLocation.getScheme(), responseLocation.getHost(), proxyPassPath
								+ responseLocation.getPath(), responseLocation.getFragment()));
			} catch (URISyntaxException e) {
				LOG.error(e.getMessage());
			}
		}
	}

}
