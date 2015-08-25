package io.corbel.lib.ws.encoding;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

import io.corbel.lib.ws.auth.priority.CorbelPriorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A workaround to encode slash (/) in matrix params
 * 
 * @author Alexander De Leon
 * 
 */
@PreMatching
@Priority(CorbelPriorities.MATRIX_ENCODING_REQUEST_FILTER)
public class MatrixEncodingRequestFilter implements ContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(MatrixEncodingRequestFilter.class);

    private final Pattern matrixPattern;

    /**
     * Creates a new filter for the specified pattern
     * 
     * @param matrixPattern A pattern according to the {@link java.util.regex.Pattern} class. This pattern must contain two groups. The
     *        first group the non placing part of the path and the second the replacing part. For example:
     *        ^(/v1.0/.+/resource/.+/.+/.+;r=)(.+)$
     */
    public MatrixEncodingRequestFilter(String matrixPattern) {
        this.matrixPattern = Pattern.compile(matrixPattern);
        LOG.info("Creating matrix encoding filter for path pattern {}", matrixPattern);
    }

    @Override
    public void filter(ContainerRequestContext request) {
        URI path = request.getUriInfo().getAbsolutePath();
        Matcher matcher = matrixPattern.matcher(path.toASCIIString());
        if (matcher.matches()) {
            String encodedMatrix = matcher.group(2).replaceAll("/", "%2F");
            String encodedRequestUri = matcher.replaceFirst("$1" + encodedMatrix);
            request.setRequestUri(request.getUriInfo().getBaseUri(), URI.create(encodedRequestUri));
        }
    }

}
