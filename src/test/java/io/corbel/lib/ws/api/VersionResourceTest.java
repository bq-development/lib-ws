package io.corbel.lib.ws.api;

import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.util.Properties;

import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Alexander De Leon
 * 
 */
public class VersionResourceTest {

    @ClassRule public static final ResourceTestRule RULE = ResourceTestRule.builder().addResource(new VersionResource()).build();

    @Test
    public void testVersionResponse() {
        Properties prop = new Properties();
        prop.setProperty("build.a", "1");
        prop.setProperty("build.b", "2");
        assertThat(RULE.client().target("/version").request().get(Properties.class)).isEqualTo(prop);
    }

}
