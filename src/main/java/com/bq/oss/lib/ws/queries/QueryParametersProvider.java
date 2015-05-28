package com.bq.oss.lib.ws.queries;


import java.util.List;
import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.bq.oss.lib.queries.builder.QueryParametersBuilder;
import com.bq.oss.lib.queries.exception.InvalidParameterException;
import com.bq.oss.lib.queries.jaxrs.QueryParameters;
import com.bq.oss.lib.queries.parser.AggregationParser;
import com.bq.oss.lib.queries.parser.QueryParser;
import com.bq.oss.lib.queries.parser.SortParser;
import com.bq.oss.lib.ws.annotation.Rest;
import com.bq.oss.lib.ws.api.error.ErrorMessage;
import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.bq.oss.lib.ws.model.Error;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * @author Rubén Carrasco
 */
@Provider
public final class QueryParametersProvider implements InjectableProvider<Rest, Parameter> {

    public static final String API_PAGE_SIZE = "api:pageSize";
    public static final String API_PAGE = "api:page";
    public static final String API_SORT = "api:sort";
    public static final String API_QUERY = "api:query";
    public static final String API_CONDITION = "api:condition";
    public static final String API_SEARCH = "api:search";
    public static final String API_AGGREGATION = "api:aggregation";

    private final int defaultPageSize;
    private final int maxPageSize;
    private final QueryParametersBuilder queryParametersBuilder;

    public QueryParametersProvider(int defaultPageSize, int maxPageSize, QueryParametersBuilder queryParametersBuilder) {
        super();
        this.defaultPageSize = defaultPageSize;
        this.maxPageSize = maxPageSize;
        this.queryParametersBuilder = queryParametersBuilder;
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
                    return queryParametersBuilder.createQueryParameters(
                            getIntegerParam(params, API_PAGE).orElse(0),
                            getIntegerParam(params, API_PAGE_SIZE).orElse(defaultPageSize),
                            maxPageSize, getStringParam(params, API_SORT),
                            getListStringParam(params, API_QUERY), getListStringParam(params, API_CONDITION), getStringParam(params, API_AGGREGATION),
                            getStringParam(params, API_SEARCH)
                    );
                } catch (InvalidParameterException e) {
                    throw toRequestException(e);
                } catch (IllegalArgumentException e) {
                    throw new WebApplicationException(badRequestResponse(error(e)));
                }
            }

            private WebApplicationException toRequestException(InvalidParameterException e) {
                switch (e.getParameter()) {
                    case AGGREGATION:
                        return new WebApplicationException(badRequestResponse(new Error("invalid_aggregation",
                                ErrorMessage.INVALID_AGGREGATION.getMessage(e.getValue(), e.getMessage()))));
                    case PAGE:
                        return new WebApplicationException(badRequestResponse(new Error("invalid_page", ErrorMessage.INVALID_PAGE.getMessage(e.getValue()))));
                    case PAGE_SIZE:
                        return new WebApplicationException(badRequestResponse(new Error("invalid_page_size", ErrorMessage.INVALID_PAGE_SIZE.getMessage(
                                e.getValue(), maxPageSize))));
                    case QUERY:
                        return new WebApplicationException(badRequestResponse(new Error("invalid_query", ErrorMessage.INVALID_QUERY.getMessage(e.getValue(),
                                e.getMessage()))));
                    case SORT:
                        return new WebApplicationException(badRequestResponse(new Error("invalid_sort", ErrorMessage.INVALID_SORT.getMessage(e.getValue(),
                                e.getMessage()))));
                    default:
                        return new WebApplicationException(ErrorResponseFactory.getInstance().badRequest());
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

            private Optional<List<String>> getListStringParam(MultivaluedMap<String, String> params, String key) {
                return params.containsKey(key) ? Optional.of(params.get(key)) : Optional.empty();

            }
        };
    }
}
