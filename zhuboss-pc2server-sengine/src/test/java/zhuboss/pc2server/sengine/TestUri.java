package zhuboss.pc2server.sengine;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.util.UrlEncoded;

import junit.framework.TestCase;

public class TestUri extends TestCase {
	public void testURI() throws URISyntaxException{
		String uri = "/node?a";
		URI absolutePath = UriBuilder.fromUri(UrlEncoded.encodeString(uri)).build();
		System.out.println(absolutePath.getRawQuery());
		System.out.println(new URI(uri).getRawQuery());
		
	}
}
