package com.malinowski.providers;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.malinowski.models.Email;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClients.class, HttpPost.class, MailGunProvider.class, UsernamePasswordCredentials.class})
public class MailGunProviderTest {

	/**
	 * Test the send method sends correct data to email provider
	 */
	@Test
	public void testSendWithoutFailureOnProviderSide() {
		MailGunProvider mailGunProvider = new MailGunProvider();
		HttpPost httpPost = new HttpPost("whatever");
		
		setupForSendTests(mailGunProvider, httpPost, true);
		
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		JSONObject json = new JSONObject();
		json.put("to", "to@whatever.com");
		json.put("to_name", "To");
		json.put("from", "from@whatever.com");
		json.put("from_name", "From");
		json.put("subject", "Subject");
		json.put("body", "Body");
		
		Email email = new Email(json);
		
		// We assert that the call to send returns true (everything went well on provider side)
		assertTrue(mailGunProvider.send(email));
		
		HttpEntity entity;
		String entityString, expectedStringSentToServer, expectedAuth;
		
		// Check the content of the POST request sent to MailGun (nothing is sent, it's mocked)
		entity = httpPost.getEntity();
		entityString = null;
		
		try {			
			entityString = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Assert that we send correct information to MailGun
		expectedStringSentToServer = "from=From+%3Cfrom%40whatever.com%3E&to=To+%3Cto%40whatever.com%3E&subject=Subject&text=Body";
		assertEquals(expectedStringSentToServer, entityString);
		
		// Test header (credentials) are correct
		expectedAuth = "Basic YXBpOndoYXRldmVyQVBJa2V5"; 
		Header header = httpPost.getFirstHeader("Authorization");
		
		assertNotNull(header);
		assertEquals(expectedAuth, header.getValue());
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Test the send method returns false when an error occurred on the email provider side
	 */
	@Test
	public void testSendWithFailureOnProviderSide() {
		MailGunProvider mailGunProvider = new MailGunProvider();
		HttpPost httpPost = new HttpPost("whatever");
		
		setupForSendTests(mailGunProvider, httpPost, false);
		
		/* We're done with the configuration of the mocks and expectations. Let's test now */
		PowerMock.replayAll();
		
		JSONObject json = new JSONObject();
		json.put("to", "to@whatever.com");
		json.put("to_name", "To");
		json.put("from", "from@whatever.com");
		json.put("from_name", "From");
		json.put("subject", "Subject");
		json.put("body", "Body");
		
		Email email = new Email(json);
		
		// We assert that the call to send returns false (something happened on provider side)
		assertFalse(mailGunProvider.send(email));
		
		/* Make sure all the expected calls were made */
		PowerMock.verifyAll();
	}
	
	/**
	 * Setup the mock objects and expectation calls for the send test
	 * @param mailGunProvider
	 * @param httpPost HttpPost object that will be used when instantiated. It is used eventually in the test to make sure it sends the correct data to the email provider.
	 * @param providerSucceed Boolean indicating whether we should simulate a success request on the provider side or not.
	 */
	private void setupForSendTests(MailGunProvider mailGunProvider, HttpPost httpPost, boolean providerSucceed) {
		
		Field apiUrlField, apiKeyField;
		String api_url, api_key;
		
		api_url = null;
		api_key = null;
		
		// We get the constants API_URL and API_KEY just for the purpose of the test
		try {
			apiUrlField = MailGunProvider.class.getDeclaredField("API_URL");
			apiUrlField.setAccessible(true);
			api_url = (String)apiUrlField.get(mailGunProvider);
			
			apiKeyField = MailGunProvider.class.getDeclaredField("API_KEY");
			apiKeyField.setAccessible(true);
			api_key = (String)apiKeyField.get(mailGunProvider);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Mock all static methods of HttpClients (particularly createDefault)
		PowerMock.mockStatic(HttpClients.class);
		
		// Create a HttpClient mock object (expect HttpClients.createDefault to be called)
		HttpClient httpClientMock = PowerMock.createMock(CloseableHttpClient.class);
		
		HttpClients.createDefault();
		PowerMock.expectLastCall().andReturn(httpClientMock);
		
		/* Expect the constructor of HttpPost to be called and return httpPost */
		try {
			PowerMock.expectNew(HttpPost.class, api_url).andReturn(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* Expect the constructor of UsernamePasswordCredentials to be called and return credentials */
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("api", "whateverAPIkey");
		try {
			PowerMock.expectNew(UsernamePasswordCredentials.class, "api", api_key).andReturn(credentials);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Expect execute method to be called on httpClient with httpPost
		// It should return also an httpResponse that we mock
		// We mock also a StatusLine that is accessible from httpResponse to get HTTP status code
		CloseableHttpResponse httpResponseMock = PowerMock.createMock(CloseableHttpResponse.class);
		StatusLine statusLineMock = PowerMock.createMock(StatusLine.class);
		
		try {
			httpClientMock.execute(httpPost);
			PowerMock.expectLastCall().andReturn(httpResponseMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// expect getStatusLine to be called and return a statusLine object (here we mock it)
		httpResponseMock.getStatusLine();
		PowerMock.expectLastCall().andReturn(statusLineMock);
		
		// expect getStatusCode to be called and return 200 (everything went well on provider side)
		// or return status code 500 if we decide that provider failed
		statusLineMock.getStatusCode();
		
		if (providerSucceed) {
			PowerMock.expectLastCall().andReturn(200);
		}
		else {
			PowerMock.expectLastCall().andReturn(500);
		}
	}
}
