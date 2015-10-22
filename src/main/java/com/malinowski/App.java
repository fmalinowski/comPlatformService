package com.malinowski;

import com.malinowski.providers.EmailServiceProvider;
import com.malinowski.providers.MailGunProvider;
import com.malinowski.providers.MandrillProvider;
import com.malinowski.requestsHandler.EmailRequestsHandler;

public class App 
{
    public static void main( String[] args )
    {
    	/*
    	 *  Uncomment this line (and comment the following one) to use MailGunProvider 
    	 *  instead of MandrillProvider.
    	 *  EmailServiceProvider emailServiceProvider = new MailGunProvider();
    	 */
    	EmailServiceProvider emailServiceProvider = new MandrillProvider();
    	EmailRequestsHandler emailRequestsHandler = new EmailRequestsHandler(emailServiceProvider);
        
    	System.out.println( "Running Communication Platform Service..." );
        EmailHttpServer httpServer = new EmailHttpServer(emailRequestsHandler);
        httpServer.start();
    }
}
