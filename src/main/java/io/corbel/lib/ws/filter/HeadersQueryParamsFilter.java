package io.corbel.lib.ws.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;

import io.corbel.lib.ws.auth.priority.CorbelPriorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.corbel.lib.ws.api.error.ErrorResponseFactory;

/**
 * @author Francisco Sanchez
 */
@PreMatching
@Priority(CorbelPriorities.HEADERS_QUERY_PARAMS_FILTER)
public class HeadersQueryParamsFilter extends OptionalContainerRequestFilter {

    private static final String HEADER_KEY = "headers";
    private final ObjectMapper objectMapper;
    private final TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};

    private static final Logger LOG = LoggerFactory.getLogger(HeadersQueryParamsFilter.class);

    public HeadersQueryParamsFilter(boolean enabled, ObjectMapper objectMapper) {
        super(enabled);
        this.objectMapper = objectMapper;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        String headerQueryParameters = request.getUriInfo().getQueryParameters().getFirst(HEADER_KEY);
        if (null != headerQueryParameters) {
            try {
                HashMap<String, String> queryParamHeaders = objectMapper.readValue(headerQueryParameters, typeRef);
                for (String key : queryParamHeaders.keySet()) {
                    try {
                        request.getHeaders().putSingle(key, URLDecoder.decode(queryParamHeaders.get(key), "UTF-8"));
                    } catch (UnsupportedEncodingException ignored) {
                        LOG.error("Cannot decode query param value: " + queryParamHeaders.get(key));
                    }
                }
            } catch (IOException e) {
                throw new WebApplicationException(ErrorResponseFactory.getInstance()
                        .badRequest(new io.corbel.lib.ws.model.Error("bad_request", "invalid query parameter format: " + HEADER_KEY)));
            }
        }
    }
}
