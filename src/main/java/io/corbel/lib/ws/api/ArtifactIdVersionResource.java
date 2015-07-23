package io.corbel.lib.ws.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander De Leon
 * 
 */
@Path("/version")
@Produces(MediaType.APPLICATION_JSON)
public class ArtifactIdVersionResource extends VersionResource {

	private static final Logger LOG = LoggerFactory.getLogger(ArtifactIdVersionResource.class);

	public ArtifactIdVersionResource(String artifactId) {
		super("/META-INF/" + artifactId + "-build.properties", DEFAULT_BUILD_METADATA_FILE);
	}

}
