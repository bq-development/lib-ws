package com.bq.oss.lib.ws.queries;


import java.lang.Exception;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.bq.oss.lib.queries.parser.AggregationParser;
import com.bq.oss.lib.queries.parser.QueryParser;
import com.bq.oss.lib.ws.annotation.Rest;
import com.bq.oss.lib.ws.api.error.ApiRequestException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.bq.oss.lib.ws.model.Error;

/**
 * @author Rubén Carrasco
 * 
 */
@Provider
public final class QueryParametersProvider implements InjectableProvider<Rest, Parameter> {

	public static final String API_PAGE_SIZE = "api:pageSize";
	public static final String API_PAGE = "api:page";
	public static final String API_SORT = "api:sort";
	public static final String API_QUERY = "api:query";
	public static final String API_SEARCH = "api:search";
	public static final String API_AGGREGATION = "api:aggregation";

	private final int defaultPageSize;
	private final int maxPageSize;
	private final QueryParser queryParser;
	private final AggregationParser aggregationParser;

	public QueryParametersProvider(int defaultPageSize, int maxPageSize, QueryParser queryParser,
			AggregationParser aggregationParser) {
		super();
		this.defaultPageSize = defaultPageSize;
		this.maxPageSize = maxPageSize;
		this.queryParser = queryParser;
		this.aggregationParser = aggregationParser;

	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	@Override
	public Injectable<QueryParameters> getInjectable(ComponentContext ic, final Rest a, final Parameter c) {

		if (QueryParameters.class != c.getParameterClass()) {
			return null;
		}

		return new AbstractHttpContextInjectable<QueryParameters>() {

			@Override
			public QueryParameters getValue(HttpContext context) {
				MultivaluedMap<String, String> params = context.getUriInfo().getQueryParameters();

				try {
					return new QueryParameters(getIntegerParam(params, API_PAGE_SIZE).orElse(defaultPageSize),
							getIntegerParam(params, API_PAGE).orElse(0), maxPageSize, getStringParam(params, API_SORT),
							getStringParam(params, API_QUERY), queryParser, getStringParam(params, API_AGGREGATION),
							aggregationParser, getStringParam(params, API_SEARCH));
				} catch (NumberFormatException e) {
					throw new WebApplicationException(badRequestResponse(error(e)));
				} catch (ApiRequestException e) {
					throw new WebApplicationException(
							badRequestResponse(e.getError() != null ? e.getError() : error(e)));
				}
			}

			private Error error(Exception e) {
				return new Error("bad_request", e.getMessage());
			}

			private Response badRequestResponse(Error error) {
				return Response.status(Status.BAD_REQUEST).entity(error).build();
			}

			private Optional<java.lang.Integer> getIntegerParam(MultivaluedMap<String, String> params, String key) {
				return params.containsKey(key) ? Optional.of(Integer.valueOf(params.get(key).get(0))) : Optional
						.empty();
			}

			private Optional<String> getStringParam(MultivaluedMap<String, String> params, String key) {
				return params.containsKey(key) ? Optional.of(params.get(key).get(0)) : Optional.empty();
			}
		};
	}
}
