/**
 * Copyright (c) 1999, 2000 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.http;

import java.io.*;
import java.net.*;
import javax.servlet.http.HttpServletResponse;

//  ******************** HttpTransaction ********************
/**
 * * Whenever a connection has been made on the port the server is listening to, *
 * an HttpTransaction is created to deal with the request. The task of parsing
 * the * HTTP request and to generete the HTTP response is delegated to the
 * HttpRequest and * HttpResponse classes. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$ *
 * @see HttpServer *
 * @see HttpRequest *
 * @see HttpResponse
 */
public final class HttpTransaction extends Thread {
	private HttpServer	httpServer;
	private Log			log;
	final String		transId;
	Socket				client;
	private String		remoteAddress	= null;
	private String		remoteHost		= null;

	HttpTransaction(ThreadGroup parentGroup, HttpServer httpServer,
			int threadCount, Socket client) {
		super(parentGroup, "HttpServer.HttpTransaction-" + threadCount);
		this.httpServer = httpServer;
		this.client = client;
		transId = "HttpTransaction_#" + threadCount;
		log = new Log(transId);
		InetAddress i = client.getInetAddress();
		remoteAddress = i.getHostAddress();
		if ("true".equals(HttpConfig.getProperty("http.network.dnslookup",
				"false")))
			remoteHost = i.getHostName();
	}

	public void run() {
		DataOutputStream dataOut = null;
		BufferedInputStream inBuf = null;
		log.debug("Starting new transaction");
		try {
			dataOut = new DataOutputStream(new BufferedOutputStream(client
					.getOutputStream(), 1024));
			inBuf = new BufferedInputStream(client.getInputStream());
			HttpRequest request = new HttpRequest(httpServer, this, inBuf);
			HttpResponse response = new HttpResponse(httpServer, transId,
					dataOut, request);
			request.setResponse(response);
			int status_code = request.parseRequest();
			if (status_code != HttpServletResponse.SC_OK) {
				response.sendError(status_code);
			}
			else {
				response.createResponse();
			}
			response.sendResponse(request.getMethod());
		}
		catch (InterruptedIOException e) {
			log.debug("Stopping HttpTransaction: " + getName()
					+ " - client connection timed out");
		}
		catch (Exception e) {
			log.error("IOException in client: " + e);
		}
		finally {
			if (inBuf != null) {
				try {
					inBuf.close();
				}
				catch (Exception ignore) {
				}
			}
			if (dataOut != null) {
				try {
					dataOut.close();
				}
				catch (Exception ignore) {
				}
			}
			try {
				client.close();
			}
			catch (Exception ignore) {
			}
		}
	}

	/**
	 * Returns hostaddress taken from InetAddress of client socket.
	 * 
	 * @return HostAddress.
	 */
	public String getHostAddress() {
		return remoteAddress;
	}

	/**
	 * Returns hostname taken from InetAddress of client socket.
	 * 
	 * @return HostName
	 */
	public String getHostName() {
		return remoteHost;
	}
} // HttpTransaction
