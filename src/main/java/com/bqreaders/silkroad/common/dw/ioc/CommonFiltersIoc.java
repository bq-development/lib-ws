package com.bqreaders.silkroad.common.dw.ioc;

import com.bqreaders.silkroad.common.filter.CharsetResponseFilter;
import com.bqreaders.silkroad.common.filter.NoRedirectResponseFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by Alberto J. Rubio
 */
@Configuration
public class CommonFiltersIoc {

    @Autowired
    private Environment env;

    @Bean
    public CharsetResponseFilter getCharsetResponseFilter() {
        return new CharsetResponseFilter(
                env.getProperty("filter.charset.enabled", Boolean.class, true));
    }

    @Bean
    public NoRedirectResponseFilter getRedirectResponseFilter() {
        return new NoRedirectResponseFilter(
                env.getProperty("filter.noRedirect.enabled", Boolean.class, true));
    }
}
