package com.bqreaders.silkroad.common.api.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rubén Carrasco
 * 
 */
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable exception) {
		if (WebApplicationException.class.isAssignableFrom(exception.getClass())) {
			return ((WebApplicationException) exception).getResponse();
		}
		LOG.error("Unexpected exception", exception);
		return ErrorResponseFactory.getInstance().serverError(exception);
	}

}
