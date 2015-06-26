package com.bq.oss.lib.ws.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.bq.oss.lib.queries.builder.QueryParametersBuilder;
import com.bq.oss.lib.queries.parser.AggregationParser;
import com.bq.oss.lib.queries.parser.CustomJsonParser;
import com.bq.oss.lib.queries.parser.CustomSearchParser;
import com.bq.oss.lib.queries.parser.DefaultPaginationParser;
import com.bq.oss.lib.queries.parser.JacksonAggregationParser;
import com.bq.oss.lib.queries.parser.JacksonQueryParser;
import com.bq.oss.lib.queries.parser.JacksonSortParser;
import com.bq.oss.lib.queries.parser.PaginationParser;
import com.bq.oss.lib.queries.parser.QueryParser;
import com.bq.oss.lib.queries.parser.SearchParser;
import com.bq.oss.lib.queries.parser.SortParser;
import com.bq.oss.lib.ws.api.provider.RemoteAddressProvider;
import com.bq.oss.lib.ws.queries.QueryParametersProvider;
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
                .getProperty("api.maxPageSize")), getQueryParametersBuilder());
    }

    @Bean
    public QueryParametersBuilder getQueryParametersBuilder() {
        return new QueryParametersBuilder(getQueryParser(), getAggregationParser(), getSortParser(), getPaginationParser(),
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
