/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.director;

import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.osgi.framework.*;
import org.osgi.service.http.*;
import org.osgi.util.tracker.ServiceTracker;
import org.xml.sax.SAXException;

/**
 * Track Http Service objects and register our servlet /test/director and
 * resources at /test/director/www.
 */
class HttpTracker extends ServiceTracker implements HttpContext {
	Handler	handler;

	/**
	 * Create a HttpTracker.
	 */
	HttpTracker(Handler handler, BundleContext context) {
		super(context, HttpService.class.getName(), null);
		this.handler = handler;
	}

	/**
	 * New Http Service discovered, add our resources + servlet.
	 */
	public Object addingService(ServiceReference reference) {
		try {
			HttpService service = (HttpService) context.getService(reference);
			TestServlet ts = new TestServlet(handler);
			service.registerServlet(Handler.ALIAS, ts, new Hashtable(), this);
			service.registerResources(Handler.ALIAS + "/www", "www", this);
			return service;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Default HttpContext methods. We are obviously no paranoia.
	 */
	public boolean handleSecurity(HttpServletRequest rq, HttpServletResponse rsp) {
		return true;
	}

	public URL getResource(String rsrc) {
		URL url = getClass().getResource(rsrc);
		if (url != null)
			return url;
		rsrc = "/" + rsrc.substring(4);
		return getClass().getResource(rsrc);
	}

	public String getMimeType(String s) {
		return null;
	}
}
/**
 * The servlet is used to access the latest test results.
 * 
 * Test results are stored as XML files but can be expanded using the included
 * XSLT processor. The following parameters are supported in the URL:
 * 
 * <pre>
 * 
 *  
 *    source  	name of source test result file (no dir), 
 *    			e.g. 1009163849.xml
 *    style   	name of the style sheet, these are 
 *    			stored in the www directory. e.g. testresult.html
 *    clear   	Clear the style sheet cache
 * 
 * 
 */

class TestServlet extends HttpServlet {
	Hashtable	cache	= new Hashtable();
	Handler		handler;

	TestServlet(Handler handler) {
		this.handler = handler;
	}

	/**
	 * Check for a test result request.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		String source = req.getParameter("source");
		String style = req.getParameter("style");
		String clear = req.getParameter("clear");
		if (clear != null && clear.equals("yes")) {
			clearCache();
		}
		try {
			if (source == null) {
				PrintWriter out = res.getWriter();
				out
						.println("<html><head><title>Test Results</title></head><body>");
				out.println("<h1>Test Results</h1>");
				File result[] = handler.getResults();
				for (int i = 0; result != null && i < result.length; i++) {
					out.println("<a name='" + result[i].getName() + "'>"
							+ "<a href='" + Handler.ALIAS + "?source="
							+ result[i].getName() + "'>" + result[i].getName()
							+ "</a> <a href='" + Handler.ALIAS + "?source="
							+ result[i].getName()
							+ "&style=testresult.xsl'>FORMATTED</a><br>");
				}
				out.println("</body></html>");
				out.flush();
			}
			else
				if (style == null)
					copyraw(source, req, res);
				else
					apply(style, source, req, res);
		}
		catch (Exception err) {
			res.getOutputStream().println(
					"Error applying stylesheet: " + err.getMessage());
		}
	}

	public String getServletInfo() {
		return "OSGi Director Test Result Servlet";
	}

	/**
	 * Copy the source file to the output.
	 */
	void copyraw(String source, HttpServletRequest rq, HttpServletResponse rsp)
			throws Exception {
		rsp.setContentType("text/plain;characterset=iso-8859-1");
		OutputStream out = rsp.getOutputStream();
		InputStream in = new FileInputStream(handler.getResult(source));
		byte buffer[] = new byte[1024];
		int size = in.read(buffer);
		while (size > 0) {
			out.write(buffer, 0, size);
			size = in.read(buffer);
		}
		in.close();
	}

	/**
	 * Apply stylesheet to source document
	 */
	void apply(String style, String source, HttpServletRequest req,
			HttpServletResponse res) throws SAXException, java.io.IOException {
		ServletOutputStream out = res.getOutputStream();
		URL styleURL = null;
		styleURL = getClass().getResource("www/" + style);
		System.out.println("URL " + style + " " + styleURL);
		if (source == null) {
			out.println("No source parameter supplied");
			return;
		}
		try {
			Templates pss = tryCache(styleURL);
			Transformer transformer = pss.newTransformer();
			res.setContentType("text/html");
			transformer.transform(new StreamSource(handler.getResult(source)),
					new StreamResult(out));
		}
		catch (Exception err) {
			out.println(err.getMessage());
			err.printStackTrace();
		}
	}

	/**
	 * Maintain prepared stylesheets in memory for reuse
	 */
	synchronized Templates tryCache(URL url) throws Exception {
		Templates x = (Templates) cache.get(url);
		if (x == null) {
			TransformerFactory factory = TransformerFactory.newInstance();
			x = factory.newTemplates(new StreamSource(url.openStream()));
			cache.put(url, x);
		}
		return x;
	}

	/**
	 * Clear the cache. Useful if stylesheets have been modified, or simply if
	 * space is running low. We let the garbage collector do the work.
	 */
	synchronized void clearCache() {
		cache = new Hashtable();
	}
}
