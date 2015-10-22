package com.malinowski.requestsHandler;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sun.net.httpserver.HttpExchange;
import com.malinowski.models.Email;
import com.malinowski.providers.EmailServiceProvider;

@SuppressWarnings("restriction")
@RunWith(PowerMockRunner.class)
@PrepareForTest({EmailRequestsHandler.class})
public class EmailRequestsHandlerTest {
	
	/* Create a fake service provider for tests purposes */
	class ProviderMockClass implements EmailServiceProvider {
		public boolean send(Email email) {
			return true;
		}
	}
	
	/**
	 * Test that an exception is raised when instantiating with a null object
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testEmailRequestsHandlerWithException() {
		new EmailRequestsHandler(null);
	}

	/**
	 * Test handlePostRequest when the email is valid and the email provider returns a successful answer
	 * handlePostRequest should return true
	 */
	@Test
	public void testHandlePostRequestWhenEmailIsValidAndProviderSucceeded() {		
		JSONObject jsonRequest = new JSONObject();		
		
		ProviderMockClass providerMock = setupForHandlePostRequest(jsonRequest, true, true);
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		assertTrue(emailRequestsHandler.handlePostRequest(jsonRequest));
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test handlePostRequest when the email is invalid and the email provider returns a successful answer
	 * handlePostRequest should return false
	 */
	@Test
	public void testHandlePostRequestWhenEmailIsInValidAndProviderSucceeded() {		
		JSONObject jsonRequest = new JSONObject();
		
		ProviderMockClass providerMock = setupForHandlePostRequest(jsonRequest, false, true);
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		assertFalse(emailRequestsHandler.handlePostRequest(jsonRequest));
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test handlePostRequest when the email is valid and the email provider returns a failed answer
	 * handlePostRequest should return false
	 */
	@Test
	public void testHandlePostRequestWhenEmailIsValidAndProviderFailed() {		
		JSONObject jsonRequest = new JSONObject();
		
		ProviderMockClass providerMock = setupForHandlePostRequest(jsonRequest, true, false);
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		assertFalse(emailRequestsHandler.handlePostRequest(jsonRequest));
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test handlePostRequest when the email is invalid and the email provider returns a failed answer
	 * handlePostRequest should return false
	 */
	@Test
	public void testHandlePostRequestWhenEmailIsInValidAndProviderFailed() {		
		JSONObject jsonRequest = new JSONObject();
		
		ProviderMockClass providerMock = setupForHandlePostRequest(jsonRequest, false, false);
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		assertFalse(emailRequestsHandler.handlePostRequest(jsonRequest));
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test handle when the client made a POST request to /email and the email fields are valid
	 * We should return a 200 HTTP status code to the client
	 */
	@Test
	public void testHandleWithPostRequestAndEmailFieldsAreValid() {
		
		/* Creation of the mocks */
		HttpExchange httpRequestMock = PowerMock.createMock(HttpExchange.class);
		ProviderMockClass providerMock = PowerMock.createMock(ProviderMockClass.class);
		
		/* We expect the client to do a request different than a POST request */
		httpRequestMock.getRequestMethod();
		PowerMock.expectLastCall().andReturn("POST");
		
		/* The content of the client request is a JSON object */
		JSONObject json = new JSONObject();
		json.put("to", "to@whatever.com");
		json.put("to_name", "To");
		json.put("from", "from@whatever.com");
		json.put("from_name", "From");
		json.put("subject", "Subject");
		json.put("body", "Body");
		InputStream inputStream = new ByteArrayInputStream(json.toString().getBytes());
		httpRequestMock.getRequestBody();
		PowerMock.expectLastCall().andReturn(inputStream);
		
		/* The send method of the provider will be eventually called with an instance of email */
		providerMock.send((Email)EasyMock.anyObject());
		PowerMock.expectLastCall().andReturn(true);
		
		/* We expect to return a 200 status error code to the client and close the connection */
		try {
			httpRequestMock.sendResponseHeaders(200, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		httpRequestMock.close();
		
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		
		try {
			emailRequestsHandler.handle(httpRequestMock);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test handle when the client made a POST request to /email and the email fields are invalid
	 * We should return a 400 HTTP status code to the client
	 */
	@Test
	public void testHandleWithPostRequestAndEmailFieldsAreInValid() {
		
		/* Creation of the mocks */
		HttpExchange httpRequestMock = PowerMock.createMock(HttpExchange.class);
		ProviderMockClass providerMock = PowerMock.createMock(ProviderMockClass.class);
		
		/* We expect the client to do a request different than a POST request */
		httpRequestMock.getRequestMethod();
		PowerMock.expectLastCall().andReturn("POST");
		
		/* The content of the client request is a JSON object */
		JSONObject json = new JSONObject();
		InputStream inputStream = new ByteArrayInputStream(json.toString().getBytes());
		httpRequestMock.getRequestBody();
		PowerMock.expectLastCall().andReturn(inputStream);
		
		/* We expect to return a 400 status error code to the client and close the connection 
		 * because email fields are not correct */
		try {
			httpRequestMock.sendResponseHeaders(400, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		httpRequestMock.close();
		
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		
		try {
			emailRequestsHandler.handle(httpRequestMock);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}

	/**
	 * Test handle when the client makes a request other than POST
	 * We should return a 400 HTTP status code to the client
	 */
	@Test
	public void testHandleWhenRequestOtherThanPostIsMade() {
		
		/* Creation of the mocks */
		HttpExchange httpRequestMock = PowerMock.createMock(HttpExchange.class);
		
		/* We expect the client to do a request different than a POST request */
		httpRequestMock.getRequestMethod();
		PowerMock.expectLastCall().andReturn("GET");
		
		/* We expect to return a 400 status error code to the client and close the connection */
		try {
			
			httpRequestMock.sendResponseHeaders(400, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		httpRequestMock.close();
		
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		ProviderMockClass providerMock = new ProviderMockClass();
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		
		try {
			emailRequestsHandler.handle(httpRequestMock);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test handle when the client makes a POST request to /email but send something else
	 * than a JSON Object
	 * We should return a 400 HTTP status code to the client
	 */
	@Test
	public void testHandleWhenContentOfRequestIsNotJSONobject() {
		
		/* Creation of the mocks */
		HttpExchange httpRequestMock = PowerMock.createMock(HttpExchange.class);
		
		/* We expect the client to do a request different than a POST request */
		httpRequestMock.getRequestMethod();
		PowerMock.expectLastCall().andReturn("POST");
		
		/* The content of the client request is not a JSON object */
		InputStream inputStream = new ByteArrayInputStream("This is not a JSON object".getBytes());
		httpRequestMock.getRequestBody();
		PowerMock.expectLastCall().andReturn(inputStream);
		
		/* We expect to return a 400 status error code to the client and close the connection */
		try {
			
			httpRequestMock.sendResponseHeaders(400, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		httpRequestMock.close();
		
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		ProviderMockClass providerMock = new ProviderMockClass();
		EmailRequestsHandler emailRequestsHandler  = new EmailRequestsHandler(providerMock);
		
		try {
			emailRequestsHandler.handle(httpRequestMock);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}

	/**
	 * Setup for the handle tests. We mock the required objects and set up the expectations
	 * @param jsonRequest JSONObject representing the client request
	 * @param isEmailValid Boolean indicating whether we simulate a valid email fields sent by the client
	 * @param hasProviderRequestSucceeded Boolean indicating whether we simulate a successful processed request by the email provider 
	 * @return
	 */
	private ProviderMockClass setupForHandlePostRequest(JSONObject jsonRequest, boolean isEmailValid, boolean hasProviderRequestSucceeded) {
		/* Creation of the mocks */
		ProviderMockClass providerMock = PowerMock.createMock(ProviderMockClass.class);
		Email emailMock = PowerMock.createMock(Email.class);
		
		/* Expect the constructor of Email to be called and return the email mock*/
		try {
			PowerMock.expectNew(Email.class, jsonRequest).andReturn(emailMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* Expect isValid to be called */
		emailMock.isValid();
		PowerMock.expectLastCall().andReturn(isEmailValid ? true : false);
		
		/* Expect send method of the provider to be called */
		if (isEmailValid) {
			providerMock.send(emailMock);
			PowerMock.expectLastCall().andReturn(hasProviderRequestSucceeded ? true : false);
		}
		
		return providerMock;
	}
	
}
