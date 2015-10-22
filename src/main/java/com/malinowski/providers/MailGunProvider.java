package com.malinowski.providers;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.malinowski.models.Email;

/**
 * MailGun provider
 */
public class MailGunProvider implements EmailServiceProvider {
	private static final String API_URL = "YOUR_URL";
	private static final String API_KEY = "YOUR_KEY";

	public boolean send(Email email) {
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(API_URL);
		
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("api", API_KEY);
		
		try {
			httppost.addHeader(new BasicScheme().authenticate(creds, httppost, null));
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(4);
			params.add(new BasicNameValuePair("from", email.getFromName() + " <"+ email.getFrom() + ">"));
			params.add(new BasicNameValuePair("to", email.getToName() + " <" + email.getTo() + ">"));
			params.add(new BasicNameValuePair("subject", email.getSubject()));
			params.add(new BasicNameValuePair("text", email.getTextBody()));
			
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
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
