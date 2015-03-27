/*
 * Copyright (C) 2013 StarTIC
 */
package com.bqreaders.silkroad.common.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resource to provide version information
 * 
 * @author Alexander De Leon
 * 
 */
@Path("/version")
@Produces(MediaType.APPLICATION_JSON)
public class VersionResource {

	private static final Logger LOG = LoggerFactory.getLogger(VersionResource.class);
	protected static final String DEFAULT_BUILD_METADATA_FILE = "/META-INF/build.properties";

	private final String[] propertyFiles;
	private Properties buildMetadataProperties;

	public VersionResource(String... buildMetadataFiles) {
		this.propertyFiles = buildMetadataFiles;
	}

	public VersionResource() {
		this(DEFAULT_BUILD_METADATA_FILE);
	}

	@GET
	public Response getVersion()  {
		Properties data = getBuildMetadataProperties();
		if (data == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(data).build();

	}

	private Properties getBuildMetadataProperties()  {
		if (buildMetadataProperties == null) {
			loadBuildMetadataProperties();
		}
		return buildMetadataProperties;
	}

	private void loadBuildMetadataProperties()  {
		LOG.info("loading build metadata file {} from classpath", propertyFiles);
		for (String propertyFile : propertyFiles) {
			InputStream buildPropertiesStream = VersionResource.class.getResourceAsStream(propertyFile);
			if (buildPropertiesStream != null) {
				Properties prop = new Properties();
				try {
					prop.load(buildPropertiesStream);
				} catch (IOException e) {
					LOG.warn("Problem loading metadata file: "+propertyFile, e);
				}
				buildMetadataProperties = prop;
				return;
			}
		}
		LOG.info("Build metadata file {} not found", propertyFiles);
	}
}
