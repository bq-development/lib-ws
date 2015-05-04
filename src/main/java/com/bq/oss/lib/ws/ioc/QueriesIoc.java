package com.bq.oss.lib.ws.ioc;

import com.bq.oss.lib.queries.parser.AggregationParser;
import com.bq.oss.lib.queries.parser.CustomJsonParser;
import com.bq.oss.lib.queries.parser.JacksonAggregationParser;
import com.bq.oss.lib.queries.parser.JacksonQueryParser;
import com.bq.oss.lib.queries.parser.QueryParser;
import com.bq.oss.lib.ws.api.provider.RemoteAddressProvider;
import com.bq.oss.lib.ws.queries.QueryParametersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import java.lang.Integer;

/**
 * @author Rubén Carrasco
 * 
 */

@Configuration
public class QueriesIoc {

	@Autowired
	private Environment env;

	@Bean
	public QueryParametersProvider getSearchParametersProvider() {
		return new QueryParametersProvider(Integer.valueOf(env.getProperty("api.defaultPageSize")), Integer.valueOf(env
				.getProperty("api.maxPageSize")), getQueryParser(), getAggregationParser());
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
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new JSR310Module());
		return objectMapper;
	}
}
