package com.bq.oss.lib.ws.queries;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.model.Parameter;
import org.junit.Before;
import org.junit.Test;

import com.bq.oss.lib.queries.builder.QueryParametersBuilder;
import com.bq.oss.lib.queries.builder.ResourceQueryBuilder;
import com.bq.oss.lib.queries.jaxrs.QueryParameters;
import com.bq.oss.lib.queries.parser.CustomJsonParser;
import com.bq.oss.lib.queries.parser.CustomSearchParser;
import com.bq.oss.lib.queries.parser.DefaultPaginationParser;
import com.bq.oss.lib.queries.parser.JacksonAggregationParser;
import com.bq.oss.lib.queries.parser.JacksonQueryParser;
import com.bq.oss.lib.queries.parser.JacksonSortParser;
import com.bq.oss.lib.queries.request.Aggregation;
import com.bq.oss.lib.queries.request.Count;
import com.bq.oss.lib.queries.request.ResourceQuery;
import com.bq.oss.lib.queries.request.Search;
import com.bq.oss.lib.queries.request.Sort;
import com.bq.oss.lib.ws.annotation.Rest;
import com.bq.oss.lib.ws.queries.QueryParametersProvider.QueryParametersFactory;
import com.bq.oss.lib.ws.queries.QueryParametersProvider.QueryParametersFactoryProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Rubén Carrasco
 */
public class QueryParametersProviderTest {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE_SIZE = 10;

    MultivaluedMap<String, String> params;
    ContainerRequest request;
    QueryParametersFactory factory;

    @Rest public int restAnnotation;

    @Before
    public void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CustomJsonParser parser = new CustomJsonParser(mapper.getFactory());
        QueryParametersBuilder queryParametersBuilder = new QueryParametersBuilder(new JacksonQueryParser(parser),
                new JacksonAggregationParser(parser), new JacksonSortParser(parser), new DefaultPaginationParser(), new CustomSearchParser(
                        mapper));

        QueryParametersProvider provider = new QueryParametersProvider(DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE, queryParametersBuilder);
        QueryParametersFactoryProvider queryParametersFactoryProvider = new QueryParametersProvider.QueryParametersFactoryProvider(null,
                null);
        Parameter parameter = Parameter.create(null, null, false, QueryParameters.class, null, this.getClass().getField("restAnnotation")
                .getAnnotations());
        QueryParametersFactory createValueFactory = (QueryParametersFactory) queryParametersFactoryProvider.createValueFactory(parameter);
        factory = spy(createValueFactory);
        request = mock(ContainerRequest.class);
        doReturn(request).when(factory).getContainerRequestBind();
    }

    @Test
    public void test() {


        params = new MultivaluedHashMap();
        params.add(QueryParametersProvider.API_PAGE, "4");
        params.add(QueryParametersProvider.API_PAGE_SIZE, "20");
        params.add(QueryParametersProvider.API_SORT, "{\"price\":\"asc\"}");
        params.add(QueryParametersProvider.API_QUERY, "[{\"$eq\":{\"categories\":\"Metallica\"}}]");
        params.add(QueryParametersProvider.API_AGGREGATION, "{\"$count\":\"xxxx\"}");
        params.add(QueryParametersProvider.API_SEARCH, "{\"text\":\"Title+Test+T1\"}");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);

        QueryParameters parameters = factory.provide();
        assertThat(parameters.getPagination().getPage()).isEqualTo(4);
        assertThat(parameters.getPagination().getPageSize()).isEqualTo(20);
        Optional<Aggregation> countOperator = Optional.of(new Count("xxxx"));
        assertThat(parameters.getAggregation()).isEqualTo(countOperator);
        Optional<Sort> sort = Optional.of(new Sort("ASC", "price"));
        assertThat(parameters.getSort()).isEqualTo(sort);
        Optional<ResourceQuery> resourceQuery = Optional.of(new ResourceQueryBuilder().add("categories", "Metallica").build());
        assertThat(parameters.getQuery()).isEqualTo(resourceQuery);
        assertThat(parameters.getSearch()).isEqualTo(Optional.of(new Search("Title+Test+T1")));
    }

    @Test
    public void testDefaults() {
        params = new MultivaluedHashMap();
        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);

        QueryParameters parameters = factory.provide();
        assertThat(parameters.getPagination().getPage()).isEqualTo(0);
        assertThat(parameters.getPagination().getPageSize()).isEqualTo(DEFAULT_PAGE_SIZE);
        assertThat(parameters.getAggregation()).isEqualTo(Optional.empty());
        assertThat(parameters.getSort()).isEqualTo(Optional.empty());
        assertThat(parameters.getQuery()).isEqualTo(Optional.empty());
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters1() {
        params = new MultivaluedHashMap();
        params.add(QueryParametersProvider.API_PAGE, "asdf");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);
        factory.provide();
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters2() {
        params = new MultivaluedHashMap();
        params.add(QueryParametersProvider.API_PAGE_SIZE, "20000");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);
        factory.provide();
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters3() {
        params = new MultivaluedHashMap();
        params.add(QueryParametersProvider.API_SORT, "{\"price\":\"qwer\"}");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);
        factory.provide();
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters4() {
        params = new MultivaluedHashMap();
        params.add(QueryParametersProvider.API_QUERY, "[{\"$eq\":\"categories\":\"Metallica\"}}]");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);
        factory.provide();
    }

    @Test(expected = WebApplicationException.class)
    public void testBadParameters5() {
        params = new MultivaluedHashMap();
        params.add(QueryParametersProvider.API_AGGREGATION, "{\"$asdf\":\"xxxx\"}");

        ExtendedUriInfo uriInfoMock = mock(ExtendedUriInfo.class);
        when(uriInfoMock.getQueryParameters()).thenReturn(params);
        when(request.getUriInfo()).thenReturn(uriInfoMock);
        factory.provide();
    }
}
