package io.corbel.lib.ws.auth.ioc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Alexander De Leon
 * 
 */
@Configuration @Import({AuthorizationFilterIoc.class, AuthorizationFilterWithPublicAccessIoc.class}) public class AuthorizationIoc {}