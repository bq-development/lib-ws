package com.bqreaders.silkroad.common.api.error;

import com.bqreaders.silkroad.common.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ErrorResponseFactory {

	private static final Logger LOG = LoggerFactory.getLogger(ErrorResponseFactory.class);
	private static final int METHOD_NOT_ALLOWED_STATUS = 405;
	private static final int INVALID_ENTITY = 422;
	private static final int RESOURCE_LOCKED = 423;
	private static final int BAD_GATEWAY = 502;

	private static final Error DEFAULT_FORBIDDEN_ERROR = new Error("forbidden", ErrorMessage.FORBIDDEN.getMessage());
	private static final Error DEFAULT_UNAUTHORIZED_ERROR = new Error("unauthorized",
			ErrorMessage.UNAUTHORIZE.getMessage());
	private static final Error DEFAULT_METHOD_NOT_ALLOWED_ERROR = new Error("method_not_allowed",
			ErrorMessage.NOT_ALLOWED.getMessage());
	private static final Error DEFAULT_CONFLICT_ERROR = new Error("conflict", ErrorMessage.CONFLICT.getMessage());
	private static final Error DEFAULT_NOT_FOUND_ERROR = new Error("not_found", ErrorMessage.NOT_FOUND.getMessage());
	private static final Error DEFAULT_BAD_REQUEST_ERROR = new Error("bad_request",
			ErrorMessage.BAD_REQUEST.getMessage());
	private static final Error DEFAULT_BAD_GATEWAY = new Error("bad_gateway", ErrorMessage.BAD_GATEWAY.getMessage());
	private static ErrorResponseFactory instance;

	public static ErrorResponseFactory getInstance() {
		if (instance == null) {
			LOG.debug("Creating instance of {}", ErrorResponseFactory.class);
			instance = new ErrorResponseFactory();
		}
		return instance;
	}

	protected ErrorResponseFactory() {
	}

	public Response badRequest(ApiRequestException e) {
		return badRequest(e.getError());
	}

	public Response serverError(Throwable exception) {
		return serverError(new Error("internal_server_error", exception.getMessage()));
	}

	public Response badGateway() {
		return badGateway(DEFAULT_BAD_GATEWAY);
	}

	public Response badRequest() {
		return badRequest(DEFAULT_BAD_REQUEST_ERROR);
	}

	public Response notFound() {
		return notfound(DEFAULT_NOT_FOUND_ERROR);
	}

	public Response conflict() {
		return conflict(DEFAULT_CONFLICT_ERROR);
	}

	public Response invalidEntity(String description) {
		return invalidEntity(new Error("invalid_entity", ErrorMessage.INVALID_ENTITY.getMessage(description)));
	}

	public Response methodNotAllowed() {
		return methodNotAllowed(DEFAULT_METHOD_NOT_ALLOWED_ERROR);
	}

	public Response unauthorized() {
		return unauthorized(DEFAULT_UNAUTHORIZED_ERROR);
	}

	public Response unauthorized(String message) {
		return unauthorized(new Error("unauthorized", message));
	}

	public Response preconditionFailed(String description) {
		return jsonResponse(new Error("precondition_failed", ErrorMessage.PRECONDITION_FAILED.getMessage(description)),
				Status.PRECONDITION_FAILED);
	}

	public Response forbidden() {
		return forbidden(DEFAULT_FORBIDDEN_ERROR);
	}

	public Response badRequest(Error error) {
		return jsonResponse(error, Status.BAD_REQUEST);
	}

	public Response notfound(Error error) {
		return jsonResponse(error, Status.NOT_FOUND);
	}

	public Response conflict(Error error) {
		return jsonResponse(error, Status.CONFLICT);
	}

	public Response invalidEntity(Error error) {
		return jsonResponse(error, INVALID_ENTITY);
	}

	public Response methodNotAllowed(Error error) {
		return jsonResponse(error, METHOD_NOT_ALLOWED_STATUS);
	}

	public Response missingParameter(String parameter) {
		return badRequest(new Error("missing_parameter", parameter));
	}

	public Response unauthorized(Error error) {
		return jsonResponse(error, Status.UNAUTHORIZED);
	}

	public Response forbidden(Error error) {
		return jsonResponse(error, Status.FORBIDDEN);
	}

	public Response resourceLocked(Error error) {
		return jsonResponse(error, RESOURCE_LOCKED);
	}

	public Response serverError(Error error) {
		return jsonResponse(error, Status.INTERNAL_SERVER_ERROR);
	}

	public Response badGateway(Error error) {
		return jsonResponse(error, BAD_GATEWAY);
	}

	private Response jsonResponse(Error error, Status status) {
		return jsonResponse(error, status.getStatusCode());
	}

	private Response jsonResponse(Error error, int status) {
		return Response.status(status).entity(error).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
