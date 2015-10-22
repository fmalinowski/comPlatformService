package com.malinowski;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
/**
 * This class implements an HTTP server
 *
 */
public class EmailHttpServer {
	private HttpHandler emailRequestsHandler;
	private HttpServer httpServer;
	private int port;
	
	/**
	 * @param emailRequestsHandler Instance of EmailRequestsHandler
	 * @param port Port used to listen to incoming requests
	 */
	public EmailHttpServer(HttpHandler emailRequestsHandler, int port) {
		this.emailRequestsHandler = emailRequestsHandler;
		this.port = port;
	}
	
	public EmailHttpServer(HttpHandler emailRequestsHandler) {
		this(emailRequestsHandler, 8000);
	}
	
	/**
	 * Launch the server
	 */
	public void start() {
		
		if (httpServer == null) {
			try {
				httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
				httpServer.createContext("/email", emailRequestsHandler);
				httpServer.setExecutor(null);
				httpServer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}
	
	/**
	 * Stop the server
	 */
	public void stop() {
		if (httpServer != null) {
			httpServer.stop(0);
			httpServer = null;
		}
	}
}
