/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Telia Research AB. 2000.
 * This source code is owned by Telia Research AB,
 * and is being distributed to OSGi MEMBERS as
 * MEMBER LICENSED MATERIALS under the terms of section 3.2 of
 * the OSGi MEMBER AGREEMENT.
 * Updated June 2001
 */
package org.osgi.test.cases.http.junit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

public class HttpTestBundle1 extends DefaultTestBundleControl {
	// Http service
	HttpService			_http;
	ServiceReference	_httpSR;
	int					_httpPort;
	
	protected void setUp() {
		_httpSR = getContext().getServiceReference(HttpService.class.getName());
		assertNotNull(_httpSR);
		_http = (HttpService) getContext().getService(_httpSR);
		assertNotNull(_http);
		_httpPort = guessHttpPort();
	}

	protected void tearDown() {
		getContext().ungetService(_httpSR);
	}
	
	// REGISTRATION BEHAVIOUR
	public void testSimpleServletRegistration() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc1servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>1 Registration of simple servlet</h1></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc1servlet");
		log("Simple Servlet unregistered");
	}

	public void testSimpleServletUnregistration() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc2servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>2 Unregistration of simple servlet</h1></body></html>",
				resbuf.toString());
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
			fail("Simple servlet improperly available");
		}
		catch (IOException e) {
			log("Simple servlet not available");
			in.close();
		}
	}

	public void testOverlappingRegistrationPaths() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /aa/bb
		log("Receiving from servlet resource /aa/bb");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>3 Registration of two overlapping servlets</h1><p>Servlet aabb</p></body></html>",
				resbuf.toString());
		// get and use first resource /aa
		source_aa = new URL(urlstr_aa);
		in = new BufferedReader(new InputStreamReader(source_aa.openStream()));
		assertNotNull(in);
		// read lines in resource /aa
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>3 Registration of two overlapping servlets</h1><p>Servlet aa</p></body></html>",
				resbuf.toString());
		// unregister servlets with overlapping names
		_http.unregister("/aa");
		log("Servlet resource /aa unregistered");
		_http.unregister("/aa/bb");
		log("Servlet resource /aa/bb unregistered");
	}

	public void testUnregistringOverlapping() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /aa/bb
		log("Receiving from servlet resource /aa/bb");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>4 Unregistration of overlapping servlet</h1><p>Servlet aabb</p></body></html>",
				resbuf.toString());
		// get and use first resource /aa
		source_aa = new URL(urlstr_aa);
		in = new BufferedReader(new InputStreamReader(source_aa.openStream()));
		assertNotNull(in);
		// read lines in resource /aa
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>4 Unregistration of overlapping servlet</h1><p>Servlet aa</p></body></html>",
				resbuf.toString());
		// unregister servlet with path /aa/bb
		_http.unregister("/aa/bb");
		log("Servlet resource /aa/bb unregistered");
		// try to get and use the resoruce /aa/bb after unregistering
		in = new BufferedReader(new InputStreamReader(source_aabb.openStream()));
		assertNotNull(in);
		// read lines in resource /aa/bb
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa/bb");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>4 Unregistration of overlapping servlet</h1><p>Servlet aa</p></body></html>",
				resbuf.toString());
		// get and use first resoruce /aa
		in = new BufferedReader(new InputStreamReader(source_aa.openStream()));
		assertNotNull(in);
		// read lines in resource /aa
		resbuf.setLength(0);
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>4 Unregistration of overlapping servlet</h1><p>Servlet aa</p></body></html>",
				resbuf.toString());
		// unregister servlets with overlapping names
		_http.unregister("/aa");
		log("Servlet resource /aa unregistered");
	}

	public void testBundleStopping() throws Exception {
		log("TC5 Stopping of source bundle");
		final String urlstr = "http://localhost:" + _httpPort + "/tc5servlet";
		log("Check if tc5servlet available before bundle installed (no)");
		check(urlstr, null);
		Bundle tb1 = getContext().installBundle("BundleStoppingHelperBundle",
				getClass().getResourceAsStream("/tb1.jar"));
		assertNotNull(tb1);
		log("Check if tc5servlet available after bundle installed (no)");
		check(urlstr, null);
		tb1.start();
		log("Check if tc5servlet available after bundle started (yes)");
		check(
				urlstr,
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>5 Uninstallation of source bundle</h1></body></html>");
		tb1.stop();
		log("Check if tc5servlet available after bundle stopped (no)");
		check(urlstr, null);
		tb1.start();
		log("Check if tc5servlet available after bundle started again (yes)");
		check(
				urlstr,
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>5 Uninstallation of source bundle</h1></body></html>");
		tb1.uninstall();
		log("Check if tc5servlet available after bundle uninstalled without stop (no)");
		check(urlstr, null);
	}

	private void check(String urlstr, String expected) {
		URL source = null;
		String inputLine;
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		try {
			source = new URL(urlstr);
		}
		catch (MalformedURLException e) {
			fail("Malformed URL " + urlstr);
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
			assertNotNull(in);
			// read lines in resource /tc5servlet
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			in.close();
			assertEquals(expected, resbuf.toString());
			log("--- end file: " + source.getPath());
		}
		catch (IOException e) {
			if (expected != null) {
				fail(source.getPath() + " not available");
			}
		}
	}

	public void testLongRegistrationName() throws Exception {
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
		assertNotNull(in);
		// read lines in resource
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>6 Registering servlet with long name</h1></body></html>",
				resbuf.toString());
		// unregister servlet with long name
		_http
				.unregister("/abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz");
		log("Servlet resource with long name unregistered");
	}

	public void testNullServlet() throws Exception {
		log("TC7 Attempt to register null servlet");
		HttpTestServlet1 servlet = null;
		// register servlet with path /tc7servlet
		try {
			_http.registerServlet("/tc7servlet", servlet, null, httpContext);
			fail("Null servlet registered");
		}
		catch (Exception e1) {
			log("Exception for null servlet: " + e1);
		}
	}

	public void testCaseSensitivity() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /aa
		log("Receiving from servlet resource /aa");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>8 Case sensitivity</h1><p>Lowercase Servlet</p></body></html>",
				resbuf.toString());
		// get and use resoruce /AA
		source_UC = new URL(urlstr_UC);
		in = new BufferedReader(new InputStreamReader(source_UC.openStream()));
		assertNotNull(in);
		// read lines in resource /AA
		resbuf.setLength(0);
		log("Receiving from servlet resource /AA");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>8 Case sensitivity</h1><p>Uppercase Servlet</p></body></html>",
				resbuf.toString());
		// unregister servlet aa
		_http.unregister("/aa");
		log("Servlet resource /aa unregistered");
		// unregister servlets AA
		_http.unregister("/AA");
		log("Servlet resource /AA unregistered");
	}

	public void testGenericServletRegistration() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc9servlet
		log("Receiving resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>9 javax.servlet.Servlet registration</h1></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc9servlet");
		log("Generic Servlet unregistered");
	}

	public void testRegisterResources() throws Exception {
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
		assertNotNull(in);
		// read lines in HTML resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>TestCaseServlet TC10</title></head><body><h1>OSGi - HTTP Service test case TC10 - Register resources</h1></body></html>",
				resbuf.toString());
		source2 = new URL(res2url);
		log("Receiving GIF resource");
		InputStream org = getClass().getResourceAsStream("www/tc10.gif");
		assertNotNull(org);
		InputStream copy = source2.openStream();
		assertNotNull(copy);
		while (true) {
			int orgByte = org.read();
			int copyByte = copy.read();
			if (orgByte != copyByte) {
				fail("GIF does not match!");
				break;
			}
			if (orgByte == -1) {
				log("GIF matches");
				break;
			}
		}
		org.close();
		copy.close();
		// unregister resource
		_http.unregister("/tc10resource");
		log("Resource unregistered");
	}

	public void testMultipleRegistrations() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc11resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>TestCaseServlet TC11</title></head><body><h1>OSGi - HTTP Service test case TC11 - Multiple registrations</h1></body></html>",
				resbuf.toString());
		// register resource with path /tc11resource once more
		try {
			_http.registerResources("/tc11resource", "www", httpContext);
			fail("Resource registered twice");
		}
		catch (NamespaceException e) {
			log("Resource already registered");
		}
		// unregister resource
		_http.unregister("/tc11resource");
		log("Resource unregistered");
	}

	public void testMultipleUnregistrations() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc12resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>TestCaseServlet TC12</title></head><body><h1>OSGi - HTTP Service test case TC12 - Multiple unregistrations</h1></body></html>",
				resbuf.toString());
		// unregister resource with path /tc12resource
		_http.unregister("/tc12resource");
		log("Resource unregistered once");
		// unregister resource with path /tc12resource once more
		try {
			_http.unregister("/tc12resource");
			fail("Resource unregistered twice");
		}
		catch (IllegalArgumentException e) {
			log("Resource already unregistered");
		}
	}

	public void testEndingSlash() throws Exception {
		log("TC13 Registering resource with name ending with slash");
		try {
			// register resource with path /tc13resource
			_http.registerResources("/tc13resource/", "www", httpContext);
			fail("Resource registered");
		}
		catch (IllegalArgumentException e) {
			log("Resource could not be registerd due to ending slash");
		}
	}

	public void testRootAlias() throws Exception {
		log("TC14 Registering resource with root alias slash");
		try {
			// register resource with path /
			_http.registerResources("/", "www", httpContext);
			log("Resource registered with root alias slash");
			// unregister resource with path /
			_http.unregister("/");
			log("Resource unregistered");
		}
		catch (IllegalArgumentException e) {
			fail("Resource could not be registerd");
		}
	}

	public void testServletRegDefaultContext() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc1servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>15 Registration of servlet using default HTTP context</h1></body></html>",
				resbuf.toString());
		// unregister servlet
		_http.unregister("/tc15servlet");
		log("Servlet unregistered");
	}

	public void testResourceRegDefaultContext() throws Exception {
		log("TC16 Registering resources using default HTTP context");
		final String res1url = "http://localhost:" + _httpPort
				+ "/tc16resource/tc16.html";
		URL source1 = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		// register resource with path /tc16resource
		_http.registerResources("/tc16resource",
				"org/osgi/test/cases/http/junit/www", null);
		log("Resource registered");
		source1 = new URL(res1url);
		in = new BufferedReader(new InputStreamReader(source1.openStream()));
		assertNotNull(in);
		// read lines in HTML resource
		log("Receiving HTML resource");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>TestCaseServlet TC16</title></head><body><h1>OSGi - HTTP Service test case TC16 - Default HTTP context</h1></body></html>",
				resbuf.toString());
		// unregister resource
		_http.unregister("/tc16resource");
		log("Resource unregistered");
	}

	public void testRegisterMultipleAliases() throws Exception {
		log("TC17 Attempt to register servlet with multiple aliases");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		// register same servlet with paths /tc17a and /tc17b
		try {
			_http.registerServlet("/tc17a", servlet, null, httpContext);
			log("Servlet registered with path tc17a");
			_http.registerServlet("/tc17b", servlet, null, httpContext);
			fail("Servlet registered with path tc17a and tc17b");
			// unregister servlet /tc17b
			_http.unregister("/tc17b");
			fail("Servlet resource /tc17b unregistered");
		}
		catch (ServletException e1) {
			log("Attempted to register servlet with multiple aliases");
		}
		// unregister servlet /tc17a
		_http.unregister("/tc17a");
		log("Servlet resource /tc17a unregistered");
	}

	// RUNTIME BEHAVIOUR
	public void testEmpty_doGet() throws Exception {
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
		assertNotNull(in);
		// read lines in resource
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals("", resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc50servlet");
		log("Servlet resource with empty doGet unregistered");
	}

	public void testSecurity() throws Exception {
		log("TC51 Verification of statements under handleSecurity()");
		// HttpContext permitting
		final boolean[] handleSecurityCalled = {false};
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
				handleSecurityCalled[0] = true;
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
					// empty
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
			fail("Error registering servlet", se);
		}
		catch (NamespaceException ne) {
			fail("Error namespace registering servlet", ne);
		}
		// get and use resource
		try {
			source = new URL(urlstr);
		}
		catch (MalformedURLException mue) {
			fail("url error", mue);
		}
		// read lines in resource
		log("Receiving from servlet");
		try {
			// Access with permission
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			assertNotNull(in);
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			in.close();
			assertTrue(handleSecurityCalled[0]);
			assertEquals(
					"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>51 Verification of statements under handleSecurity()</h1></body></html>",
					resbuf.toString());
		}
		catch (IOException ioe) {
			fail("IOException in servlet", ioe);
		}
		finally {
			// unregister simple servlet
			_http.unregister("/tc51servlet");
			log("Security Servlet unregistered");
		}
		// Reregister servlet with path /tc51servlet
		try {
			_http.registerServlet("/tc51servlet", servlet, null, hc2);
			log("Security Servlet reregistered");
		}
		catch (ServletException se) {
			fail("Error registering servlet", se);
		}
		catch (NamespaceException ne) {
			fail("Error namespace registering servlet", ne);
		}
		// get and use resource
		try {
			source = new URL(urlstr);
		}
		catch (MalformedURLException mue) {
			fail("url error", mue);
		}
		try {
			// Access without permission
			HttpURLConnection conn = (HttpURLConnection) source.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new IOException("401");
			}
			fail("Access improperly allowed");
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

	public void testLargeOutput() throws Exception {
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
		assertNotNull(in);
		char[] buf = new char[1024];
		log("Receiving from servlet");
		while ((len = in.read(buf, 0, buf.length)) > 0)
			bufsize += len;
		in.close();
		assertEquals(4194304, bufsize);
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

	public void testLargeURL() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc56servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>56 Access of servlet with long parameter values</h1><p>Parameter: param1 Value: value1</p><p>Parameter: param2 Value: abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz</p></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc56servlet");
		log("Servlet accessed with long parameter unregistered");
	}

	public void testOddURL() throws Exception {
		log("TC57 Access URL with special parameter values");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc57servlet?TestCase=57";
		String param1 = "param1=" + URLEncoder.encode("&", "UTF-8");
		String param2 = "param2=" + URLEncoder.encode("&&", "UTF-8");
		String param3 = "param3=" + URLEncoder.encode("%", "UTF-8");
		String param4 = "param4=" + URLEncoder.encode(" ", "UTF-8");
		String param5 = "param5=" + URLEncoder.encode("?", "UTF-8");
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
		assertNotNull(in);
		// log (source.toString()); //for testing purposes!!
		// read lines in resource /tc57servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>57 Access URL with special parameter values</h1><p>Parameter: param1 Value: &</p><p>Parameter: param2 Value: &&</p><p>Parameter: param3 Value: %</p><p>Parameter: param4 Value:  </p><p>Parameter: param5 Value: ?</p></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc57servlet");
		log("Servlet accessed with special parameter values unregistered");
	}

	public void testMultipleNamesURL() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc58aservlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>58a Access URL using getParameter with different values for the same parameter</h1><p>Parameter: param1 Value: value1, value2</p></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc58aservlet");
		log("Servlet accessed with different values for the same parameter unregistered");
	}

	public void testMultipleNamesURL2() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc58bservlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>58b Access URL using getParameterValues with different values for the same parameter</h1><p>Parameter: param1 Value: value1,value2</p></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/tc58bservlet");
		log("Servlet accessed with different values for the same parameter unregistered");
	}

	public void testMIMEtypes() throws Exception {
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
		assertEquals("text/plain", ct);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		assertNotNull(in);
		// read lines in resource /tc59servlet
		log("Receiving text/plain from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"OSGi - HTTP Service test case59 Verify most common MIME types",
				resbuf.toString());
		source = new URL(urlstr2);
		URLConnection uc2 = source.openConnection();
		ct = filterMIMEtype(uc2.getContentType());
		assertEquals("text/html", ct);
		in = new BufferedReader(new InputStreamReader(source.openStream()));
		assertNotNull(in);
		resbuf.setLength(0);
		// read lines in resource /tc59servlet
		log("Receiving text/html from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>59 Verify most common MIME types</h1></body></html>",
				resbuf.toString());
		source = new URL(urlstr3);
		URLConnection uc3 = source.openConnection();
		ct = filterMIMEtype(uc3.getContentType());
		assertEquals("image/jpeg", ct);
		// unregister simple servlet
		_http.unregister("/tc59servlet");
		log("Servlet TC59Servlet unregistered");
	}

	public void testResourceName() throws Exception {
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
		assertNotNull(in);
		// read lines in resource /tc60servlet
		log("Receiving from servlet");
		while ((inputLine = in.readLine()) != null) {
			resbuf.append(inputLine);
		}
		in.close();
		assertEquals(
				"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>60 Verify resource name construction</h1><p>RequestURI = /dir1/dir2/tc60servlet</p></body></html>",
				resbuf.toString());
		// unregister simple servlet
		_http.unregister("/dir1/dir2/tc60servlet");
		log("Servlet TC60Servlet unregistered");
	}

	public void testAuthentication() throws Exception {
		log("TC61 Verification of authentication under handleSecurity()");
		HttpTestServlet1 servlet = new HttpTestServlet1();
		final String urlstr = "http://localhost:" + _httpPort
				+ "/tc61servlet?TestCase=61";
		URL source = null;
		String inputLine = new String();
		StringBuffer resbuf = new StringBuffer();
		BufferedReader in = null;
		final boolean[] handleSecurityCalled = {false};
		// register servlet with path /tc61servlet using default context
		try {
			_http.registerServlet("/tc61servlet", servlet, null,
					new HttpContext() {
						public boolean handleSecurity(
								HttpServletRequest request,
								HttpServletResponse response)
								throws IOException {
							log("Handle Security Called");
							handleSecurityCalled[0] = true;
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
			fail("Error registering servlet", se);
		}
		catch (NamespaceException ne) {
			fail("Error namespace registering servlet", ne);
		}
		// read lines in resource
		log("Receiving from servlet");
		try {
			// get and use resource /tc61servlet
			source = new URL(urlstr);
			in = new BufferedReader(new InputStreamReader(source.openStream()));
			assertNotNull(in);
			// read lines in resource /tc1servlet
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			in.close();
			assertTrue(handleSecurityCalled[0]);
			assertEquals(
					"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>61 Verification of authentication under handleSecurity()</h1><p>Remote User is unknown user, Authentication Type is Basic</p></body></html>",
					resbuf.toString());
		}
		catch (IOException ioe) {
			fail("IOException in servlet", ioe);
		}
		finally {
			// unregister simple servlet
			_http.unregister("/tc61servlet");
			log("Servlet unregistered");
		}
	}

	public void testServletContextSharing() throws Exception {
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
			fail("Error registering servlet", se);
		}
		catch (NamespaceException ne) {
			fail("Error namespace registering servlet", ne);
		}
		try {
			// get and use resource /tc62servlet1
			source1 = new URL(urlstr1);
			in = new BufferedReader(new InputStreamReader(source1.openStream()));
			assertNotNull(in);
			// read lines in resource /tc62servlet1
			log("Receiving from servlet1");
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			in.close();
			assertEquals(
					"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>62 Verification of ServletContext sharing</h1><p>Writing parameter: param1 with value: val1</p></body></html>",
					resbuf.toString());
			// get and use resource /tc62servlet2
			source2 = new URL(urlstr2);
			in = new BufferedReader(new InputStreamReader(source2.openStream()));
			assertNotNull(in);
			// read lines in resource /tc62servlet2
			log("Receiving from servlet2");
			resbuf.setLength(0);
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			in.close();
			assertEquals(
					"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>62 Verification of ServletContext sharing</h1><p>Parameter param1 has value: val1</p></body></html>",
					resbuf.toString());
			// get and use resource /tc62servlet3
			source3 = new URL(urlstr3);
			in = new BufferedReader(new InputStreamReader(source3.openStream()));
			assertNotNull(in);
			// read lines in resource /tc62servlet3
			log("Receiving from servlet3");
			resbuf.setLength(0);
			while ((inputLine = in.readLine()) != null) {
				resbuf.append(inputLine);
			}
			in.close();
			assertEquals(
					"<html><head><title>OSGi - HTTP Service test case</title></head><body><h1>62 Verification of ServletContext sharing</h1><p>Parameter param1 has value: null</p></body></html>",
					resbuf.toString());
		}
		catch (IOException ioe) {
			fail("IOException in servlet", ioe);
		}
		finally {
			// unregister simple servlet
			_http.unregister("/tc62servlet1");
			log("Servlet1 unregistered");
			_http.unregister("/tc62servlet2");
			log("Servlet2 unregistered");
			_http.unregister("/tc62servlet3");
			log("Servlet3 unregistered");
		}
	}

	public void xxxtestPermissions() {
		log("TC63 Registering resources without correct permissions");
		// BUGBUG need to complete test case!!
	}

	private int guessHttpPort() {
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
			try {
				// Otherwise pick 8080 if it's busy.
				new ServerSocket(8080);
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
