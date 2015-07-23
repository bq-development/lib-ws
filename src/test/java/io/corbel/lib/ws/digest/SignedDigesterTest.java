package io.corbel.lib.ws.digest;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Alexander De Leon
 * 
 */
public class SignedDigesterTest {

	private static final String THE_SECRET = "the_secret";

	private Digester delagateMock;
	private SignedDigester signedDigester;

	@Before
	public void setup() {
		delagateMock = mock(Digester.class);
		signedDigester = new SignedDigester(delagateMock, THE_SECRET);
	}

	@Test
	public void test() {
		signedDigester.digest("hello");
		verify(delagateMock).digest("hello" + THE_SECRET);
	}

}
