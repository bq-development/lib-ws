package com.bqreaders.silkroad.common.charset;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.MediaType;

/**
 * Created by Alberto J. Rubio
 */
public class CharsetResponseFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
        MediaType contentType = response.getMediaType();
        response.getHttpHeaders().putSingle("Content-Type", contentType.toString() + ";charset=UTF-8");
        return response;
    }
}
