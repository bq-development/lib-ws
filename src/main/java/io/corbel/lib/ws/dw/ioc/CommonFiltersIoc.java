package io.corbel.lib.ws.dw.ioc;

import java.util.Collections;
import java.util.List;

import io.corbel.lib.ws.filter.CharsetResponseFilter;
import io.corbel.lib.ws.filter.NoRedirectResponseFilter;
import io.corbel.lib.ws.filter.RequestIdFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.corbel.lib.ws.filter.HeadersQueryParamsFilter;
import io.corbel.lib.ws.filter.ProxyLocationResponseRewriteFilter;
import io.corbel.lib.ws.filter.QueryParamsNotAllowedFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Alberto J. Rubio
 */
@Configuration public class CommonFiltersIoc {

    @Autowired private Environment env;

    @Bean
    public CharsetResponseFilter getCharsetResponseFilter() {
        return new CharsetResponseFilter(env.getProperty("filter.charset.enabled", Boolean.class, true));
    }

    @Bean
    public NoRedirectResponseFilter getRedirectResponseFilter() {
        return new NoRedirectResponseFilter(env.getProperty("filter.noRedirect.enabled", Boolean.class, true));
    }

    @Bean
    public ProxyLocationResponseRewriteFilter getProxyLocationResponseRewriteFilter() {
        return new ProxyLocationResponseRewriteFilter(env.getProperty("filter.proxyLocationResponseRewrite.enabled", Boolean.class, true));
    }

    @Bean
    public HeadersQueryParamsFilter getHeadersQueryParmamsFilter(ObjectMapper objectMapper) {
        return new HeadersQueryParamsFilter(env.getProperty("filter.headersQueryParams.enabled", Boolean.class, false), objectMapper);
    }

    @Bean
    public QueryParamsNotAllowedFilter getStrictQueryParamsFilter() {
        return new QueryParamsNotAllowedFilter(env.getProperty("filter.queryParamsNotAllowedFilter.enabled", Boolean.class, false),
                env.getProperty("filter.queryParamsNotAllowedFilter.methods", List.class, Collections.emptyList()));
    }

    @Bean
    public RequestIdFilter getRequestIdFilter() {
        return new RequestIdFilter();
    }

}
