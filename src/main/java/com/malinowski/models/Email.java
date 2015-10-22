package com.malinowski.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class Email {
	private String to;
	private String toName;
	private String from;
	private String fromName;
	private String subject;
	private String body;

	/**
	 * Instantiate an Email
	 * @param json Instance of JSONObject containing the email input values submitted by a client
	 */
	public Email(JSONObject json) {
		this.to = json.optString("to", "");
		this.toName = json.optString("to_name", "");
		this.from = json.optString("from", "");
		this.fromName = json.optString("from_name", "");
		this.subject = json.optString("subject", "");
		this.body = json.optString("body", "");
	}
	
	/**
	 * Run the validations for the email
	 */
	public boolean isValid() {
		return isToValid() && isFromValid() && isToNameValid() && 
				isFromNameValid() && isSubjectValid() && isBodyValid();
	}

	public String getTo() {
		return to;
	}

	public String getToName() {
		return toName;
	}

	public String getFrom() {
		return from;
	}

	public String getFromName() {
		return fromName;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}
	
	/**
	 * Body of the email without the HTML tags
	 */
	public String getTextBody() {
		return Jsoup.parse(body).text();
	}
	
	private boolean isToValid() {
		return isEmailValid(this.to);
	}
	
	private boolean isFromValid() {
		return isEmailValid(this.from);
	}
	
	private boolean isToNameValid() {
		return !this.toName.isEmpty();
	}
	
	private boolean isFromNameValid() {
		return !this.fromName.isEmpty();
	}
	
	private boolean isSubjectValid() {
		return !this.subject.isEmpty();
	}
	
	private boolean isBodyValid() {
		return !this.body.isEmpty();
	}
	
	private boolean isEmailValid(String s) {
		String emailPattern = "^[A-Za-z0-9.%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
		Pattern pattern = Pattern.compile(emailPattern);
		Matcher matcher = pattern.matcher(s);
		
		return matcher.find() ? true : false;
	}
}
