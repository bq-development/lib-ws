package io.corbel.lib.ws.filter;

import java.net.URI;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.corbel.lib.token.exception.TokenVerificationException;
import io.corbel.lib.token.parser.TokenParser;
import io.corbel.lib.token.reader.TokenReader;

/**
 * @author Alberto J. Rubio
 */
@PreMatching @Priority(1) public class AllowRequestWithoutDomainInUriFilter extends OptionalContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AllowRequestWithoutDomainInUriFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String API_VERSION = "/v1.0/";
    private static final String EMPTY_STRING = "";

    private final TokenParser tokenParser;
    private final String unAuthenticatedPathPattern;

    public AllowRequestWithoutDomainInUriFilter(boolean enabled, TokenParser tokenParser, String unAuthenticatedPathPattern) {
        super(enabled);
        this.tokenParser = tokenParser;
        this.unAuthenticatedPathPattern = unAuthenticatedPathPattern;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        try {
            UriInfo uriInfo = request.getUriInfo();
            String path = uriInfo.getPath();
            if (!path.matches(unAuthenticatedPathPattern)) {
                String value = request.getHeaderString(AUTHORIZATION_HEADER);
                if (value.startsWith(TOKEN_PREFIX)) {
                    TokenReader tokenReader = tokenParser.parseAndVerify(value.substring(TOKEN_PREFIX.length()));
                    String versionAndDomainPath = API_VERSION + tokenReader.getInfo().getDomainId() + "/";
                    if (!path.startsWith(versionAndDomainPath)) {
                        String pathWithoutVersion = path.replace(API_VERSION, EMPTY_STRING);
                        String pathWithDomain = versionAndDomainPath + pathWithoutVersion;
                        URI requestUriWithDomain = uriInfo.getRequestUriBuilder().replacePath(pathWithDomain).build();
                        request.setRequestUri(requestUriWithDomain);
                    }
                }
            }
        } catch (TokenVerificationException ignored) {
            LOG.debug("Cannot parse authorization token");
        }
    }
}