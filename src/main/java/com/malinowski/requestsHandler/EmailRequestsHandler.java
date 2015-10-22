package com.malinowski.requestsHandler;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.malinowski.models.Email;
import com.malinowski.providers.EmailServiceProvider;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Implementation of the requests handler for the route /email
 */
@SuppressWarnings("restriction")
public class EmailRequestsHandler implements HttpHandler {
	private EmailServiceProvider serviceProvider;
	
	/**
	 * Instantiate an EmailRequestsHandler object
	 * @param serviceProvider EmailServiceProvider to be used to send email
	 * @throws IllegalArgumentException
	 */
	public EmailRequestsHandler(EmailServiceProvider serviceProvider) throws IllegalArgumentException {
		if (serviceProvider == null) {
			throw new IllegalArgumentException("serviceProvider can't be null");
		}
		this.serviceProvider = serviceProvider;
	}
	
	/**
	 * Handle the post request by validating the email fields and contacting the appropriate provider.
	 * @param json JSONObject representing the request made by the client
	 * @return true if the email fields are correct and the request to provider succeeded, returns false otherwise
	 */
	public boolean handlePostRequest(JSONObject json) {
		Email email;
		
		email = new Email(json);
		if (email.isValid() && serviceProvider.send(email)) {
			return true;
		}
		return false;
	}

	/**
	 * Implementation of the handle method of HttpHandler
	 * It sends back a 200 HTTP status code to the client if everything went well, 
	 * otherwise it returns a 400 Status code.
	 */
	public void handle(HttpExchange httpRequest) throws IOException {
		JSONObject jsonRequest;
		String requestBody;
		int httpStatusCodeResponse;
		
		if (httpRequest.getRequestMethod().equals("POST")) {
			try {
				requestBody = IOUtils.toString(httpRequest.getRequestBody());
				jsonRequest = new JSONObject(requestBody);
				
				httpStatusCodeResponse = handlePostRequest(jsonRequest) ? 200 : 400;
			} catch (Exception e) {
				httpStatusCodeResponse = 400;
			}
		}
		else {
			httpStatusCodeResponse = 400;
		}
		httpRequest.sendResponseHeaders(httpStatusCodeResponse, 0);
		httpRequest.close();
	}
}
