/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Test;

import com.bqreaders.silkroad.common.api.error.GenericExceptionMapper;
import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * @author Alexander De Leon
 * 
 */
public class VersionResourceTest {

	@ClassRule
	public static final ResourceTestRule RULE = ResourceTestRule.builder().addResource(new VersionResource()).build();

	@Test
	public void testVersionResponse() {
		Properties prop = new Properties();
		prop.setProperty("build.a", "1");
		prop.setProperty("build.b", "2");
		assertThat(RULE.client().resource("/version").get(Properties.class)).isEqualTo(prop);
	}

}
