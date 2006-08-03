/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Telia Research AB. 2000.
 * This source code is owned by Telia Research AB,
 * and is being distributed to OSGi MEMBERS as
 * MEMBER LICENSED MATERIALS under the terms of section 3.2 of
 * the OSGi MEMBER AGREEMENT.
 * Updated June 2001
 */
package org.osgi.test.cases.http.tbc;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.osgi.framework.*;
import org.osgi.service.http.*;
import org.osgi.test.cases.util.*;
import org.osgi.test.service.*;

public class HttpTestBundle1 extends DefaultTestBundleControl implements
		BundleActivator, Runnable {
	// Http service
	HttpService			_http;
	ServiceReference	_httpSR;
	int					_httpPort;
	TestCaseLink		_link;
	ServiceReference	_ref;
	Dictionary			_properties	= new Hashtable();
	boolean				_continue;
	static String[]		methods		= new String[] {
									// REGISTRATION BEHAVIOUR
									"SimpleServletRegistration", // "TC1"
									"SimpleServletUnregistration", // "TC2"
									"OverlappingRegistrationPaths", // "TC3"
									"UnregistringOverlapping", // "TC4"
									"BundleStopping", // "TC5"
									"LongRegistrationName", // "TC6"
									"NullServlet", // "TC7"
									"CaseSensitivity", // "TC8"
									"GenericServletRegistration", // "TC9"
									"RegisterResources", // "TC10"
									"MultipleRegistrations", // "TC11"
									"MultipleUnregistrations", // "TC12"
									"EndingSlash", // "TC13"
									"RootAlias", // "TC14" new
									"ServletRegDefaultContext", // "TC15" new
									"ResourceRegDefaultContext", // "TC16"
																	// new
									"RegisterMultipleAliases", // "TC17" new
									// RUNTIME BEHAVIOUR
									"Empty_doGet", // "TC50"
									"Security", // "TC51"
									// "doGetExceptions", // "TC52" Disabled due
									// to undefined behavior
									"LargeOutput", // "TC53"
									// "WebServerVersion", // "TC54" removed
									// "HangRequest", // "TC55" removed because
									// not clear what it tests
									"LargeURL", // "TC56"
									"OddURL", // "TC57"
									"MultipleNamesURL", // "TC58a" updated
									"MultipleNamesURL2", // "TC58b" updated
									"MIMEtypes", // "TC59" updated
									"ResourceName", // "TC60"
									"Authentication", // "TC61" new
									"ServletContextSharing", // "TC62" new
									// BUGBUG need to complete this testcase
									// "Permissions" // "TC63" new
									};

	protected String[] getMethods() {
		return methods;
	}

	public boolean checkPrerequisites() {
		_httpSR = getContext().getServiceReference(HttpService.class.getName());
		if (_httpSR == null)
			return false;
		_http = (HttpService) getContext().getService(_httpSR);
		if (_http == null)
			return false;
		_httpPort = guessHttpPort();
		return true;
	}

	// REGISTRATION BEHAVIOUR
	public void SimpleServletRegistration() throws Exception {
		log("TC1 Registration of simple servlet");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc1servlet?TestCase=1";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc1servlet
		_http.registerServlet("/tc1servlet", servlet, null, httpContext);
		log("Simple servlet registered");
		// get and use resource /tc1servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc1servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc1servlet");
		log("Simple Servlet unregistered");
	}

	public void SimpleServletUnregistration() throws Exception {
		log("TC2 Unregistration of simple servlet");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc2servlet?TestCase=2";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc2servlet
		_http.registerServlet("/tc2servlet", servlet, null, httpContext);
		log("Simple servlet registered");
		// get and use resoruce /tc2servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc2servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc2servlet");
		log("Simple Servlet unregistered");
		// try to get and use the resoruce /tc2servlet after unregistering
		log("Check if Simple servlet available after having been unregistered");
		try { 
			HttpURLConnection conn = (HttpURLConnection) source.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new IOException("404");
			}
			log("Simple servlet improperly available");
		}
		catch (IOException e) {
			log("Simple servlet not available");
			in.close();
		}
	}

	public void OverlappingRegistrationPaths() throws Exception {
		log("TC3 Registration of two overlapping servlets");
		HttpTestServlet1 servlet_aa = new HttpTestServlet1();
		HttpTestServlet2 servlet_aabb = new HttpTestServlet2();
		final String urlstr_aa = "http://localhost:" + _httpPort
				+ "/aa?TestCase=3";
		final String urlstr_aabb = "http://localhost:" + _httpPort
				+ "/aa/bb?TestCase=3";
		URL source_aa = null;
		URL source_aabb = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register first servlet with path /aa
		_http.registerServlet("/aa", servlet_aa, null, httpContext);
		log("Servlet resource /aa registered");
		// register second servlet with overlapping path /aa/bb
		_http.registerServlet("/aa/bb", servlet_aabb, null, httpContext);
		log("Servlet resource /aa/bb registered");
		// get and use second resoruce /aa/bb
		source_aabb = new URL(urlstr_aabb);
		in = new BufferedReader(new InputStreamReader(source_aabb.openStream()));
		// read lines in resource /aa/bb
		log("Receiving from servlet resource /aa/bb");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// get and use first resource /aa
		source_aa = new URL(urlstr_aa);
		in = new BufferedReader(new InputStreamReader(source_aa.openStream()));
		// read lines in resource /aa
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister servlets with overlapping names
		_http.unregister("/aa");
		log("Servlet resource /aa unregistered");
		_http.unregister("/aa/bb");
		log("Servlet resource /aa/bb unregistered");
	}

	public void UnregistringOverlapping() throws Exception {
		log("TC4 Unregistration of overlapping servlet");
		HttpTestServlet1 servlet_aa = new HttpTestServlet1();
		HttpTestServlet2 servlet_aabb = new HttpTestServlet2();
		final String urlstr_aa = "http://localhost:" + _httpPort
				+ "/aa?TestCase=4";
		final String urlstr_aabb = "http://localhost:" + _httpPort
				+ "/aa/bb?TestCase=4";
		URL source_aa = null;
		URL source_aabb = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register first servlet with path /aa
		_http.registerServlet("/aa", servlet_aa, null, httpContext);
		log("Servlet resource /aa registered");
		// register second servlet with overlapping path /aa/bb
		_http.registerServlet("/aa/bb", servlet_aabb, null, httpContext);
		log("Servlet resource /aa/bb registered");
		// get and use second resource /aa/bb
		source_aabb = new URL(urlstr_aabb);
		in = new BufferedReader(new InputStreamReader(source_aabb.openStream()));
		// read lines in resource /aa/bb
		log("Receiving from servlet resource /aa/bb");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// get and use first resource /aa
		source_aa = new URL(urlstr_aa);
		in = new BufferedReader(new InputStreamReader(source_aa.openStream()));
		// read lines in resource /aa
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister servlet with path /aa/bb
		_http.unregister("/aa/bb");
		log("Servlet resource /aa/bb unregistered");
		// try to get and use the resoruce /aa/bb after unregistering
		in = new BufferedReader(new InputStreamReader(source_aabb.openStream()));
		// read lines in resource /aa/bb
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa/bb");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// get and use first resoruce /aa
		in = new BufferedReader(new InputStreamReader(source_aa.openStream()));
		// read lines in resource /aa
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister servlets with overlapping names
		_http.unregister("/aa");
		log("Servlet resource /aa unregistered");
	}

	public void BundleStopping() throws Exception {
		log("TC5 Stopping of source bundle");
		final String urlstr = "http://localhost:" + _httpPort + "/tc5servlet";
		log("Check if tc5servlet available before bundle installed (no)");
		check(urlstr);
		Bundle tb2 = getContext().installBundle("BundleStoppingHelperBundle",
				getClass().getResourceAsStream("/tb1.jar"));
		log("Check if tc5servlet available after bundle installed (no)");
		check(urlstr);
		tb2.start();
		log("Check if tc5servlet available after bundle started (yes)");
		check(urlstr);
		tb2.stop();
		log("Check if tc5servlet available after bundle stopped (no)");
		check(urlstr);
		tb2.start();
		log("Check if tc5servlet available after bundle started again (yes)");
		check(urlstr);
		tb2.uninstall();
		log("Check if tc5servlet available after bundle uninstalled without stop (no)");
		check(urlstr);
	}

	void check(String urlstr) {
		URL source = null;
		String inputLine = new String();
		BufferedReader in = null;
		try {
			source = new URL(urlstr);
		}
		catch (MalformedURLException e) {
			log("Malformed URL " + urlstr);
			return;
		}
		try {
			// get and use resoruce /tc5servlet
			HttpURLConnection conn = (HttpURLConnection) source.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new IOException("404");
			}
			log("--- start file: " + source.getPath());
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			// read lines in resource /tc5servlet
			while ((inputLine = in.readLine()) != null) {
				log(inputLine);
			}
			in.close();
			log("--- end file: " + source.getPath());
		}
		catch (IOException e) {
			log(source.getPath() + " not available");
		}
	}

	public void LongRegistrationName() throws Exception {
		log("TC6 Registering servlet with long name");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:"
				+ _httpPort
				+ "/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz?TestCase=6";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with long name
		_http
				.registerServlet(
						"/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz",
						servlet, null, httpContext);
		log("Servlet resource with long name registered");
		// get and use resoruce /tc6servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister servlet with long name
		_http
				.unregister("/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
		log("Servlet resource with long name unregistered");
	}

	public void NullServlet() throws Exception {
		log("TC7 Attempt to register null servlet");
		HttpTestServlet1 servlet = null;
		// register servlet with path /tc7servlet
		try {
			_http.registerServlet("/tc7servlet", servlet, null, httpContext);
			log("Null servlet registered");
		}
		catch (Exception e1) {
			log("NullPointerException for null servlet");
		}
		// See if it did not remain registered
		try {
			_http.registerServlet("/tc7servlet", servlet, null, httpContext);
		}
		catch (NullPointerException ne) {
		}
		catch (IllegalArgumentException iae) {
		}
		catch (Exception e1) {
			log("Null servlet, causing null ptr exception, caused registration that was not cleaned up");
		}
	}

	public void CaseSensitivity() throws Exception {
		log("TC8 Registering servlets with case sensitive names");
		HttpTestServlet1 servlet_lc = new HttpTestServlet1();
		HttpTestServlet2 servlet_UC = new HttpTestServlet2();
		final String urlstr_lc = "http://localhost:" + _httpPort
				+ "/aa?TestCase=8";
		final String urlstr_UC = "http://localhost:" + _httpPort
				+ "/AA?TestCase=8";
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		URL source_lc = null;
		URL source_UC = null;
		BufferedReader in = null;
		// register servlet with path /aa
		_http.registerServlet("/aa", servlet_lc, null, httpContext);
		log("Servlet resource /aa registered");
		// register servlet with path /AA
		_http.registerServlet("/AA", servlet_UC, null, httpContext);
		log("Servlet resource /AA registered");
		// get and use resoruce /aa
		source_lc = new URL(urlstr_lc);
		in = new BufferedReader(new InputStreamReader(source_lc.openStream()));
		// read lines in resource /aa
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// get and use resoruce /AA
		source_UC = new URL(urlstr_UC);
		in = new BufferedReader(new InputStreamReader(source_UC.openStream()));
		// read lines in resource /AA
		resbuf.setLength(0);
		log("Receiving from servlet resource /AA");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister servlet aa
		_http.unregister("/aa");
		log("Servlet resource /aa unregistered");
		// unregister servlets AA
		_http.unregister("/AA");
		log("Servlet resource /AA unregistered");
	}

	public void GenericServletRegistration() throws Exception {
		log("TC9 Registering javax.servlet.Servlet");
		HttpTestServlet3 servlet = new HttpTestServlet3();
		final String urlstr = "http://localhost:" + _httpPort + "/tc9servlet";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc9servlet
		_http.registerServlet("/tc9servlet", servlet, null, httpContext);
		log("Generic servlet registered");
		// get and use resource /tc9servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc9servlet
		log("Receiving resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc9servlet");
		log("Generic Servlet unregistered");
	}

	public void RegisterResources() throws Exception {
		log("TC10 Registering resources");
		final String res1url = "http://localhost:" + _httpPort
				+ "/tc10resource/tc10.html";
		final String res2url = "http://localhost:" + _httpPort
				+ "/tc10resource/tc10.gif";
		URL source1 = null;
		URL source2 = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register resource with path /tc10resource
		_http.registerResources("/tc10resource", "www", httpContext);
		log("Resource registered");
		source1 = new URL(res1url);
		in = new BufferedReader(new InputStreamReader(source1.openStream()));
		// read lines in HTML resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		source2 = new URL(res2url);
		log("Receiving GIF resource");
		InputStream org = getClass().getResourceAsStream("www/tc10.gif");
		InputStream copy = source2.openStream();
		while (true) {
			int orgByte = org.read();
			int copyByte = copy.read();
			if (orgByte != copyByte) {
				log("GIF does not match!");
				break;
			}
			if (orgByte == -1) {
				log("GIF matches");
				break;
			}
		}
		// unregister resource
		_http.unregister("/tc10resource");
		log("Resource unregistered");
	}

	public void MultipleRegistrations() throws Exception {
		log("TC11 Multiple registering of one resource");
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc11resource/tc11.html";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register resource with path /tc11resource
		_http.registerResources("/tc11resource", "www", httpContext);
		log("Resource registered once");
		// get and use resource /tc11resource
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc11resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// register resource with path /tc11resource once more
		try {
			_http.registerResources("/tc11resource", "www", httpContext);
			log("Resource registered twice");
		}
		catch (NamespaceException e) {
			log("Resource already registered");
		}
		// unregister resource
		_http.unregister("/tc11resource");
		log("Resource unregistered");
	}

	public void MultipleUnregistrations() throws Exception {
		log("TC12 Multiple unregistering of one resource");
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc12resource/tc12.html";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register resource with path /tc12resource
		_http.registerResources("/tc12resource", "www", httpContext);
		log("Resource registered");
		// get and use resource /tc12resource
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc12resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister resource with path /tc12resource
		_http.unregister("/tc12resource");
		log("Resource unregistered once");
		// unregister resource with path /tc12resource once more
		try {
			_http.unregister("/tc12resource");
			log("Resource unregistered twice");
		}
		catch (IllegalArgumentException e) {
			log("Resource already unregistered");
		}
	}

	public void EndingSlash() throws Exception {
		log("TC13 Registering resource with name ending with slash");
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc13resource/tc13.html";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		try {
			// register resource with path /tc13resource
			_http.registerResources("/tc13resource/", "www", httpContext);
			log("Resource registered");
		}
		catch (IllegalArgumentException e) {
			log("Resource could not be registerd due to ending slash");
		}
	}

	public void RootAlias() throws Exception {
		log("TC14 Registering resource with root alias slash");
		final String urlstr = "http://localhost:" + _httpPort + "/tc14.html";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		try {
			// register resource with path /
			_http.registerResources("/", "www", httpContext);
			log("Resource registered with root alias slash");
			// unregister resource with path /
			_http.unregister("/");
			log("Resource unregistered");
		}
		catch (IllegalArgumentException e) {
			log("Resource could not be registerd");
		}
	}

	public void ServletRegDefaultContext() throws Exception {
		log("TC15 Registration of servlet using default HTTP context");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc15servlet?TestCase=15";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc15servlet
		_http.registerServlet("/tc15servlet", servlet, null, null);
		log("Servlet registered with default HTTP context");
		// get and use resource /tc15servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc1servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister servlet
		_http.unregister("/tc15servlet");
		log("Servlet unregistered");
	}

	public void ResourceRegDefaultContext() throws Exception {
		log("TC16 Registering resources using default HTTP context");
		final String res1url = "http://localhost:" + _httpPort
				+ "/tc16resource/tc16.html";
		URL source1 = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register resource with path /tc16resource
		_http.registerResources("/tc16resource",
				"org/osgi/test/cases/http/tbc/www", null);
		log("Resource registered");
		source1 = new URL(res1url);
		in = new BufferedReader(new InputStreamReader(source1.openStream()));
		// read lines in HTML resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister resource
		_http.unregister("/tc16resource");
		log("Resource unregistered");
	}

	public void RegisterMultipleAliases() throws Exception {
		log("TC17 Attempt to register servlet with multiple aliases");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		// register same servlet with paths /tc17a and /tc17b
		try {
			_http.registerServlet("/tc17a", servlet, null, httpContext);
			log("Servlet registered with path tc17a");
			_http.registerServlet("/tc17b", servlet, null, httpContext);
			log("Servlet registered with path tc17a and tc17b");
			// unregister servlet /tc17b
			_http.unregister("/tc17b");
			log("Servlet resource /tc17b unregistered");
		}
		catch (ServletException e1) {
			log("Attempted to register servlet with multiple aliases");
		}
		// unregister servlet /tc17a
		_http.unregister("/tc17a");
		log("Servlet resource /tc17a unregistered");
	}

	// RUNTIME BEHAVIOUR
	public void Empty_doGet() throws Exception {
		log("TC50 Register servlet with empty doGet() method");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc50servlet?TestCase=50";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		_http.registerServlet("/tc50servlet", servlet, null, httpContext);
		log("Servlet resource with empty doGet registered");
		// get and use resource
		source = new URL(urlstr);
		// Disable keep alive for this connection to prevent the URLConnection
		// from having
		// to wait for the keep alive to timeout.
		HttpURLConnection conn = (HttpURLConnection) source.openConnection();
		conn.setRequestProperty("Connection", "close");
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		// read lines in resource
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		in.close();
		// unregister simple servlet
		_http.unregister("/tc50servlet");
		log("Servlet resource with empty doGet unregistered");
	}

	public void Security() throws Exception {
		log("TC51 Verification of statements under handleSecurity()");
		// HttpContext permitting
		HttpContext hc1 = new HttpContext() {
			public java.net.URL getResource(String name) {
				// Map a resource name to a URL.
				return getClass().getResource(name);
			}

			public String getMimeType(String name) {
				// Map a name to a MIME type.
				return null;
			}

			public boolean handleSecurity(HttpServletRequest request,
					HttpServletResponse response) throws java.io.IOException {
				// Handle security for a request.
				log("Handle Security Called");
				return true;
			}
		};
		// HttpContext nonpermitting
		HttpContext hc2 = new HttpContext() {
			// private boolean ispermitted = true;
			public java.net.URL getResource(String name) {
				// Map a resource name to a URL.
				return getClass().getResource(name);
			}

			public String getMimeType(String name) {
				// Map a name to a MIME type.
				return null;
			}

			public boolean handleSecurity(HttpServletRequest request,
					HttpServletResponse response) throws java.io.IOException {
				// Handle security for a request.
				response.setHeader("WWW-Authenticate",
						"Basic realm=\"Some Realm\"");
				try {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
							"Not Authorized");
					/* This call will generate HTML into the response. */
				}
				catch (IOException e) {
				}
				return (false);
			}
		};
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc51servlet?TestCase=51";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc51servlet
		try {
			_http.registerServlet("/tc51servlet", servlet, null, hc1);
			log("Security Servlet registered");
		}
		catch (ServletException se) {
			log("Error registering servlet");
		}
		catch (NamespaceException ne) {
			log("Error namespace registering servlet");
		}
		// get and use resource
		try {
			source = new URL(urlstr);
		}
		catch (MalformedURLException mue) {
		}
		// read lines in resource
		log("Receiving from servlet");
		try {
			// Access with permission
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			log(resbuf.toString());
		}
		catch (IOException ioe) {
			log("IOException in servlet");
			ioe.printStackTrace();
		}
		finally {
			// unregister simple servlet
			in.close();
			_http.unregister("/tc51servlet");
			log("Security Servlet unregistered");
		}
		// Reregister servlet with path /tc51servlet
		try {
			_http.registerServlet("/tc51servlet", servlet, null, hc2);
			log("Security Servlet reregistered");
		}
		catch (ServletException se) {
			log("Error registering servlet");
		}
		catch (NamespaceException ne) {
			log("Error namespace registering servlet");
		}
		// get and use resource
		try {
			source = new URL(urlstr);
		}
		catch (MalformedURLException mue) {
		}
		try {
			// Access without permission
			HttpURLConnection conn = (HttpURLConnection) source.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new IOException("401");
			}
			log("Access improperly allowed");
		}
		catch (IOException fnfe) {
			log("Access denied");
		}
		finally {
			// unregister simple servlet
			_http.unregister("/tc51servlet");
			log("Security Servlet unregistered");
		}
	}

	/**
	 * This test seems to be nonsense because exceptions thrown in a servlet do
	 * not have a defined result in the output. It could send the stream so far
	 * or it could append it with the error message. This test is disabled for
	 * now.
	 */
	public void doGetExceptions() throws Exception {
		log("TC52 Servlet throws exception in doGet()");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc52servlet?TestCase=52";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc52servlet
		_http.registerServlet("/tc52servlet", servlet, null, httpContext);
		log("Servlet resource throwing exception registered");
		// get and use resource
		source = new URL(urlstr);
		// read lines in resource
		log("Receiving from servlet");
		try {
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			log(resbuf.toString());
			in.close();
		}
		catch (IOException ioe) {
			log(resbuf.toString());
			log("IOException in servlet");
			ioe.printStackTrace();
		}
		finally {
			// unregister simple servlet
			_http.unregister("/tc52servlet");
			log("Servlet resource throwing exception unregistered");
		}
	}

	public void LargeOutput() throws Exception {
		log("TC53 Servlet returns 4 MB file");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc53servlet?TestCase=53";
		URL source = null;
		BufferedReader in = null;
		int bufsize = 0;
		int len = 0;
		// register servlet with path /tc54servlet
		_http.registerServlet("/tc53servlet", servlet, null, httpContext);
		log("Servlet resource returning large output registered");
		// get and use resoruce /tc53servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		char[] buf = new char[1024];
		log("Receiving from servlet");
		while ((len = in.read(buf, 0, buf.length)) > 0)
			bufsize += len;
		log("bufsize = " + bufsize);
		in.close();
		// unregister simple servlet
		_http.unregister("/tc53servlet");
		log("Servlet resource returning large output unregistered");
	}

	// Remove tc54 as this is not needed
	// public void WebServerVersion () throws Exception
	// {
	// log ( "TC54 Query version of http service" );
	//
	// HttpTestServlet1 servlet = new HttpTestServlet1();
	// final String urlstr = "http://localhost:" +
	// _httpPort +
	// "/tc54servlet?TestCase=54";
	// URL source = null;
	// String inputLine = new String();
	// StringBuffer resbuf = new StringBuffer();
	// BufferedReader in = null;
	//
	// // register servlet with path /tc54servlet
	// _http.registerServlet("/tc54servlet",
	// servlet,
	// null,
	// httpContext);
	// log("Servlet resource returning http version registered");
	//
	// // get and use resoruce /tc54servlet
	// source = new URL(urlstr);
	// in = new BufferedReader(new InputStreamReader(source.openStream()));
	//
	// // read lines in resource /tc54servlet
	// log ("Receiving from servlet");
	// while ((inputLine = in.readLine()) != null) { resbuf.append(inputLine); }
	// log (resbuf.toString());
	// in.close();
	//
	// // unregister simple servlet
	// _http.unregister ("/tc54servlet");
	// log ("Servlet resource returning http version unregistered");
	// }
	// End of tc54

	public void LargeURL() throws Exception {
		log("TC56 Access of servlet with long parameter values");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc56servlet?TestCase=56";
		String param1 = "param1=value1";
		String param2 = "param2=abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc56servlet
		_http.registerServlet("/tc56servlet", servlet, null, httpContext);
		log("Servlet accessed with long parameter registered");
		// get and use resource /tc56servlet
		source = new URL(urlstr + "&" + param1 + "&" + param2);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc56servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc56servlet");
		log("Servlet accessed with long parameter unregistered");
	}

	public void OddURL() throws Exception {
		log("TC57 Access URL with special parameter values");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc57servlet?TestCase=57";
		String param1 = "param1=" + URLEncoder.encode("&");
		String param2 = "param2=" + URLEncoder.encode("&&");
		String param3 = "param3=" + URLEncoder.encode("%");
		String param4 = "param4=" + URLEncoder.encode(" ");
		String param5 = "param5=" + URLEncoder.encode("?");
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc57servlet
		_http.registerServlet("/tc57servlet", servlet, null, httpContext);
		log("Servlet accessed with special parameter values registered");
		// get and use resource /tc57servlet
		source = new URL(urlstr + "&" + param1 + "&" + param2 + "&" + param3
				+ "&" + param4 + "&" + param5);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// log (source.toString()); //for testing purposes!!
		// read lines in resource /tc57servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc57servlet");
		log("Servlet accessed with special parameter values unregistered");
	}

	public void MultipleNamesURL() throws Exception {
		log("TC58a Access URL using getParameter with different values for the same parameter");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc58aservlet?TestCase=58a";
		String param1 = "param1=value1";
		String param2 = "param1=value2";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc58aservlet
		_http.registerServlet("/tc58aservlet", servlet, null, httpContext);
		log("Servlet accessed with different values for the same parameter registered");
		// get and use resource /tc58aservlet
		source = new URL(urlstr + "&" + param1 + "&" + param2);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc58aservlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc58aservlet");
		log("Servlet accessed with different values for the same parameter unregistered");
	}

	public void MultipleNamesURL2() throws Exception {
		log("TC58b Access URL using getParameterValues with different values for the same parameter");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc58bservlet?TestCase=58b";
		String param1 = "param1=value1";
		String param2 = "param1=value2";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc58bservlet
		_http.registerServlet("/tc58bservlet", servlet, null, httpContext);
		log("Servlet accessed with different values for the same parameter registered");
		// get and use resoruce /tc58bservlet
		source = new URL(urlstr + "&" + param1 + "&" + param2);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc58bservlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc58bservlet");
		log("Servlet accessed with different values for the same parameter unregistered");
	}

	public void MIMEtypes() throws Exception {
		log("TC59 Verify most common MIME types");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr1 = "http://localhost:" + _httpPort
				+ "/tc59servlet?TestCase=59&type=plain";
		final String urlstr2 = "http://localhost:" + _httpPort
				+ "/tc59servlet?TestCase=59&type=html";
		final String urlstr3 = "http://localhost:" + _httpPort
				+ "/tc59servlet?TestCase=59&type=jpeg";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		String ct = new String();
		BufferedReader in = null;
		// register servlet with path /tc59servlet
		_http.registerServlet("/tc59servlet", servlet, null, httpContext);
		log("Servlet TC59Servlet registered");
		// get and use resoruce /tc59servlet
		source = new URL(urlstr1);
		URLConnection uc1 = source.openConnection();
		ct = filterMIMEtype(uc1.getContentType());
		log("ContentType = " + ct);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc59servlet
		log("Receiving text/plain from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		source = new URL(urlstr2);
		URLConnection uc2 = source.openConnection();
		ct = filterMIMEtype(uc2.getContentType());
		log("ContentType = " + ct);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		resbuf.setLength(0);
		// read lines in resource /tc59servlet
		log("Receiving text/html from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		source = new URL(urlstr3);
		URLConnection uc3 = source.openConnection();
		ct = filterMIMEtype(uc3.getContentType());
		log("ContentType = " + ct);
		// unregister simple servlet
		_http.unregister("/tc59servlet");
		log("Servlet TC59Servlet unregistered");
	}

	public void ResourceName() throws Exception {
		log("TC60 Verify resource name construction");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/dir1/dir2/tc60servlet?TestCase=60";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc60servlet
		_http.registerServlet("/dir1/dir2/tc60servlet", servlet, null,
				httpContext);
		log("Servlet TC60Servlet registered");
		// get and use resource /tc60servlet
		source = new URL(urlstr);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		// read lines in resource /tc60servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		log(resbuf.toString());
		// unregister simple servlet
		_http.unregister("/dir1/dir2/tc60servlet");
		log("Servlet TC60Servlet unregistered");
	}

	public void Authentication() throws Exception {
		log("TC61 Verification of authentication under handleSecurity()");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc61servlet?TestCase=61";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlet with path /tc61servlet using default context
		try {
			_http.registerServlet("/tc61servlet", servlet, null,
					new HttpContext() {
						public boolean handleSecurity(
								HttpServletRequest request,
								HttpServletResponse response)
								throws IOException {
							log("Handle Security Called");
							request.setAttribute(HttpContext.REMOTE_USER,
									"unknown user");
							request.setAttribute(
									HttpContext.AUTHENTICATION_TYPE, "Basic");
							return true;
						}

						public URL getResource(String name) {
							return null;
						}

						public String getMimeType(String name) {
							return null;
						}
					});
			log("Servlet registered");
		}
		catch (ServletException se) {
			log("Error registering servlet");
		}
		catch (NamespaceException ne) {
			log("Error namespace registering servlet");
		}
		// read lines in resource
		log("Receiving from servlet");
		try {
			// get and use resource /tc61servlet
			source = new URL(urlstr);
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			// read lines in resource /tc1servlet
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			log(resbuf.toString());
		}
		catch (IOException ioe) {
			log("IOException in servlet");
			ioe.printStackTrace();
		}
		finally {
			// unregister simple servlet
			in.close();
			_http.unregister("/tc61servlet");
			log("Servlet unregistered");
		}
	}

	public void ServletContextSharing() throws Exception {
		log("TC62 Verification of servlet context sharing");
		// HttpContext for servlet1 and servlet2
		HttpContext hc1 = _http.createDefaultHttpContext();
		// HttpContext for servlet3
		HttpContext hc2 = _http.createDefaultHttpContext();
		HttpTestServlet1 servlet1 = new HttpTestServlet1();
		HttpTestServlet2 servlet2 = new HttpTestServlet2();
		HttpTestServlet4 servlet3 = new HttpTestServlet4();
		final String urlstr1 = "http://localhost:" + _httpPort
				+ "/tc62servlet1?TestCase=62";
		final String urlstr2 = "http://localhost:" + _httpPort
				+ "/tc62servlet2?TestCase=62";
		final String urlstr3 = "http://localhost:" + _httpPort
				+ "/tc62servlet3?TestCase=62";
		URL source1 = null;
		URL source2 = null;
		URL source3 = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register servlets
		try {
			_http.registerServlet("/tc62servlet1", servlet1, null, hc1);
			log("Servlet1 registered");
			_http.registerServlet("/tc62servlet2", servlet2, null, hc1);
			log("Servlet2 registered");
			_http.registerServlet("/tc62servlet3", servlet3, null, hc2);
			log("Servlet3 registered");
		}
		catch (ServletException se) {
			log("Error registering servlet");
		}
		catch (NamespaceException ne) {
			log("Error namespace registering servlet");
		}
		try {
			// get and use resource /tc62servlet1
			source1 = new URL(urlstr1);
			in = new BufferedReader(new InputStreamReader(source1.openStream()));
			// read lines in resource /tc62servlet1
			log("Receiving from servlet1");
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			log(resbuf.toString());
			// get and use resource /tc62servlet2
			source2 = new URL(urlstr2);
			in = new BufferedReader(new InputStreamReader(source2.openStream()));
			// read lines in resource /tc62servlet2
			log("Receiving from servlet2");
			resbuf.setLength(0);
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			log(resbuf.toString());
			// get and use resource /tc62servlet3
			source3 = new URL(urlstr3);
			in = new BufferedReader(new InputStreamReader(source3.openStream()));
			// read lines in resource /tc62servlet3
			log("Receiving from servlet3");
			resbuf.setLength(0);
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			log(resbuf.toString());
		}
		catch (IOException ioe) {
			log("IOException in servlet");
			ioe.printStackTrace();
		}
		finally {
			// unregister simple servlet
			in.close();
			_http.unregister("/tc62servlet1");
			log("Servlet1 unregistered");
			_http.unregister("/tc62servlet2");
			log("Servlet2 unregistered");
			_http.unregister("/tc62servlet3");
			log("Servlet3 unregistered");
		}
	}

	public void Permissions() {
		log("TC63 Registering resources without correct permissions");
		// BUGBUG need to complete test case!!
	}

	int guessHttpPort() {
		// Try to find the HTTP port.
		String p;
		int tBax = 0;
		// First check if the user has a preference.
		p = getContext().getProperty("org.osgi.service.http.port");
		if (p == null) {
			p = System.getProperty("org.osgi.service.http.port");
		}
		if (p != null) {
			tBax = Integer.parseInt(p);
		}
		else {
			ServerSocket sock;
			try {
				// Otherwise pick 8080 if it's busy.
				sock = new ServerSocket(8080);
				sock = null;
				// Port 8080 was not busy, go for 80.
				tBax = 80;
			}
			catch (IOException ioe) {
				tBax = 8080;
			}
		}
		return tBax;
	}

	// HttpContext
	private HttpContext	httpContext	= new HttpContext() {
										public java.net.URL getResource(
												final String name) {
											// Map a resource name to a URL.
											return (URL) AccessController
													.doPrivileged(new PrivilegedAction() {
														public Object run() {
															return getClass()
																	.getResource(
																			name);
														}
													});
										}

										public String getMimeType(String name) {
											// Map a name to a MIME type.
											return null;
										}

										public boolean handleSecurity(
												HttpServletRequest request,
												HttpServletResponse response)
												throws java.io.IOException {
											// Handle security for a request.
											return true;
										}
									};

	// remove attributes from MIME-type and convert it into lower case
	private String filterMIMEtype(String mime) {
		if (mime.indexOf(';') != -1)
			return mime.substring(0, mime.indexOf(';')).toLowerCase();
		else
			return mime.toLowerCase();
	}

}
