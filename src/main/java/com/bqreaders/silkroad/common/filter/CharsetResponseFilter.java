package com.bqreaders.silkroad.common.filter;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Created by Alberto J. Rubio
 */
public class CharsetResponseFilter implements ContainerResponseFilter {

	private static final String CHARSET_UTF_8 = "; charset=UTF-8";

    private final boolean enabled;

    public CharsetResponseFilter(boolean enabled) {
        this.enabled = enabled;
    }

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		if(enabled) {
            MediaType mediaType = response.getMediaType();
            if (mediaType != null) {
                String contentType = mediaType.toString();
                if (!contentType.contains("charset")) {
                    contentType = contentType + CHARSET_UTF_8;
                }
                response.getHttpHeaders().putSingle("Content-Type", contentType);
            }
        }
		return response;
	}
}
