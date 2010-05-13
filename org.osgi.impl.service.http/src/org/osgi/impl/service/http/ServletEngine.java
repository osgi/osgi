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

import java.security.AccessControlContext;
import java.util.*;
import javax.servlet.http.*;
import org.osgi.service.http.HttpContext;

//  ******************** ServletEngine ********************
/**
 * * The servlet engine, i.e. sets up the environment for the servlets. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Id$
 */
public final class ServletEngine implements HttpSessionContext {
	private HttpServer			httpServer;
	private Hashtable			sessions		= new Hashtable();			// Hashtable
																			   // with
																			   // all
																			   // current
																			   // HttpSession's
	private Hashtable			contexts		= new Hashtable();			// HttpContext
																			   // ->
																			   // ServletContext
	private int					sessionCount	= 0;
	private Log					log				= new Log("ServletEngine");
	private Hashtable			statusCodes		= new Hashtable();			// Status
																				  // code
																				  // ->
																				  // info
																				  // message
	private HttpSessionTimeout	sessionTimeoutThread;

	public ServletEngine(HttpServer httpServer, ThreadGroup threadGroup) {
		this.httpServer = httpServer;
		// Setup status codes
		setupStatusCodes();
		// Create timeout thread for servlet created sessions.
		sessionTimeoutThread = new HttpSessionTimeout(threadGroup, this);
	}

	public void close() {
		sessionTimeoutThread.close();
	}

	//----------------------------------------------------------------------------
	//            METHODS FOR DEALING WITH SESSIONS
	//----------------------------------------------------------------------------
	// Creates an HttpSession with a unique id
	HttpSessionImpl createHttpSession(AliasRegistration reg) {
		String id = "osgirefsession." + reg.getBundle().getBundleId() + "."
				+ sessionCount++ + "." + System.currentTimeMillis();
		HttpSessionImpl session = new HttpSessionImpl(this, reg, id);
		sessions.put(session.getId(), session);
		log.info("Created new HTTP session: " + session.getId());
		return session;
	}

	HttpSessionImpl getHttpSession(String sessionId) {
		if (sessionId == null)
			return null;
		HttpSessionImpl session = (HttpSessionImpl) sessions.get(sessionId);
		if (session != null)
			session.setSessionAccessed();
		return session;
	}

	void removeHttpSession(String sessionId) {
		sessions.remove(sessionId);
	}

	Enumeration getHttpSessions() {
		return sessions.elements();
	}

	//----------------------------------------------------------------------------
	//            Methods for dealing with ServletContexts
	//----------------------------------------------------------------------------
	ServletContextImpl getServletContext(HttpContext httpContext, Log log,
			AccessControlContext acc) {
		ServletContextImpl contxt = (ServletContextImpl) contexts
				.get(httpContext);
		if (contxt != null) {
			contxt.refCnt++;
		}
		else {
			contxt = new ServletContextImpl(httpServer, httpContext, log, acc);
			contxt.refCnt = 1;
			contexts.put(httpContext, contxt);
		}
		return contxt;
	}

	void removeServletContext(HttpContext httpContext, ServletContextImpl contxt) {
		if (--contxt.refCnt == 0) {
			contexts.remove(httpContext);
		}
	}

	//----------------------------------------------------------------------------
	//            IMPLEMENTS - HttpSessionContext (which is deprecated)
	//----------------------------------------------------------------------------
	/**
	 * *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public HttpSession getSession(String sessionId) {
		return null;
	}

	/**
	 * *
	 * 
	 * @deprecated Not used in JSDK 2.1
	 */
	public Enumeration getIds() {
		return null;
	}

	//----------------------------------------------------------------------------
	//            HTTP status codes
	//----------------------------------------------------------------------------
	String getStatusMessage(int statusCode) {
		String statusMessage = (String) statusCodes
				.get(new Integer(statusCode));
		if (statusMessage == null)
			statusMessage = "Unknown error code (" + statusCode + ")";
		return statusMessage;
	}

	private void setupStatusCodes() {
		statusCodes.put(new Integer(HttpServletResponse.SC_CONTINUE),
				"Continue");
		statusCodes.put(
				new Integer(HttpServletResponse.SC_SWITCHING_PROTOCOLS),
				"Switching Protocols");
		statusCodes.put(new Integer(HttpServletResponse.SC_OK), "OK");
		statusCodes.put(new Integer(HttpServletResponse.SC_CREATED), "Created");
		statusCodes.put(new Integer(HttpServletResponse.SC_ACCEPTED),
				"Accepted");
		statusCodes.put(new Integer(
				HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION),
				"Non-Authoritative Information");
		statusCodes.put(new Integer(HttpServletResponse.SC_NO_CONTENT),
				"No Content");
		statusCodes.put(new Integer(HttpServletResponse.SC_RESET_CONTENT),
				"Reset Content");
		statusCodes.put(new Integer(HttpServletResponse.SC_PARTIAL_CONTENT),
				"Partial Content");
		statusCodes.put(new Integer(HttpServletResponse.SC_MULTIPLE_CHOICES),
				"Multiple Choices");
		statusCodes.put(new Integer(HttpServletResponse.SC_MOVED_PERMANENTLY),
				"Moved Permanently");
		statusCodes.put(new Integer(HttpServletResponse.SC_MOVED_TEMPORARILY),
				"Moved Temporarily");
		statusCodes.put(new Integer(HttpServletResponse.SC_SEE_OTHER),
				"See Other");
		statusCodes.put(new Integer(HttpServletResponse.SC_NOT_MODIFIED),
				"Not Modified");
		statusCodes.put(new Integer(HttpServletResponse.SC_USE_PROXY),
				"Use Proxy");
		statusCodes.put(new Integer(HttpServletResponse.SC_BAD_REQUEST),
				"Bad Request");
		statusCodes.put(new Integer(HttpServletResponse.SC_UNAUTHORIZED),
				"Unauthorized");
		statusCodes.put(new Integer(HttpServletResponse.SC_PAYMENT_REQUIRED),
				"Payment Required");
		statusCodes.put(new Integer(HttpServletResponse.SC_FORBIDDEN),
				"Forbidden");
		statusCodes.put(new Integer(HttpServletResponse.SC_NOT_FOUND),
				"Not Found");
		statusCodes.put(new Integer(HttpServletResponse.SC_METHOD_NOT_ALLOWED),
				"Method Not Allowed");
		statusCodes.put(new Integer(HttpServletResponse.SC_NOT_ACCEPTABLE),
				"Not Acceptable");
		statusCodes.put(new Integer(
				HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED),
				"Proxy Authentication Required");
		statusCodes.put(new Integer(HttpServletResponse.SC_REQUEST_TIMEOUT),
				"Request Time-out");
		statusCodes.put(new Integer(HttpServletResponse.SC_CONFLICT),
				"Conflict");
		statusCodes.put(new Integer(HttpServletResponse.SC_GONE), "Gone");
		statusCodes.put(new Integer(HttpServletResponse.SC_LENGTH_REQUIRED),
				"Length Required");
		statusCodes.put(
				new Integer(HttpServletResponse.SC_PRECONDITION_FAILED),
				"Precondition Failed");
		statusCodes.put(new Integer(
				HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE),
				"Request Entity Too Large");
		statusCodes.put(
				new Integer(HttpServletResponse.SC_REQUEST_URI_TOO_LONG),
				"Request-URI Too Large");
		statusCodes.put(new Integer(
				HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE),
				"Unsupported Media Type");
		statusCodes.put(new Integer(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
				"Internal Server Error");
		statusCodes.put(new Integer(HttpServletResponse.SC_NOT_IMPLEMENTED),
				"Not Implemented");
		statusCodes.put(new Integer(HttpServletResponse.SC_BAD_GATEWAY),
				"Bad Gateway");
		statusCodes.put(
				new Integer(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
				"Service Unavailable");
		statusCodes.put(new Integer(HttpServletResponse.SC_GATEWAY_TIMEOUT),
				"Gateway Time-out");
		statusCodes.put(new Integer(
				HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED),
				"HTTP Version not supported");
	}
} // ServletEngine
//  ******************** HttpSessionTimeout ********************
/**
 * * Utility class which check HttpSession timeouts *
 */

final class HttpSessionTimeout extends Thread {
	private ServletEngine	servletEngine;
	private Log				log		= new Log("HttpSessionTimeout");
	private boolean			done	= false;
	private long			intervalMillis;
	private int				interval;

	/**
	 * Constructor
	 */
	public HttpSessionTimeout(ThreadGroup parentGroup,
			ServletEngine servletEngine) {
		super(parentGroup, "ServletEngine-sessionTimeout");
		this.servletEngine = servletEngine;
		interval = Integer.parseInt(HttpConfig.getProperty("session.interval",
				"1"));
		intervalMillis = interval * 60 * 1000;
		start();
	}

	public void close() {
		done = true;
		interrupt();
		try {
			join(2000);
		}
		catch (InterruptedException ignore) {
		}
		if (isAlive())
			stop();
	}

	public void run() {
		log.info("Session timeout thread started, checking every " + interval
				+ " minute(s)");
		try {
			while (!done) {
				Date now = new Date(System.currentTimeMillis());
				Enumeration sessionsEnum = servletEngine.getHttpSessions();
				while (sessionsEnum.hasMoreElements()) {
					HttpSessionImpl session = (HttpSessionImpl) sessionsEnum
							.nextElement();
					if (now.getTime() - session.getLastAccessedTime() > session
							.getMaxInactiveInterval() * 1000) {
						log.info("Timeout for sessionid: " + session.getId());
						servletEngine.removeHttpSession(session.getId());
						session.invalidate();
					}
				}
				sleep(intervalMillis);
			}
		}
		catch (InterruptedException ie) {
			log.error("While checking session timeout: " + ie.getMessage());
		}
		catch (ThreadDeath td) {
			throw td;
		}
	}
} // HttpSessionTimeout
