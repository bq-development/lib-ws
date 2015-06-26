package com.bq.oss.lib.ws.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import com.google.common.base.Optional;

public class BasicAuthenticator implements Authenticator<BasicCredentials, AuthorizationInfo> {

    @Override
    public Optional<AuthorizationInfo> authenticate(BasicCredentials credentials) throws AuthenticationException {
        // TODO Auto-generated method stub
        return null;
    }

}
