package com.malinowski;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class EmailHttpServerTest {

	/* Fake HttpHandler for tests purposes */
	public class FakeEmailHttpHandler implements HttpHandler {
		public void handle(HttpExchange httpRequest) throws IOException {
			httpRequest.sendResponseHeaders(202, 0);
			httpRequest.close();
		}	
	}
	
	/**
	 * Test the start method in normal conditions
	 */
	@Test
	public void testStart() {
		FakeEmailHttpHandler fakeEmailHttpHandler = new FakeEmailHttpHandler();	
		
		EmailHttpServer emailHttpServer = new EmailHttpServer(fakeEmailHttpHandler);
		emailHttpServer.start();
		
		/* Prepare a HTTP Post request to /email */
		HttpPost httppost = new HttpPost("http://localhost:8000/email");
		HttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = null;
		
		/* Make the HTTP Post request to /email */
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// We make sure that the handle method of the FakeEmailHttpHandler has been properly called 
		assertEquals(202, response.getStatusLine().getStatusCode());
		emailHttpServer.stop();
	}
	
	/**
	 * Test the start method with a client calling an inexistent route
	 */
	@Test
	public void testStartWithInexistentRoute() {
		FakeEmailHttpHandler fakeEmailHttpHandler = new FakeEmailHttpHandler();	
		
		EmailHttpServer emailHttpServer = new EmailHttpServer(fakeEmailHttpHandler);
		emailHttpServer.start();
		
		/* Prepare a HTTP Post request to an inexistent route */
		HttpPost httppost = new HttpPost("http://localhost:8000/whatever");
		HttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = null;
		
		/* Make the HTTP Post request to inexistent route /whatever */
		try {
			response = httpclient.execute(httppost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(404, response.getStatusLine().getStatusCode());
		emailHttpServer.stop();
	}
	
	/**
	 * Test the stop method stops the server correctly
	 */
	@Test
	public void testStop() {
		FakeEmailHttpHandler fakeEmailHttpHandler = new FakeEmailHttpHandler();	
		
		EmailHttpServer emailHttpServer = new EmailHttpServer(fakeEmailHttpHandler);
		emailHttpServer.start();
		emailHttpServer.stop();
		
		// We make sure we can restart the server without having an exception
		emailHttpServer.start();
		emailHttpServer.stop();
	}
}
