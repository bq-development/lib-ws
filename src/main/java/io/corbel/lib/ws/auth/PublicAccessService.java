package io.corbel.lib.ws.auth;

import com.google.gson.JsonObject;

import java.util.Set;

/**
 *
 * @author Alberto J. Rubio
 *
 */
public interface PublicAccessService {
    Set<JsonObject> getDomainPublicRules(String domainId);
}
