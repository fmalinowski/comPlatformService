package com.malinowski.providers;

import com.malinowski.models.Email;

/**
 * Abstraction of the Email Service Providers (MailGun or Mandrill)
 */
public interface EmailServiceProvider {
	
	/**
	 * Send an email to the email provider
	 * @param email Instance of Email containing information that will be used by the email providers
	 * @return true if everything went well on the email provider side, false otherwise
	 */
	public boolean send(Email email);
	
}
