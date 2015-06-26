package com.bq.oss.lib.ws.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MediaType;



/**
 * Created by Alberto J. Rubio
 */
public class CharsetResponseFilter extends OptionalContainerResponseFilter {
    private static final String CHARSET_UTF_8 = "; charset=UTF-8";

    public CharsetResponseFilter(boolean enabled) {
        super(enabled);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        MediaType mediaType = response.getMediaType();
        if (mediaType != null) {
            String contentType = mediaType.toString();
            if (!contentType.contains("charset")) {
                contentType = contentType + CHARSET_UTF_8;
            }
            response.getHeaders().putSingle("Content-Type", contentType);
        }
    }
}
