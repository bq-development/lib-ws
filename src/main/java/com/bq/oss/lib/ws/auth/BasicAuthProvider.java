/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.auth;




/**
 * @author Alexander De Leon
 *
 */
// public class BasicAuthProvider<T> extends io.dropwizard.auth.basic.BasicAuthProvider<T> {
//
// private String realm;
//
// public BasicAuthProvider(Authenticator<BasicCredentials, T> authenticator) {
// this(authenticator, null);
// }
//
// public BasicAuthProvider(Authenticator<BasicCredentials, T> authenticator, String realm) {
// super(authenticator, realm);
// this.realm = realm;
// }
//
// @Override
// public Injectable<?> getInjectable(ComponentContext context, Auth auth, Parameter param) {
// AbstractHttpContextInjectable<?> injectable = (AbstractHttpContextInjectable<?>) super.getInjectable(context, auth, param);
// return new AbstractHttpContextInjectable<Object>() {
// @Override
// public Object getValue(HttpContext context) {
// try {
// return injectable.getValue(context);
// } catch (WebApplicationException webApplicationException) {
// if (realm == null) {
// webApplicationException.getResponse().getMetadata().remove(HttpHeaders.WWW_AUTHENTICATE);
// }
// throw webApplicationException;
// }
// }
// };
// }
// }
