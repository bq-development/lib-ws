package io.corbel.lib.ws.cors;

import com.google.common.base.Joiner;
import com.google.common.net.HttpHeaders;
import io.corbel.lib.ws.model.CustomHeaders;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This filter permits different configurations for the CORS behaviour.
 *
 * @see <a href="http://www.w3.org/TR/cors/">http://www.w3.org/TR/cors/</a>
 *
 * @author Alexander De Leon
 * 
 */
public class CorsResponseFilter implements ContainerResponseFilter {

    private static final String SPACE = " ";

    private final List<String> allowedOrigins;
    private final int preflightRequestMaxAge;

    public static CorsResponseFilter disabled() {
        return new CorsResponseFilter(Collections.<String>emptyList(), 0);
    }

    public static CorsResponseFilter anyOrigin(int preflightRequestMaxAge) {
        return new CorsResponseFilter(null, preflightRequestMaxAge);
    }

    public static CorsResponseFilter onlyAllowedOrigins(int preflightRequestMaxAge, String... allowedOrigins) {
        return new CorsResponseFilter(Arrays.asList(allowedOrigins), preflightRequestMaxAge);
    }

    public CorsResponseFilter(List<String> allowedOrigins, int preflightRequestMaxAge) {
        this.allowedOrigins = allowedOrigins;
        this.preflightRequestMaxAge = preflightRequestMaxAge;
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        String origin = allowAnyOrigin() ? request.getHeaderString(HttpHeaders.ORIGIN) : serializedOriginList();
        if (origin != null) {
            response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
            response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            if (request.getMethod().equals(HttpMethod.OPTIONS)) {
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, preflightRequestMaxAge);
                response.getHeaders().add(
                        HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                        Joiner.on(",").join(HttpHeaders.AUTHORIZATION, HttpHeaders.ACCEPT, HttpHeaders.CONTENT_TYPE,
                                CustomHeaders.NO_REDIRECT_HEADER, CustomHeaders.REQUEST_COOKIE_HEADER, CustomHeaders.X_HTTP_METHOD_OVERRIDE,
                                CustomHeaders.X_CHALLENGE));
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        Joiner.on(",").join(HttpHeaders.LOCATION, HttpHeaders.DATE));
                if (response.getHeaders().containsKey(HttpHeaders.ALLOW)) {
                    response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, response.getHeaders().getFirst(HttpHeaders.ALLOW));
                }
            } else if (response.getHeaders().containsKey(HttpHeaders.LOCATION)) {
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION);
            }

        }
    }

    private boolean allowAnyOrigin() {
        return allowedOrigins == null;
    }

    private String serializedOriginList() {
        return allowedOrigins.isEmpty() ? null : Joiner.on(SPACE).join(allowedOrigins);
    }
}
