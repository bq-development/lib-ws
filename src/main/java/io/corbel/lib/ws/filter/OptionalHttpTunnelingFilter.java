package io.corbel.lib.ws.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;

import io.corbel.lib.ws.api.error.ErrorResponseFactory;
import io.corbel.lib.ws.auth.priority.CorbelPriorities;
import io.corbel.lib.ws.model.Error;

/**
 * Created by Francisco Sanchez on 8/09/15.
 */
@PreMatching @Priority(CorbelPriorities.HTTP_TUNNELING_FILTER) public class OptionalHttpTunnelingFilter
        extends
            OptionalContainerRequestFilter {

    public static final String HTTP_TUNNELING = "httpTunneling";
    public static final String BAD_PATH_BODY_ERROR = "bad_path_body";

    public OptionalHttpTunnelingFilter(boolean enabled) {
        super(enabled);
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        Optional<String> httpTunnelingVerb = Optional.ofNullable(request.getUriInfo().getQueryParameters().getFirst(HTTP_TUNNELING));
        if (httpTunnelingVerb.isPresent()) {
            request.setMethod(httpTunnelingVerb.get());
            try {
                String line = getQueryParametersFromTheBody(request);
                request.setEntityStream(null);
                addQueryParametersToTheUri(request, line);
            } catch (IOException | URISyntaxException e) {
                throw new WebApplicationException(
                        ErrorResponseFactory.getInstance().badRequest(new Error(BAD_PATH_BODY_ERROR, e.getMessage())));
            }
        }
    }

    private String getQueryParametersFromTheBody(ContainerRequestContext request) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getEntityStream()));
        return Optional.ofNullable(bufferedReader.readLine()).map(line -> line.startsWith("?") ? line : "?" + line).orElse("");
    }

    private void addQueryParametersToTheUri(ContainerRequestContext request, String queryParameters) throws URISyntaxException {
        URI originalUri = request.getUriInfo().getAbsolutePath();
        URI modifiedUri = new URI(originalUri.toString() + queryParameters);
        request.setRequestUri(modifiedUri);
    }

}
