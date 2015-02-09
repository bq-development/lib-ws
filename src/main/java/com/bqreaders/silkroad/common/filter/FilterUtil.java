package com.bqreaders.silkroad.common.filter;

import com.bqreaders.silkroad.common.model.CustomHeaders;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * @author Francisco Sanchez
 */
public class FilterUtil {
    public static boolean hasNoRedirectHeader(ContainerRequest request) {
        return Boolean.parseBoolean(request.getHeaderValue(CustomHeaders.NO_REDIRECT_HEADER.toString()));
    }

    public static boolean redirect(int status) {
		return status >= 300 && status < 400;
	}
}
