package io.corbel.lib.ws.filter;

import org.eclipse.jetty.server.Response;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Alexander De Leon <alex.deleon@devialab.com>
 */
public class ChunkedAwaredShallowEtagHeaderFilter extends ShallowEtagHeaderFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isChucked(response)) {
            filterChain.doFilter(request, response);
        }
        else {
            super.doFilterInternal(request, response, filterChain);
        }
    }

    @Override
    protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response, int responseStatusCode, byte[] responseBody) {
        return !isChucked(response) && super.isEligibleForEtag(request, response, responseStatusCode, responseBody);
    }

    protected boolean isChucked(HttpServletResponse response) {
        return response instanceof  Response && ((Response) response).getContentLength() == -1;
    }
}
