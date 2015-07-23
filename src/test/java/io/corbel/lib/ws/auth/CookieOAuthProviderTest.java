package io.corbel.lib.ws.auth;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.Authenticator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.glassfish.jersey.server.model.Parameter;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.sun.jndi.toolkit.ctx.ComponentContext;


/**
 * @author Rubén Carrasco
 *
 */
public class CookieOAuthProviderTest {

    private static final String TOKEN = "tokenTest";

    CookieOAuthFactory<TestClass> cookieOAuthProvider;

    private Authenticator<String, TestClass> authenticator;

    private HttpServletRequest request;

    private Auth a;

    @Before
    public void setUp() throws Exception {
        authenticator = mock(Authenticator.class);
        Optional<TestClass> value = Optional.of(new TestClass());
        when(authenticator.authenticate(TOKEN)).thenReturn(value);
        cookieOAuthProvider = new CookieOAuthFactory<TestClass>(authenticator, "realm", TestClass.class);

        a = mock(Auth.class);
        when(a.required()).thenReturn(true);

        request = mock(HttpServletRequest.class);
        cookieOAuthProvider.setRequest(request);
        Cookie[] cookies = new Cookie[] {new Cookie("token", TOKEN)};
        when(request.getCookies()).thenReturn(cookies);
    }

    @Test
    public void test() {
        ComponentContext ic = null;
        Parameter c = null;
        assertThat(cookieOAuthProvider.provide()).isInstanceOf(TestClass.class);
    }

    @Test
    public void testNoCookie() {
        ComponentContext ic = null;
        Parameter c = null;

        when(request.getCookies()).thenReturn(null);

        assertThat(cookieOAuthProvider.provide()).isNull();
    }

    private class TestClass {

    }

}
