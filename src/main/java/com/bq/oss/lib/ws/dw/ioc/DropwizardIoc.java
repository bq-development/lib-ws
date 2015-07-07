/*
 * 
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.dw.ioc;

import io.dropwizard.jetty.GzipFilterFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.logging.AppenderFactory;
import io.dropwizard.logging.ConsoleAppenderFactory;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.logging.LoggingFactory;
import io.dropwizard.logging.SyslogAppenderFactory;
import io.dropwizard.server.DefaultServerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Level;

import com.google.common.collect.ImmutableList;

/**
 * @author Alexander De Leon
 */
@Configuration public class DropwizardIoc {

    @Autowired private Environment env;

    @Bean
    public DefaultServerFactory getHttpConfiguration() {
        DefaultServerFactory configuration = new DefaultServerFactory();
        ((HttpConnectorFactory) configuration.getApplicationConnectors().get(0)).setPort(env.getProperty("dw.http.port", Integer.class,
                8080));
        ((HttpConnectorFactory) configuration.getAdminConnectors().get(0)).setPort(env
                .getProperty("dw.http.adminPort", Integer.class, 8081));
        configuration.getRequestLogFactory().setAppenders(getLogConfiguration());
        configuration.setGzipFilterFactory(getGzipConfiguration());
        configuration.setRegisterDefaultExceptionMappers(false);
        return configuration;
    }

    @Bean
    public LoggingFactory getLoggingConfiguration() {
        LoggingFactory configuration = new LoggingFactory();
        configuration.setLevel(getLogLevel("dw.logging.level", "INFO"));
        configuration.setAppenders(getLogConfiguration());
        return configuration;
    }

    private ImmutableList<AppenderFactory> getLogConfiguration() {
        ImmutableList.Builder<AppenderFactory> appenders = ImmutableList.builder();
        getConsoleConfiguration(appenders);
        getSyslogConfiguration(appenders);
        getFileConfiguration(appenders);
        return appenders.build();
    }

    private void getConsoleConfiguration(ImmutableList.Builder<AppenderFactory> appenders) {
        if (env.getProperty("dw.logging.console.enabled", Boolean.class, true)) {
            ConsoleAppenderFactory configuration = new ConsoleAppenderFactory();
            configuration.setThreshold(getLogLevel("dw.logging.console.threshold", "ALL"));
            appenders.add(configuration);
        }
    }

    private void getSyslogConfiguration(ImmutableList.Builder<AppenderFactory> appenders) {
        if (env.getProperty("dw.logging.syslog.enabled", Boolean.class, true)) {
            SyslogAppenderFactory configuration = new SyslogAppenderFactory();
            configuration.setThreshold(getLogLevel("dw.logging.syslog.threshold", "ALL"));
            appenders.add(configuration);
        }
    }

    private void getFileConfiguration(ImmutableList.Builder<AppenderFactory> appenders) {
        if (env.getProperty("dw.logging.file.enabled", Boolean.class, false)) {
            FileAppenderFactory configuration = new FileAppenderFactory();
            configuration.setThreshold(getLogLevel("dw.logging.file.threshold", "ALL"));
            configuration.setCurrentLogFilename(env.getProperty("dw.logging.file.currentLogFilename", "./logs/app.log"));
            configuration.setArchivedLogFilenamePattern(env.getProperty("dw.logging.file.archivedLogFilenamePattern",
                    "./logs/app-%d.log.gz"));
            appenders.add(configuration);
        }
    }

    private GzipFilterFactory getGzipConfiguration() {
        GzipFilterFactory gzipConfiguration = new GzipFilterFactory();
        gzipConfiguration.setEnabled(false);
        return gzipConfiguration;
    }

    private Level getLogLevel(String property, String def) {
        String level = env.getProperty(property, def);
        return Level.toLevel(level);
    }

}
