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
import java.util.*;
import javax.servlet.*;
import org.osgi.framework.Bundle;
import org.osgi.service.http.NamespaceException;

//  ******************** HttpServer ********************
/**
 * * This is the actual HTTP server. The server listens to a * port, typically
 * 80 or 8080 for HTTP requests. When a request is * received an HttpTransaction
 * is created to deal with the request. *
 * <p>* The server extends Thread and the thread method run() hangs on a * call
 * to serverSock.accept() until the server is stoped. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$ * *
 * @see HttpTransaction *
 */
public final class HttpServer extends Thread {
	private ServerSocket	serverSock;									// The
																		   // server
																		   // socket
																		   // we
																		   // are
																		   // listening
																		   // to
	private boolean			done		= false;							// Flag
																			   // indicating
																			   // when
																			   // to
																			   // stop
	private Hashtable		registrations;									// alias
																			   // ->
																			   // AliasRegistration
	private Vector			servlets;										// registered
																			   // servlets
	private Bundle			bundle;										// The
																			 // HttpServers'
																			 // own
																			 // bundle
																			 // object
	private ThreadGroup		threadGroup	= new ThreadGroup(
												"HttpTransactionManager");
	private Log				log			= new Log("HttpServer");			;
	final ServletEngine		servletEngine;
	final String			baseURL;
	// Some statistics
	private int				threadCount	= 0;

	public HttpServer(Bundle bundle) {
		super("HttpServer");
		this.bundle = bundle;
		registrations = new Hashtable();
		servlets = new Vector();
		// Check and set hostname
		String httpHostname = System
				.getProperty("org.osgi.service.http.hostname");
		if (httpHostname == null) {
			try {
				httpHostname = InetAddress.getLocalHost().getHostName();
			}
			catch (UnknownHostException e) {
				httpHostname = "localhost";
			}
		}
		// Try binding the HttpServer to a port.
		// First check if "org.osgi.service.http.port" is set
		String userPort = System.getProperty("org.osgi.service.http.port");
		if (userPort != null) {
			log.info("Trying user supplied HTTP port: " + userPort);
			if (!bindPort(Integer.parseInt(userPort))) {
				log
						.error("Cannot use port specified by property osr.osgi.service.http.port");
				throw new IllegalArgumentException(
						"Cannot use port specified by property org.osgi.service.http.port ["
								+ userPort + "]");
			}
		}
		else {
			// Use port as specified in config. First try primary port
			int port;
			if (!bindPort(port = Integer.parseInt(HttpConfig.getProperty(
					"port.primary", "80")))) {
				log
						.warn("Failed to use use server socket primary port, trying secondary port");
				// Try secondary port
				if (!bindPort(port = Integer.parseInt(HttpConfig.getProperty(
						"port.secondary", "8080")))) {
					String msg = "Secondary port failed as well. No port, cannot continue!";
					log.error(msg);
					throw new IllegalArgumentException(msg);
				}
			}
		}
		log.info("HTTP service will use port " + getServerPort());
		baseURL = "http://" + httpHostname + ":" + getServerPort();
		// Create the servlet engine, pass along the ThreadGroup
		this.servletEngine = new ServletEngine(this, threadGroup);
	}

	/**
	 * * Starts server. Blocks on accept() until shutdown() is called
	 */
	public void run() {
		log.info("Starting to listen for HTTP requests");
		try {
			while (!done) {
				Socket client = serverSock.accept(); // Listen for incoming
													 // requests.
				(new HttpTransaction(threadGroup, this, threadCount++, client))
						.start();
			}
		}
		catch (IOException e) {
			if (!done) {
				log.error("FATAL - Communication error in HttpServer, exiting",
						e);
				throw new RuntimeException("Communication error in HttpServer");
			}
		}
	}

	/**
	 * * Stops the HTTP server. Closes the server socket and returns
	 */
	public void shutdown() throws IOException {
		done = true;
		servletEngine.close();
		serverSock.close();
		Log.close();
		try {
			join();
		}
		catch (InterruptedException ignore) {
		}
	}

	//----------------------------------------------------------------------------
	//            Methods for dealing with registrations
	//----------------------------------------------------------------------------
	public void addServlet(Servlet servlet) throws ServletException {
		if (servlets.contains(servlet))
			throw new ServletException("Servlet already registered");
		servlets.addElement(servlet);
	}

	public void removeServlet(Servlet servlet) {
		servlets.removeElement(servlet);
	}

	public void addRegistration(AliasRegistration reg)
			throws NamespaceException {
		synchronized (registrations) {
			if (registrations.containsKey(reg.alias))
				throw new NamespaceException("Alias already registered: "
						+ reg.alias);
			registrations.put(reg.alias, reg);
		}
	}

	public AliasRegistration getRegistration(String alias) {
		return (AliasRegistration) registrations.get(alias);
	}

	public void removeRegistration(AliasRegistration reg) {
		synchronized (reg) {
			registrations.remove(reg.getAlias());
			reg.destroy();
		}
	}

	void removeBundleRegistrations(Bundle bundle) {
		AliasRegistration aliasReg;
		int i = 0;
		for (Enumeration enumeration = registrations.elements(); enumeration
				.hasMoreElements(); i++) {
			aliasReg = (AliasRegistration) enumeration.nextElement();
			if (bundle == aliasReg.getBundle()) {
				removeRegistration(aliasReg);
			}
		}
	}

	public AliasMatch findMatchingRegistration(String uri) {
		uri = decodeURL(uri);
		StringBuffer sbuf = new StringBuffer(uri);
		AliasRegistration aliasReg;
		AliasMatch aliasMatch;
		int i = sbuf.length();
		log.debug("Checking uri: " + uri);
		while (i >= 0) {
			String s = sbuf.toString();
			log.debug("Checking match: " + s);
			if ((aliasReg = (AliasRegistration) registrations.get(s)) != null
					&& (aliasMatch = aliasReg.getMatch(uri)) != null) {
				log.debug("Matches against: " + s);
				return aliasMatch;
			}
			while (--i >= 0) {
				if (sbuf.charAt(i) == '/') {
					sbuf.setLength(i);
					break;
				}
			}
		}
		// Nothing matched that also exists, return null
		log.debug("Sorry, no match");
		return null;
	}

	//----------------------------------------------------------------------------
	//    Some utility methods
	//----------------------------------------------------------------------------
	int getServerPort() {
		return (serverSock != null) ? serverSock.getLocalPort() : -1;
	}

	String getMimeType(String request) {
		int pos;
		String type = null;
		if ((pos = request.lastIndexOf('.')) != -1) {
			type = HttpConfig.getProperty("mimetype" + request.substring(pos));
		}
		return (type != null) ? type : HttpConfig.DEFAULT_MIME_TYPE;
	}

	String getManifestValue(String key, String def) {
		Dictionary d = bundle.getHeaders();
		Object o = d.get(key);
		return (o != null && (o instanceof String)) ? ((String) o) : def;
	}

	public static String decodeURL(String url) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			if (c == '%') {
				try {
					sb.append((char) Integer.parseInt(url.substring(i + 1,
							i + 3), 16));
				}
				catch (NumberFormatException nfe) {
					throw new IllegalArgumentException();
				}
				i += 2;
			}
			else {
				sb.append(c);
			}
		}
		// Undo conversion to external encoding
		String result = sb.toString();
		try {
			byte[] inputBytes = result.getBytes("8859_1");
			result = new String(inputBytes);
		}
		catch (UnsupportedEncodingException uee) {
			// The system should always have 8859_1
		}
		return result;
	}

	//----------------------------------------------------------------------------
	//            INTERNAL METHODS
	//----------------------------------------------------------------------------
	private boolean bindPort(int port) {
		try {
			serverSock = new ServerSocket(port);
			return true;
		}
		catch (IOException e) {
			log.warn("Could not use port " + port + ": ", e);
			return false;
		}
	}
}
