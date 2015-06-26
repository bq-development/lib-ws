package com.bq.oss.lib.ws.queries;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Parameter.Source;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import com.bq.oss.lib.queries.builder.QueryParametersBuilder;
import com.bq.oss.lib.queries.exception.InvalidParameterException;
import com.bq.oss.lib.queries.jaxrs.QueryParameters;
import com.bq.oss.lib.ws.SpringJerseyProvider;
import com.bq.oss.lib.ws.annotation.Rest;
import com.bq.oss.lib.ws.api.error.ErrorMessage;
import com.bq.oss.lib.ws.api.error.ErrorResponseFactory;
import com.bq.oss.lib.ws.model.Error;

/**
 * @author Rubén Carrasco
 */

public class QueryParametersProvider implements SpringJerseyProvider {

    public static final String API_PAGE_SIZE = "api:pageSize";
    public static final String API_PAGE = "api:page";
    public static final String API_SORT = "api:sort";
    public static final String API_QUERY = "api:query";
    public static final String API_CONDITION = "api:condition";
    public static final String API_SEARCH = "api:search";
    public static final String API_AGGREGATION = "api:aggregation";

    private static int defaultPageSize;
    private static int maxPageSize;
    private static QueryParametersBuilder queryParametersBuilder;

    public QueryParametersProvider(int defaultPageSize, int maxPageSize, QueryParametersBuilder queryParametersBuilder) {
        QueryParametersProvider.defaultPageSize = defaultPageSize;
        QueryParametersProvider.maxPageSize = maxPageSize;
        QueryParametersProvider.queryParametersBuilder = queryParametersBuilder;
    }

    @Override
    public org.glassfish.hk2.utilities.Binder getBinder() {
        return new Binder();
    }

    public static class QueryParametersInjectionResolver extends ParamInjectionResolver<Rest> {
        public QueryParametersInjectionResolver() {
            super(QueryParametersFactoryProvider.class);
        }
    }

    public static class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(QueryParametersFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(QueryParametersInjectionResolver.class).to(new TypeLiteral<InjectionResolver<Rest>>() {}).in(Singleton.class);
        }
    }

    public static class QueryParametersFactory extends AbstractContainerRequestValueFactory<QueryParameters> {

        @Override
        public QueryParameters provide() {
            MultivaluedMap<String, String> params = getContainerRequestBind().getUriInfo().getQueryParameters();

            try {
                return queryParametersBuilder.createQueryParameters(getIntegerParam(params, API_PAGE).orElse(0),
                        getIntegerParam(params, API_PAGE_SIZE).orElse(defaultPageSize), maxPageSize, getStringParam(params, API_SORT),
                        getListStringParam(params, API_QUERY), getListStringParam(params, API_CONDITION),
                        getStringParam(params, API_AGGREGATION), getStringParam(params, API_SEARCH));
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
                    return new WebApplicationException(badRequestResponse(new Error("invalid_page", ErrorMessage.INVALID_PAGE.getMessage(e
                            .getValue()))));
                case PAGE_SIZE:
                    return new WebApplicationException(badRequestResponse(new Error("invalid_page_size",
                            ErrorMessage.INVALID_PAGE_SIZE.getMessage(e.getValue(), maxPageSize))));
                case QUERY:
                    return new WebApplicationException(badRequestResponse(new Error("invalid_query", ErrorMessage.INVALID_QUERY.getMessage(
                            e.getValue(), e.getMessage()))));
                case SORT:
                    return new WebApplicationException(badRequestResponse(new Error("invalid_sort", ErrorMessage.INVALID_SORT.getMessage(
                            e.getValue(), e.getMessage()))));
                case SEARCH:
                    return new WebApplicationException(badRequestResponse(new Error("invalid_search",
                            ErrorMessage.INVALID_SEARCH.getMessage(e.getValue(), e.getMessage()))));
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
            return params.containsKey(key) ? Optional.of(Integer.valueOf(params.get(key).get(0))) : Optional.empty();
        }

        private Optional<String> getStringParam(MultivaluedMap<String, String> params, String key) {
            return params.containsKey(key) ? Optional.of(params.get(key).get(0)) : Optional.empty();
        }

        private Optional<List<String>> getListStringParam(MultivaluedMap<String, String> params, String key) {
            return params.containsKey(key) ? Optional.of(params.get(key)) : Optional.empty();

        }

        public ContainerRequest getContainerRequestBind() {
            return super.getContainerRequest();
        }
    }

    @Provider public static class QueryParametersFactoryProvider extends AbstractValueFactoryProvider {

        @Inject
        protected QueryParametersFactoryProvider(MultivaluedParameterExtractorProvider mpep, ServiceLocator locator) {
            super(mpep, locator, Source.UNKNOWN);
        }

        @Override
        protected Factory<?> createValueFactory(Parameter parameter) {
            if (parameter.getRawType().equals(QueryParameters.class) && parameter.getAnnotation(Rest.class) != null) {
                return new QueryParametersFactory();

            }
            return null;

        }

    }



}
