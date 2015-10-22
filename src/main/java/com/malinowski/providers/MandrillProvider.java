package com.malinowski.providers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.malinowski.models.Email;

/**
 * Mandrill provider
 */
@SuppressWarnings("unused")
public class MandrillProvider implements EmailServiceProvider {
	private static final String API_URL = "https://mandrillapp.com/api/1.0/messages/send.json";
	private static final String API_KEY = "YOUR_KEY";
	
	/**
	 * Convert the Email object into the appropriate JSON object for the Mandrill Provider
	 * @param email Email object containing the information to be used by the provider
	 * @return JSONObject to be sent to the Mandrill Provider
	 */
	public JSONObject getJSONrequest(Email email) {
		JSONObject json = new JSONObject();
		JSONObject jsonMessage = new JSONObject();
		JSONObject jsonTo = new JSONObject();
		JSONArray jsonArrayTo = new JSONArray();
		
		jsonTo.put("email", email.getTo());
		jsonTo.put("name", email.getToName());
		
		jsonArrayTo.put(jsonTo);
		
		jsonMessage.put("text", email.getTextBody());
		jsonMessage.put("subject", email.getSubject());
		jsonMessage.put("from_email", email.getFrom());
		jsonMessage.put("from_name", email.getFromName());
		jsonMessage.put("to", jsonArrayTo);
		
		json.put("key", API_KEY);
		json.put("message", jsonMessage);
		
		return json;
	}
	
	@SuppressWarnings("deprecation")
	public boolean send(Email email) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(API_URL);
		
		StringEntity requestEntity;
		
		JSONObject json = getJSONrequest(email);
		
		try {
			requestEntity = new StringEntity(json.toString(), "application/json", "UTF-8");
			
			httppost.setEntity(requestEntity);
			httppost.setHeader("Content-type", "application/json");
			
			HttpResponse httpResponse = httpclient.execute(httppost);
			
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
