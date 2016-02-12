package io.corbel.lib.ws.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

public class CustomShallowEtagHeaderFilter extends ShallowEtagHeaderFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletResponse responseToUse = response;
        if (!isAsyncDispatch(request) && !(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }

        filterChain.doFilter(request, responseToUse);

        if (!isAsyncStarted(request)) {
            updateResponse(request, responseToUse);
        }
    }

    private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rawEtag = response.getHeader(HttpHeaders.ETAG);
        int statusCode = response.getStatus();
        String responseETag = rawEtag;
        byte[] body = null;
        if (rawEtag == null) {
            ContentCachingResponseWrapper responseWrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
            Assert.notNull(responseWrapper, "ShallowEtagResponseWrapper not found");
            body = responseWrapper.getContentAsByteArray();
            if (response.isCommitted()) {
                if (body.length > 0) {
                    StreamUtils.copy(body, response.getOutputStream());
                }
            } else {
                responseETag = generateETagHeaderValue(body);
            }

        }

        if (isEligibleForEtag(request, response, statusCode, body)) {
            response.setHeader(HttpHeaders.ETAG, responseETag);
            String requestETag = request.getHeader(HttpHeaders.IF_NONE_MATCH);
            if (responseETag.equals(requestETag)) {
                if (logger.isTraceEnabled()) {
                    logger.trace("ETag [" + responseETag + "] equal to If-None-Match, sending 304");
                }
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("ETag [" + responseETag + "] not equal to If-None-Match [" + requestETag + "], sending normal response");
                }
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Response with status code [" + statusCode + "] not eligible for ETag");
            }
        }


        if (rawEtag == null) {
            if (body.length > 0) {
                response.setContentLength(body.length);
                StreamUtils.copy(body, response.getOutputStream());
            }
        }
    }

}
