package io.corbel.lib.ws.ioc;

import io.corbel.lib.queries.parser.AggregationParser;
import io.corbel.lib.queries.parser.CustomJsonParser;
import io.corbel.lib.queries.parser.CustomSearchParser;
import io.corbel.lib.queries.parser.DefaultPaginationParser;
import io.corbel.lib.queries.parser.JacksonAggregationParser;
import io.corbel.lib.queries.parser.JacksonQueryParser;
import io.corbel.lib.queries.parser.JacksonSortParser;
import io.corbel.lib.queries.parser.PaginationParser;
import io.corbel.lib.queries.parser.QueryParametersParser;
import io.corbel.lib.queries.parser.QueryParser;
import io.corbel.lib.queries.parser.SearchParser;
import io.corbel.lib.queries.parser.SortParser;
import io.corbel.lib.ws.api.provider.RemoteAddressProvider;
import io.corbel.lib.ws.queries.QueryParametersProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

/**
 * @author Rubén Carrasco
 * 
 */
@Configuration public class QueriesIoc {

    @Autowired private Environment env;

    @Bean
    public QueryParametersProvider getSearchParametersProvider() {
        return new QueryParametersProvider(Integer.valueOf(env.getProperty("api.defaultPageSize")), Integer.valueOf(env
                .getProperty("api.maxPageSize")), getQueryParametersParser());
    }

    @Bean
    public QueryParametersParser getQueryParametersParser() {
        return new QueryParametersParser(getQueryParser(), getAggregationParser(), getSortParser(), getPaginationParser(),
                getSearchParser());
    }

    @Bean
    public SearchParser getSearchParser() {
        return new CustomSearchParser(getObjectMapper());
    }

    @Bean
    public AggregationParser getAggregationParser() {
        return new JacksonAggregationParser(getCustomJsonParser());
    }

    @Bean
    public CustomJsonParser getCustomJsonParser() {
        return new CustomJsonParser(getObjectMapper().getFactory());
    }

    @Bean
    public RemoteAddressProvider getRemoteAddressProvider() {
        return new RemoteAddressProvider();
    }

    @Bean
    public QueryParser getQueryParser() {
        return new JacksonQueryParser(getCustomJsonParser());
    }

    @Bean
    public SortParser getSortParser() {
        return new JacksonSortParser(getCustomJsonParser());
    }

    @Bean
    public PaginationParser getPaginationParser() {
        return new DefaultPaginationParser();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JSR310Module());
        return objectMapper;
    }


}
