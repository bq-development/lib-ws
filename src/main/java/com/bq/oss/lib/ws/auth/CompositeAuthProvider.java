/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.auth;



/**
 * @author Alexander De Leon
 * 
 */
// public class CompositeAuthProvider implements InjectableProvider<Auth, Parameter> {
//
// private static final Logger LOG = LoggerFactory.getLogger(CompositeAuthProvider.class);
//
// private final Map<Class<?>, InjectableProvider<Auth, Parameter>> authProviders = new HashMap<>();
//
// public <E> void addAuthProvider(BasicAuthProvider<E> provider, Class<E> c) {
// addAuthProvider((InjectableProvider<Auth, Parameter>) provider, c);
// }
//
// public <E> void addAuthProvider(OAuthProvider<E> provider, Class<E> c) {
// addAuthProvider((InjectableProvider<Auth, Parameter>) provider, c);
// }
//
// public <E> void addAuthProvider(InjectableProvider<Auth, Parameter> provider, Class<E> c) {
// LOG.debug("Adding auth provider {} for parameter class {}", provider.getClass().getName(), c.getName());
// assertNotAlreadyRegistered(c);
// authProviders.put(c, provider);
// }
//
// @Override
// public ComponentScope getScope() {
// return ComponentScope.PerRequest;
// }
//
// @Override
// public Injectable<?> getInjectable(ComponentContext componentContext, Auth authAnnotation, Parameter parameter) {
// InjectableProvider<Auth, Parameter> provider = authProviders.get(parameter.getParameterClass());
// if (provider != null) {
// return provider.getInjectable(componentContext, authAnnotation, parameter);
// }
// if (authAnnotation.required()) {
// throw new IllegalStateException("No auth provider registered for required parameter of type " + parameter.getParameterClass());
// }
// return null;
// }
//
// private <E> void assertNotAlreadyRegistered(Class<E> c) {
// if (authProviders.containsKey(c)) {
// throw new IllegalStateException("Registering multiple providers for the same class: " + c.getName());
// }
// }
//
// }
