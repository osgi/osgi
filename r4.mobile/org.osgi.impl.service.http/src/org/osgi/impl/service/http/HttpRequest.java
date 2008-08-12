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
import java.text.*;
import java.util.*;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import org.osgi.service.http.HttpContext;

//  ******************** HttpRrequest ********************
/**
 * * Implements the HttpServletRequest interface from the servlet API. *
 * Instances of this class are handed to the registered servlets and resources *
 * when they create the HTTP response. E.g a doGet() or doPost() method * in a
 * servlet. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$ *
 * @see HttpResponse *
 * @see HttpTransaction
 */
public final class HttpRequest implements HttpServletRequest {
	private HttpServer				httpServer;
	private Log						log;
	private String					requestMethod					= null;
	private String					requestProtocol					= null;
	private String					queryString						= null;
	private String					serverName						= null;
	private String					remoteUser						= null;
	private String					remotePassword					= null;
	private String					contentType						= null;
	private String					charset							= null;
	private int						contentLength					= -1;
	private String					sessionId						= null;
	private boolean					isRequestedSessionIdFromCookie	= false;
	private boolean					isRequestedSessionIdFromUrl		= false;
	private Hashtable				headers							= new Hashtable();
	private Hashtable				parameters						= new Hashtable();
	private Hashtable				cookies							= new Hashtable();
	private Hashtable				attributes						= new Hashtable();
	private ByteArrayOutputStream	outBuffer						= new ByteArrayOutputStream();
	private HttpTransaction			transaction;
	private HttpResponse			response;														// The
																									 // respone
																									 // object
																									 // connected
																									 // to
																									 // this
																									 // request
	private BufferedInputStream		inBuf;
	private BufferedReader			reader							= null;
	private ServletInputStream		sis								= null;
	private ServletEngine			servletEngine;
	// Fields which may be/are subject to changes later on
	AliasRegistration				aliasReg;														// The
																									  // reg
																									  // this
																									  // request
																									  // resolves
																									  // to,
																									  // set
																									  // in
																									  // HttpResponse
	String							requestURI						= null;						// The
																									   // request
																									   // URI,
																									   // can
																									   // be
																									   // reset
																									   // when
																									   // redirecting

	HttpRequest(HttpServer httpServer, HttpTransaction transaction,
			BufferedInputStream inBuf) {
		this.httpServer = httpServer;
		this.transaction = transaction;
		this.inBuf = inBuf;
		log = new Log(transaction.transId + "_HttpRequest");
	}

	/**
	 * * Parses the request, which is read from the agent's input stream. *
	 * 
	 * @exception IOException If an I/O error has occurred. *
	 * @return Status code
	 */
	// NB: must be rewritten
	int parseRequest() throws IOException {
		String line;
		StringTokenizer tok;
		int startPos, endPos;
		int ix;
		// Start parsing the request, first read the request line
		if ((line = getLine()) == null || line.length() == 0)
			throw new InterruptedIOException(
					"Failed to read from client's input stream");
		log.debug("Read Request-Line: " + line);
		if ((endPos = line.indexOf(' ')) == -1) {
			log.error("Bad request: " + line);
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		requestMethod = line.substring(0, endPos);
		startPos = endPos + 1;
		if ((endPos = line.indexOf(' ', startPos)) == -1) {
			log.error("Bad request: " + line);
			return HttpServletResponse.SC_BAD_REQUEST;
		}
		requestURI = line.substring(startPos, endPos);
		requestProtocol = line.substring(endPos + 1);
		if (requestProtocol.length() == 0)
			requestProtocol = "HTTP/0.9"; // Assume HTTP/0.9 if protocol is
										  // missing
		// Parse and store query parameters if they exist
		startPos = requestURI.indexOf('?');
		if (startPos != -1) {
			queryString = requestURI.substring(startPos + 1);
			parseQueryString();
			requestURI = requestURI.substring(0, startPos);
		}
		// Parse the rest of the message headers
		String headerStr, headerValue, previousValue;
		int delimiter;
		while ((line = getLine()) != null) {
			log.debug("Read request-header: " + line);
			if (line.length() == 0)
				break;
			delimiter = line.indexOf(':');
			if (delimiter != -1) {
				headerStr = line.substring(0, delimiter).trim().toLowerCase();
				headerValue = line.substring(delimiter + 1).trim();
				previousValue = (String) headers.put(headerStr, headerValue);
				if (previousValue != null)
					headers.put(headerStr, previousValue + "," + headerValue);
			}
		}
		// Extract interesting header values
		headerValue = getHeader("host");
		if (headerValue != null) {
			delimiter = headerValue.indexOf(':');
			if (delimiter != -1)
				serverName = headerValue.substring(0, delimiter);
			else
				serverName = headerValue;
		}
		if ((contentType = getHeader("content-type")) != null) {
			if ((ix = contentType.indexOf("charset")) != -1
					&& (ix = contentType.indexOf('=', ix + "charset".length())) != -1) {
				charset = contentType.substring(ix + 1).trim();
			}
		}
		headerValue = getHeader("content-length");
		if (headerValue != null) {
			contentLength = Integer.parseInt(headerValue);
		}
		// Get all cookies
		headerValue = getHeader("cookie");
		if (headerValue != null) {
			Cookie cookie = null;
			int version = 0;
			String tokStr, prevTokStr = "";
			boolean isValue = false;
			tok = new StringTokenizer(headerValue, "=;,", true);
			while (tok.hasMoreTokens()) {
				tokStr = tok.nextToken().trim();
				if (tokStr.equals("="))
					isValue = true;
				else
					if (";,".indexOf(tokStr) != -1)
						isValue = false;
					else {
						if (prevTokStr.equals("$Version") && isValue)
							version = Integer.parseInt(tokStr);
						else
							if (prevTokStr.equals("$Path") && isValue)
								cookie.setPath(tokStr);
							else
								if (prevTokStr.equals("$Domain") && isValue)
									cookie.setDomain(tokStr);
								else
									if (isValue) {
										cookie = new Cookie(prevTokStr, tokStr);
										cookie.setVersion(version);
										cookies.put(cookie.getName(), cookie);
									}
									else
										prevTokStr = tokStr;
					}
			}
			// Move osgirefsession cookie value to sessionId if it exists.
			if ((cookie = (Cookie) cookies.get(HttpConfig.OSGIREF_SESSION)) != null) {
				sessionId = cookie.getValue();
				log.debug("We are using session: " + sessionId);
				isRequestedSessionIdFromCookie = true;
			}
		}
		// If there was no session set in a cookie look for it as a parameter
		if (!isRequestedSessionIdFromCookie) {
			sessionId = getParameter(HttpConfig.OSGIREF_SESSION);
			isRequestedSessionIdFromUrl = sessionId != null;
		}
		log.debug(requestMethod + ", content type: " + contentType);
		log.debug(requestMethod + ", content length: " + contentLength);
		if (requestMethod.equals("POST") && contentLength < 0) {
			log.error("No Content-Length specified in a POST request");
			return HttpServletResponse.SC_LENGTH_REQUIRED;
		}
		if (contentLength > -1) {
			byte buf[] = new byte[contentLength];
			int off = 0;
			while (off < contentLength) {
				int n = inBuf.read(buf, off, contentLength - off);
				if (n == -1) {
					log.error("Actual content length (" + off + ") less than "
							+ "the specified ContentLength (" + contentLength
							+ ")");
					return HttpServletResponse.SC_BAD_REQUEST;
				}
				off += n;
			}
			outBuffer.write(buf, 0, contentLength);
			if (requestMethod.equals("POST")) {
				// If it's a post from a form, parse the query in the contents
				if (contentType.equals("application/x-www-form-urlencoded")) {
					queryString = outBuffer.toString();
					parseQueryString();
				}
				log.debug("POST, done with reading content");
			}
		}
		return HttpServletResponse.SC_OK;
	}

	private void parseQueryString() {
		if (queryString != null) {
			try {
				parameters = HttpUtils.parseQueryString(queryString);
			}
			catch (IllegalArgumentException e) {
				log.error("Illegal queryString: " + queryString);
			}
		}
	}

	private String getLine() throws IOException {
		// NB: Very inefficient... - must be rewritten
		int ch;
		ch = inBuf.read();
		if (ch == -1)
			return null;
		StringBuffer buf = new StringBuffer();
		while (ch != -1 && ch != '\r' && ch != '\n') {
			buf.append((char) ch);
			ch = inBuf.read();
		}
		if (ch == '\r')
			inBuf.read(); // Consume LF
		return buf.toString();
	}

	void setResponse(HttpResponse response) {
		this.response = response;
	}

	//----------------------------------------------------------------------------
	//	IMPLEMENTS - ServletRequest
	//----------------------------------------------------------------------------
	// NO DOCUMENTATION HERE - PLEASE SEE API DOCUMENTATION
	public int getContentLength() {
		return contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public String getCharacterEncoding() {
		return charset;
	}

	public String getProtocol() {
		return requestProtocol;
	}

	public String getScheme() {
		return "http";
	}

	public String getServerName() {
		return serverName;
	}

	public int getServerPort() {
		return httpServer.getServerPort();
	}

	public String getRemoteAddr() {
		return transaction.getHostAddress();
	}

	public String getRemoteHost() {
		return transaction.getHostName();
	}

	/**
	 * *
	 * 
	 * @deprecated To be removed from servlet API
	 */
	public String getRealPath(String path) {
		return path;
	}

	public String getParameter(String name) {
		String[] values = (String[]) parameters.get(name);
		if (values == null)
			return null;
		else
			return values[0];
	}

	public String[] getParameterValues(String name) {
		return (String[]) parameters.get(name);
	}

	public Enumeration getParameterNames() {
		return parameters.keys();
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public Enumeration getAttributeNames() {
		return attributes.keys();
	}

	public void setAttribute(String key, Object o) {
		synchronized (attributes) {
			if (attributes.containsKey(key))
				throw new IllegalStateException("Key already defined: " + key);
			attributes.put(key, o);
		}
	}

	public synchronized ServletInputStream getInputStream() throws IOException {
		if (reader != null)
			throw new IllegalStateException("getReader() already called");
		return (sis == null) ? sis = new ServletInputStreamImpl(
				new ByteArrayInputStream(outBuffer.toByteArray())) : sis;
	}

	public synchronized BufferedReader getReader() throws IOException {
		if (sis != null)
			throw new IllegalStateException("getInputStream() already called");
		return (reader == null) ? reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(outBuffer
						.toByteArray()))) : reader;
	}

	//----------------------------------------------------------------------------
	//	IMPLEMENTS - HttpServletRequest
	//----------------------------------------------------------------------------
	public Cookie[] getCookies() {
		Cookie tmp[] = new Cookie[cookies.size()];
		int i = 0;
		for (Enumeration e = cookies.elements(); e.hasMoreElements(); i++)
			tmp[i] = (Cookie) e.nextElement();
		return tmp;
	}

	public String getMethod() {
		return requestMethod;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public String getServletPath() {
		return aliasReg.getAlias();
	}

	public String getPathInfo() {
		String servletPath = aliasReg.getAlias();
		if (requestURI != null && requestURI.length() > servletPath.length()
				&& requestURI.startsWith(servletPath))
			return requestURI.substring(servletPath.length());
		else
			return null;
	}

	public String getPathTranslated() {
		return getPathInfo();
	}

	public String getQueryString() {
		return queryString;
	}

	public String getRemoteUser() {
		Object o = getAttribute(HttpContext.REMOTE_USER);
		if (o instanceof String)
			return (String) o;
		else
			return null;
	}

	public String getAuthType() {
		Object o = getAttribute(HttpContext.AUTHENTICATION_TYPE);
		if (o instanceof String)
			return (String) o;
		else
			return null;
	}

	public String getHeader(String name) {
		return (String) headers.get(name.toLowerCase());
	}

	public int getIntHeader(String name) throws NumberFormatException {
		String value = (String) headers.get(name.toLowerCase());
		if (value != null)
			return Integer.parseInt(value);
		return -1;
	}

	public long getDateHeader(String name) throws IllegalArgumentException {
		String value = (String) headers.get(name.toLowerCase());
		SimpleDateFormat fmt;
		Date date;
		if (value == null) {
			return -1;
		}
		fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",
				Locale.ENGLISH);
		try {
			date = fmt.parse(value);
			return date.getTime();
		}
		catch (ParseException e) {
		}
		fmt = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz",
				Locale.ENGLISH);
		try {
			date = fmt.parse(value);
			return date.getTime();
		}
		catch (ParseException e) {
		}
		fmt = new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.ENGLISH);
		try {
			date = fmt.parse(value);
			return date.getTime();
		}
		catch (ParseException e) {
			throw new IllegalArgumentException(value);
		}
	}

	public Enumeration getHeaderNames() {
		return headers.keys();
	}

	public HttpSession getSession(boolean create) {
		log.debug("Looking for session: " + sessionId);
		HttpSession session = httpServer.servletEngine
				.getHttpSession(sessionId);
		if (session == null && create) {
			session = httpServer.servletEngine.createHttpSession(aliasReg);
			response.setSessionId(sessionId = session.getId());
		}
		return session;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public String getRequestedSessionId() {
		return sessionId;
	}

	public boolean isRequestedSessionIdValid() {
		HttpSessionImpl session = httpServer.servletEngine
				.getHttpSession(sessionId);
		if (session != null)
			return !session.isInvalidated();
		else
			return false;
	}

	public boolean isRequestedSessionIdFromCookie() {
		return isRequestedSessionIdFromCookie;
	}

	public boolean isRequestedSessionIdFromURL() {
		return isRequestedSessionIdFromUrl;
	}

	/**
	 * *
	 * 
	 * @deprecated Use isRequestedSessionIdFromURL() instead
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}
} // HttpRequest
//  ******************** ServletInputStreamImpl ********************
/**
 * * Simple implementation of the abstract ServletInputStream class
 */

final class ServletInputStreamImpl extends ServletInputStream {
	private InputStream	in;

	public ServletInputStreamImpl(InputStream in) {
		this.in = in;
	}

	public int read() throws IOException {
		return in.read();
	}
} // ServletInputStreamImpl
