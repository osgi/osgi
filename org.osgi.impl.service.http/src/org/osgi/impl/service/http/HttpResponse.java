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
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

//  ******************** HttpResponse ********************
/**
 * * Implements the HttpServletResponse interface from the servlet API. *
 * Instances of this class are handed to the registered servlets and resources *
 * when they create the HTTP response. E.g a doGet() or doPost() method * in a
 * servlet. * *
 * 
 * @author Gatespace AB (osgiref@gatespace.com) *
 * @version $Revision$
 */
public final class HttpResponse implements HttpServletResponse {
	private HttpServer				httpServer;
	private HttpRequest				request;
	private Log						log;
	private DataOutputStream		dataOut;
	private Hashtable				headers			= new Hashtable();
	private Vector					cookies			= new Vector();
	private int						statusCode		= -1;
	private String					statusMsg;
	private String					contentType;
	private ByteArrayOutputStream	outBuffer		= new ByteArrayOutputStream(
															1024);
	private String					sessionId		= null;
	private static final String		PROTO_VERSION	= "HTTP/1.0";
	private final SimpleDateFormat	responseDateFormat;
	private final GregorianCalendar	gregCal;
	private PrintWriter				pw				= null;
	private ServletOutputStream		sos				= null;
	AliasMatch						aliasMatch		= null;

	HttpResponse(HttpServer httpServer, String transId,
			DataOutputStream dataOut, HttpRequest request) {
		this.httpServer = httpServer;
		this.request = request;
		this.dataOut = dataOut;
		log = new Log(transId + "_HttpResponse");
		setContentType("text/html");
		setStatus(HttpServletResponse.SC_OK);
		// Setup some constant values
		gregCal = new GregorianCalendar();
		responseDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		responseDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	//----------------------------------------------------------------------------
	//    Misc package methods
	//----------------------------------------------------------------------------
	void setBody(InputStream in) throws IOException {
		setBody(in, false);
	}

	void setBody(InputStream in, boolean append) throws IOException {
		int bytesRead = 0;
		byte buffer[] = new byte[512];
		if (!append)
			outBuffer.reset();
		while ((bytesRead = in.read(buffer)) != -1) {
			outBuffer.write(buffer, 0, bytesRead);
		}
		in.close();
	}

	void setSessionId(String id) {
		this.sessionId = id;
	}

	/**
	 * * Creates the response which is sent back to the agent * making the
	 * request.
	 * 
	 * @exception IOException If an I/O error has occurred.
	 */
	void createResponse() throws IOException {
		String requestURI = request.getRequestURI();
		// Redirect if to default page "/index.html" if the request
		// is for "/" and "/index.html" is registered.
		// Otherwise redirect to the HTTP server's own page
		if (requestURI.equals("/")) {
			if (httpServer.getRegistration("/index.html") != null) {
				request.requestURI = requestURI = "/index.html";
			}
			else {
				sendRedirect(httpServer.baseURL + "/osgiref/http/index.html");
				return;
			}
		}
		aliasMatch = httpServer.findMatchingRegistration(requestURI);
		if (aliasMatch == null) {
			log.error("No exported resource for URI: " + requestURI);
			sendFancyError(
					HttpServletResponse.SC_NOT_FOUND,
					"The requested URI does not exist",
					"The requested URI does not exist: "
							+ "<code>"
							+ requestURI
							+ "</code><p>"
							+ "This means that there is no bundle bundle currently "
							+ " running on the platform which have registered this URI. "
							+ "Please make sure all bundles are installed and are running correctly");
			return;
		}
		// Check if we're authorized to do this by calling the security handler
		try {
			// Setup the request to point to our reg before calling
			request.aliasReg = aliasMatch.aliasReg;
			Boolean access;
			try {
				access = (Boolean) AccessController.doPrivileged(
						new PrivilegedExceptionAction() {
							public Object run() throws Exception {
								return new Boolean(aliasMatch.aliasReg
										.getHttpContext().handleSecurity(
												request, HttpResponse.this));
							}
						}, aliasMatch.aliasReg.acc);
			}
			catch (PrivilegedActionException pae) {
				Exception e = pae.getException();
				throw e;
			}
			if (!access.booleanValue()) {
				log.info("Access denied by HttpContext");
				if (statusCode == -1 || statusCode == 200) {
					sendFancyError(HttpServletResponse.SC_FORBIDDEN,
							"Sorry, access denied to URL",
							"The bundle which registered this URL denies access to this URL");
				}
				return;
			}
		}
		catch (Exception secE) {
			log.error("HttpContext.handleSecurity() throws exception", secE);
			sendFancyError(
					HttpServletResponse.SC_FORBIDDEN,
					"Access denied to URL due to bundle failure",
					"The bundle which registered this URL failed to deliver a security response. "
							+ "Denying access for everybody until problem is solved.",
					true);
			return;
		}
		// Let the registration handle the request
		aliasMatch.aliasReg.handleRequest(request, this);
	}

	/**
	 * * Send the response to the client. *
	 * 
	 * @param requestMethod the http request method. *
	 * @exception IOException If an I/O error has occurred.
	 */
	void sendResponse(String requestMethod) throws IOException {
		log.debug("Sending response: " + PROTO_VERSION + " " + statusCode + " "
				+ statusMsg);
		log.debug("  Content-Type: " + contentType);
		log.debug("  Content-Length: " + outBuffer.size());
		dataOut.writeBytes(PROTO_VERSION + " " + statusCode + " " + statusMsg
				+ "\r\n");
		dataOut.writeBytes("Date: "
				+ responseDateFormat.format(gregCal.getTime()) + "\r\n");
		dataOut.writeBytes("MIME-Version: 1.0\r\n");
		dataOut
				.writeBytes("Server: OSGi reference implementation - HTTP Server (Java)\r\n");
		dataOut.writeBytes("Connection: close\r\n"); // Not supporting
													 // Keep-Alive
		dataOut.writeBytes("Content-type: " + contentType + "\r\n");
		// Other headers
		String key;
		for (Enumeration e = headers.keys(); e.hasMoreElements();) {
			key = (String) e.nextElement();
			dataOut.writeBytes(key + ": " + headers.get(key) + "\r\n");
		}
		// add osgirefsession cookie if sessionId is set
		if (sessionId != null) {
			sendCookie(new Cookie(HttpConfig.OSGIREF_SESSION, sessionId));
		}
		// Create the rest of the Set-Cookie headers
		for (Enumeration e = cookies.elements(); e.hasMoreElements();) {
			sendCookie((Cookie) e.nextElement());
		}
		if (requestMethod.equalsIgnoreCase("head")) // Clear message body for
													// HEAD requests
			outBuffer.reset();
		dataOut.writeBytes("Content-Length: " + outBuffer.size() + "\r\n\r\n");
		outBuffer.writeTo(dataOut);
		dataOut.flush();
		closeAllStreams();
	}

	private void closeAllStreams() throws IOException {
		if (pw != null)
			pw.close();
		if (sos != null)
			sos.close();
		outBuffer.close();
	}

	private void sendCookie(Cookie cookie) throws IOException {
		String attrValue;
		int maxAge;
		dataOut.writeBytes("Set-Cookie: ");
		dataOut.writeBytes(cookie.getName() + "=" + cookie.getValue());
		if ((attrValue = cookie.getComment()) != null)
			dataOut.writeBytes(";Comment=" + attrValue);
		if ((attrValue = cookie.getDomain()) != null)
			dataOut.writeBytes(";Domain=" + attrValue);
		if ((maxAge = cookie.getMaxAge()) != -1) {
			if (maxAge > 0) {
				SimpleDateFormat s = new SimpleDateFormat(
						"EEE, dd-MMM-yyyy HH:mm:ss z");
				GregorianCalendar cal = new GregorianCalendar();
				cal.add(Calendar.SECOND, maxAge);
				dataOut.writeBytes(";Expires=" + s.format(cal.getTime()));
			}
			dataOut.writeBytes(";Max-Age=" + maxAge);
		}
		if ((attrValue = cookie.getPath()) != null)
			dataOut.writeBytes(";Path=" + attrValue);
		if (cookie.getSecure())
			dataOut.writeBytes(";Secure");
		dataOut.writeBytes(";Version=" + cookie.getVersion());
		dataOut.writeBytes("\r\n");
	}

	void sendError(Throwable th) {
		CharArrayWriter caw = new CharArrayWriter();
		PrintWriter pw = new PrintWriter(caw);
		th.printStackTrace(pw);
		sendFancyError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Internal server error stack trace", caw.toString(), true);
		String trace = caw.toString();
	}

	void sendError(String error) {
		sendFancyError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Unexpected internal server error", error);
	}

	void sendFancyError(int code, String hdr, String body) {
		sendFancyError(code, hdr, body, false);
	}

	void sendFancyError(int code, String hdr, String body, boolean see) {
		setStatus(code);
		outBuffer.reset();
		PrintWriter out = new PrintWriter(outBuffer);
		out
				.println("<html><head><title>Gatespace OSG platform HTTP server</title></head>");
		out.println("<body bgcolor=\"#ffffff\">");
		out.println("<h1>" + statusMsg + "</h1>");
		if (hdr != null)
			out.println("<b>" + hdr + "</b><p>");
		out.println(body);
		if (see)
			out
					.println("<p><em>See also the HTTP server's log for more information</em><p>");
		if (HttpConfig.useErrorTrailer)
			out.println("<p><hr>Gatespace OSG platform HTTP server, version "
					+ httpServer.getManifestValue("Bundle-Version", "X"));
		out.println("</body></html>");
		out.close();
	}

	boolean isAnyWriterCalled() {
		return (pw != null) || (sos != null);
	}

	//----------------------------------------------------------------------------
	//    IMPLEMENTS - ServletResponse
	//----------------------------------------------------------------------------
	// NO DOCUMENTATION HERE - PLEASE SEE API DOCUMENTATION
	public void setContentLength(int len) {
		// The len set by servlet is ignored
		// len is set to the actual length of the internal buffer
		// storing the response - Is this correct?
	}

	public void setContentType(String type) {
		contentType = type;
	}

	public synchronized ServletOutputStream getOutputStream()
			throws IOException {
		if (pw != null)
			throw new IllegalStateException("getWriter() already called");
		return (sos == null) ? sos = new ServletOutputStreamImpl(outBuffer)
				: sos;
	}

	public synchronized PrintWriter getWriter() throws IOException {
		if (sos != null)
			throw new IllegalStateException("getOutputStream() already called");
		return (pw == null) ? pw = new PrintWriter(outBuffer, true) : pw;
	}

	public String getCharacterEncoding() {
		return "ISO-8859-1";
	}

	//----------------------------------------------------------------------------
	//    IMPLEMENTS - HttpServletResponse
	//----------------------------------------------------------------------------
	public void addCookie(Cookie cookie) {
		cookies.addElement(cookie);
	}

	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	/**
	 * *
	 * 
	 * @deprecated To be removed from servlet API
	 */
	public void setStatus(int sc, String msg) {
		statusCode = sc;
		statusMsg = sc + " " + msg;
	}

	public void setStatus(int sc) {
		setStatus(sc, httpServer.servletEngine.getStatusMessage(sc));
	}

	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	public void setIntHeader(String name, int value) {
		headers.put(name, String.valueOf(value));
	}

	public void setDateHeader(String name, long date) {
		SimpleDateFormat s = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss zzz");
		headers.put(name, s.format(new Date(date)));
	}

	public void sendError(int sc, String msg) throws IOException {
		sendFancyError(sc, null, msg);
	}

	public void sendError(int sc) throws IOException {
		sendFancyError(sc, null, "");
	}

	public void sendRedirect(String location) throws IOException {
		URL redirectURL;
		try {
			redirectURL = new URL(location);
		}
		catch (MalformedURLException e) {
			throw new IOException(e.getMessage());
		}
		setHeader("Location", location);
		setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}

	public String encodeURL(String url) {
		if (request.isRequestedSessionIdValid()
				&& request.isRequestedSessionIdFromURL()) {
			if (url.indexOf(HttpConfig.OSGIREF_SESSION) == -1) {
				url += (url.indexOf('?') == -1) ? "?" : "&";
				url += HttpConfig.OSGIREF_SESSION + "="
						+ request.getRequestedSessionId();
			}
		}
		return url;
	}

	/**
	 * *
	 * 
	 * @deprecated To be removed from servlet API
	 */
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	public String encodeRedirectURL(String url) {
		return encodeUrl(url);
	}

	/**
	 * *
	 * 
	 * @deprecated To be removed from servlet API
	 */
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}
} // HttpResponse
//  ******************** ServletOutputStreamImpl ********************
/**
 * * Simple implementation of the abstract ServletOutputStream class
 */

final class ServletOutputStreamImpl extends ServletOutputStream {
	private OutputStream	out;

	public ServletOutputStreamImpl(OutputStream out) {
		this.out = out;
	}

	public void write(int b) throws IOException {
		out.write(b);
	}
} // ServletOutputStreamImpl
