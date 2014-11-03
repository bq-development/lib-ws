/*

 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.dw.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Level;

import com.yammer.dropwizard.config.GzipConfiguration;
import com.yammer.dropwizard.config.HttpConfiguration;
import com.yammer.dropwizard.config.LoggingConfiguration;
import com.yammer.dropwizard.config.LoggingConfiguration.ConsoleConfiguration;
import com.yammer.dropwizard.config.LoggingConfiguration.FileConfiguration;
import com.yammer.dropwizard.config.LoggingConfiguration.SyslogConfiguration;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration
public class DropwizardIoc {

	@Autowired
	private Environment env;

	@Bean
	public HttpConfiguration getHttpConfiguration() {
		HttpConfiguration configuration = new HttpConfiguration();
		configuration.setPort(env.getProperty("dw.http.port", Integer.class, 8080));
		configuration.setAdminPort(env.getProperty("dw.http.adminPort", Integer.class, 8081));
		configuration.setGzipConfiguration(getGzipConfiguration());
		return configuration;
	}

	@Bean
	public LoggingConfiguration getLogConfiguration() {
		LoggingConfiguration configuration = new LoggingConfiguration();
		configuration.setLevel(getLogLevel("dw.logging.level", "INFO"));
		configuration.setConsoleConfiguration(getConsoleConfiguration());
        configuration.setSyslogConfiguration(getSyslogConfiguration());
		configuration.setFileConfiguration(getFileConfiguration());
		return configuration;
	}

	@Bean
	public GzipConfiguration getGzipConfiguration() {
		GzipConfiguration gzipConfiguration = new GzipConfiguration();
		gzipConfiguration.setEnabled(false);
		return gzipConfiguration;
	}

	private ConsoleConfiguration getConsoleConfiguration() {
		ConsoleConfiguration configuration = new ConsoleConfiguration();
		configuration.setEnabled(env.getProperty("dw.logging.console.enabled", Boolean.class, true));
		configuration.setThreshold(getLogLevel("dw.logging.console.threshold", "ALL"));
		return configuration;
	}

    private SyslogConfiguration getSyslogConfiguration() {
        SyslogConfiguration configuration = new SyslogConfiguration();
        configuration.setEnabled(env.getProperty("dw.logging.syslog.enabled", Boolean.class, true));
        configuration.setThreshold(getLogLevel("dw.logging.syslog.threshold", "ALL"));
        return configuration;
    }

    private FileConfiguration getFileConfiguration() {
		FileConfiguration configuration = new FileConfiguration();
		configuration.setEnabled(env.getProperty("dw.logging.file.enabled", Boolean.class, false));
		configuration.setThreshold(getLogLevel("dw.logging.file.threshold", "ALL"));
		configuration.setCurrentLogFilename(env.getProperty("dw.logging.file.currentLogFilename", "./logs/app.log"));
		configuration.setArchivedLogFilenamePattern(env.getProperty("dw.logging.file.archivedLogFilenamePattern",
				"./logs/app-%d.log.gz"));
		return configuration;
	}

	private Level getLogLevel(String property, String def) {
		String level = env.getProperty(property, def);
		return Level.toLevel(level);
	}

}
