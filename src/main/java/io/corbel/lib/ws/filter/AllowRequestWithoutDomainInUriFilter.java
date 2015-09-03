package io.corbel.lib.ws.filter;

import io.corbel.lib.token.exception.TokenVerificationException;
import io.corbel.lib.token.parser.TokenParser;
import io.corbel.lib.token.reader.TokenReader;
import io.corbel.lib.ws.auth.priority.CorbelPriorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import java.net.URI;
import java.util.regex.Pattern;

/**
 * @author Alberto J. Rubio
 */
@PreMatching
@Priority(CorbelPriorities.ALLOW_REQUEST_WITHOUT_DOMAIN_IN_URI_FILTER)
public class AllowRequestWithoutDomainInUriFilter extends OptionalContainerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AllowRequestWithoutDomainInUriFilter.class);
    private static final Pattern REQUEST_WITH_DOMAIN_PATTERN = Pattern.compile("v.*/[\\w-:.]+/\\w+/\\w+:.+");
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String UNAUTHENTICATED = "unauthenticated";
    private static final String TOKEN_PREFIX = "Bearer ";
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
            String path = request.getUriInfo().getPath();
            if (!path.matches(unAuthenticatedPathPattern) && !REQUEST_WITH_DOMAIN_PATTERN.matcher(path).matches()) {
                String domain = extractRequestDomain(request);
                if (domain != null) {
                    String version = path.substring(0, path.indexOf("/"));
                    String versionAndDomainPath = version + "/" + domain;
                    String pathWithoutVersion = path.replace(version, EMPTY_STRING);
                    String pathWithDomain = versionAndDomainPath + pathWithoutVersion;
                    URI requestUriWithDomain = request.getUriInfo().getRequestUriBuilder().replacePath(pathWithDomain).build();
                    request.setRequestUri(requestUriWithDomain);
                }
            }
        } catch (TokenVerificationException ignored) {
            LOG.debug("Cannot parse authorization token");
        }
    }

    private String extractRequestDomain(ContainerRequestContext request) throws TokenVerificationException {
        String domain = UNAUTHENTICATED;
        String authorizationHeader = request.getHeaderString(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            TokenReader tokenReader = tokenParser.parseAndVerify(authorizationHeader.substring(TOKEN_PREFIX.length()));
            domain = tokenReader.getInfo().getDomainId();
        }
        return domain;
    }
}