package com.bq.oss.lib.ws.queries;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import com.bq.oss.lib.queries.builder.ResourceQueryBuilder;
import com.bq.oss.lib.queries.jaxrs.QueryParameters;
import com.bq.oss.lib.queries.parser.CustomJsonParser;
import com.bq.oss.lib.queries.parser.JacksonAggregationParser;
import com.bq.oss.lib.queries.parser.JacksonQueryParser;
import com.bq.oss.lib.queries.parser.JacksonSortParser;
import com.bq.oss.lib.queries.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

/**
 * @author Rubén Carrasco
 */
public class QueryParametersProviderTest {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_SIZE = 10;

    HttpContext context;
    AbstractHttpContextInjectable<QueryParameters> injectable;
    MultivaluedMap<String, String> params;

    @Before
    public void setUp() throws Exception {
        CustomJsonParser parser = new CustomJsonParser(new ObjectMapper().getFactory());
        QueryParametersProvider provider = new QueryParametersProvider(DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE,
                new JacksonQueryParser(parser),
                new JacksonAggregationParser(parser),
                new JacksonSortParser(parser)
        );
        Parameter parameter = new Parameter(null, null, null, null, null, QueryParameters.class);
        injectable = (AbstractHttpContextInjectable<QueryParameters>) provider.getInjectable(null, null, parameter);
        context = mock(HttpContext.class);
    }

    @Test
    public void test() {
        params = new MultivaluedMapImpl();
        params.add(QueryParametersProvider.API_PAGE, "4");
        params.add(QueryParametersProvider.API_PAGE_SIZE, "20");
        params.add(QueryParametersProvider.API_SORT, "{\"price\":\"asc\"}");
        params.add(QueryParametersProvider.API_QUERY, "[{\"$eq\":{\"categories\":\"Metallica\"}}]");
        params.add(QueryParametersProvider.API_AGGREGATION, "{\"$count\":\"xxxx\"}");
        params.add(QueryParametersProvider.API_SEARCH, "Title+Test+T1");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);

        QueryParameters parameters = injectable.getValue(context);
        assertThat(parameters.getPagination().getPage()).isEqualTo(4);
        assertThat(parameters.getPagination().getPageSize()).isEqualTo(20);
        Optional<Aggregation> countOperator = Optional.of(new Count("xxxx"));
        assertThat(parameters.getAggregation()).isEqualTo(countOperator);
        Optional<Sort> sort = Optional.of(new Sort("ASC", "price"));
        assertThat(parameters.getSort()).isEqualTo(sort);
        Optional<ResourceQuery> resourceQuery = Optional.of(new ResourceQueryBuilder().add("categories", "Metallica")
                .build());
        assertThat(parameters.getQuery()).isEqualTo(resourceQuery);
        assertThat(parameters.getSearch()).isEqualTo(Optional.of(new ResourceSearch("Title+Test+T1")));
    }

    @Test
    public void testDefaults() {
        params = new MultivaluedMapImpl();
        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);

        QueryParameters parameters = injectable.getValue(context);
        assertThat(parameters.getPagination().getPage()).isEqualTo(0);
        assertThat(parameters.getPagination().getPageSize()).isEqualTo(DEFAULT_PAGE_SIZE);
        assertThat(parameters.getAggregation()).isEqualTo(Optional.empty());
        assertThat(parameters.getSort()).isEqualTo(Optional.empty());
        assertThat(parameters.getQuery()).isEqualTo(Optional.empty());
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters1() {
        params = new MultivaluedMapImpl();
        params.add(QueryParametersProvider.API_PAGE, "asdf");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);
        injectable.getValue(context);
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters2() {
        params = new MultivaluedMapImpl();
        params.add(QueryParametersProvider.API_PAGE_SIZE, "20000");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);
        injectable.getValue(context);
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters3() {
        params = new MultivaluedMapImpl();
        params.add(QueryParametersProvider.API_SORT, "{\"price\":\"qwer\"}");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);
        injectable.getValue(context);
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters4() {
        params = new MultivaluedMapImpl();
        params.add(QueryParametersProvider.API_QUERY, "[{\"$eq\":\"categories\":\"Metallica\"}}]");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);
        injectable.getValue(context);
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters5() {
        params = new MultivaluedMapImpl();
        params.add(QueryParametersProvider.API_AGGREGATION, "{\"$asdf\":\"xxxx\"}");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(context.getUriInfo()).thenReturn(uriInfoMock);
        injectable.getValue(context);
    }
}
