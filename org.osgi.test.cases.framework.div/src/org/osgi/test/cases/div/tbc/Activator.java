/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.div.tbc;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.test.cases.div.tb6.*;
import org.osgi.test.service.*;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Radio Systems AB
 */
public class Activator extends Thread implements FrameworkListener,
		BundleActivator {
	private ServiceReference	serviceRef;
	BundleContext				_context;
	ServiceReference			_linkRef;
	TestCaseLink				_link;
	String						_tcHome;
	boolean						_continue	= true;
	static final int			TESTS		= 5;
	static String[]				methods		= new String[] {
			"testManifestHeaders", "testMissingManifestHeaders",
			"testBundleClassPath", "testNativeCode", "testFrameworkListener",
			"testFileAccess",
			/* "testDoubleManifestTags", removed since spec changed */
			"testBundleZero", "testEERequirement"};

	/**
	 * start. Gets a reference to the TestCaseLink to communicate with the
	 * TestCase.
	 */
	public void start(BundleContext context) {
		_context = context;
		_linkRef = _context.getServiceReference(TestCaseLink.class.getName());
		_link = (TestCaseLink) _context.getService(_linkRef);
		start();
	}

	public void stop(BundleContext context) {
		quit();
	}

	/**
	 * This function performs the tests.
	 */
	public void run() {
		int progress = 0;
		try {
			_link.log("Test bundle control started Ok.");
			_tcHome = (String) _link.receive(10000);
			for (int i = 0; _continue && i < methods.length; i++) {
				Method method = getClass().getDeclaredMethod(methods[i],
						new Class[0]);
				method.invoke(this, new Object[0]);
				_link.send("" + 100 * (i + 1) / methods.length);
			}
			_link.send("ready");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("done");
	}

	/**
	 * Releases the reference to the TestCaseLink.
	 */
	void quit() {
		if (_continue) {
			_context.ungetService(_linkRef);
			_linkRef = null;
			_continue = false;
		}
	}

	void log(String test, String result) {
		try {
			if (result == null)
				result = "";
			_link.log(test + " " + result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs the manifest headers.
	 */
	void testManifestHeaders() throws Exception {
		Bundle tb;
		Dictionary h;
		log(".", "");
		_link.log("Testing Manifest header syntax.");
		try {
			tb = _context.installBundle(_tcHome + "tb1.jar");
			tb.start();
			h = tb.getHeaders();
			_link.log(h.size() + " headers.");
			// The order of the headers isn't specified, so we'd better sort
			// them.
			for (Enumeration e = sort(h.keys()); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				if ( ! key.equalsIgnoreCase("import-package")) {
					String value = (String) h.get(key);
					StringBuffer result = new StringBuffer();
					for (int i = 0; i < value.length(); i++) {
						char c = value.charAt(i);
						if (c < 128)
							result.append(c);
						else {
							String hex = Integer.toHexString(c);
							result.append("\\u").append(
									"0000".substring(hex.length())).append(hex);
						}
					}
					_link.log(key.toLowerCase() + ": " + result);
				}
			}
			tb.stop();
			tb.uninstall();
		}
		catch (BundleException e) {
			log("Exception in manifest headers", "" + e);
			log("NestedException", "" + e.getNestedException());
		}
	}

	/**
	 * Tests empty manifest headers.
	 */
	void testMissingManifestHeaders() throws Exception {
		Bundle tb;
		Dictionary h;
		log(".", "");
		_link.log("Testing missing manifest headers.");
		try {
			tb = _context.installBundle(_tcHome + "tb5.jar");
			tb.start();
			h = tb.getHeaders();
			_link.log(h.size() + " headers.");
			// The order of the headers isn't specified, so we'd better sort
			// them.
			for (Enumeration e = sort(h.keys()); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				_link.log(key.toLowerCase() + ": " + h.get(key));
			}
			tb.stop();
			tb.uninstall();
		}
		catch (BundleException e) {
			log("Exception in testing missing manifest headers", "" + e);
			log("NestedException", "" + e.getNestedException());
		}
	}

	/**
	 * Tests extended classpath
	 */
	void testBundleClassPath() {
		//instaniate an object from tbcinner.jar
		log(".", "");
		log(
				"Trying to instansiate a class from a bundle class path declared in the manifest file",
				"");
		try {
			BundleClass dummy = new BundleClass();
			log(
					"We managed to instansiate a a class from the extended bundleclass path",
					"OK");
		}
		catch (Exception e) {
			log(
					"We didn't manage to instansiate a class from the extended bundle class path",
					"FAIL");
			log("Exeption in testBundleClassPath", "FAIL");
			e.printStackTrace();
		}
	}

	/**
	 * Tests that location remains the same after an update
	 */
	void testBundleLocation() {
		Bundle tb;
		try {
			log(".", "");
			log(
					"Tests if bundle location remains unchanged after an bundle update.",
					"");
			tb = _context.installBundle(_tcHome + "tb1.jar");
			String originalLocation = tb.getLocation();
			tb.update();
			if ((tb.getLocation()).equals(originalLocation))
				log("bundle location unchanged after update.", "OK");
			else
				log("bundle location changed after update.", "FAIL");
		}
		catch (Exception e) {
			log("Exception in testBundleLocation", "FAIL");
			e.printStackTrace();
		}
	}

	/**
	 * Tests native code.
	 */
	void testNativeCode() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = _context.installBundle(_tcHome + "tb2.jar");
			try {
				tb.start();
				log(".", "");
				log("Testing Native code:", "Started Ok.");
			}
			catch (BundleException be) {
				log("Native code not started", "" + be);
				reportProcessorOS();
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log("Native code not installed ", "" + be);
			reportProcessorOS();
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code:", "" + ule);
			reportProcessorOS();
		}
	}

	void reportProcessorOS() throws IOException {
		String os = _context.getProperty("org.osgi.framework.os.name");
		String proc = _context.getProperty("org.osgi.framework.processor");
		log("Current os + processor", "osname=" + os + " processor=" + proc);
		log(
				"See for allowed constants: http://membercvs.osgi.org/docs/reference.html",
				null);
	}

	/**
	 * The FrameworkEvent callback, used by the FrameworkListener test.
	 */
	boolean	synced;

	public synchronized void frameworkEvent(FrameworkEvent fe) {
		try {
			switch (fe.getType()) {
				case FrameworkEvent.ERROR :
					log("Testing FrameworkEvent:", fe.getThrowable().getClass()
							+ ".");
					break;
				case FrameworkEvent.STARTED :
					log("Testing FrameworkEvent:", "Ok.");
					break;
			}
		}
		catch (Exception e) { /* Ignore */
		}
		synced = true;
		notify();
	}

	/**
	 * Tests to add a FrameworkListener.
	 */
	synchronized void testFrameworkListener() throws Exception {
		Bundle tb;
		synced = false;
		_context.addFrameworkListener(this);
		log(".", "");
		log("Testing framework event.", "Expecting BundleException");
		tb = _context.installBundle(_tcHome + "tb3.jar");
		tb.start();
		tb.uninstall();
		_context.removeFrameworkListener(this);
		if (!synced)
			wait(10000);
		if (!synced)
			log("Test framework event", "event not received within 10 seconds");
	}

	/**
	 * Tests the file system.
	 */
	void testFileAccess() throws Exception {
		File file;
		PrintWriter out;
		BufferedReader in;
		boolean ok = true;
		String res;
		file = _context.getDataFile("testfile");
		log(".", "");
		if (file != null) {
			out = new PrintWriter(new FileWriter(file));
			out.println("Line 1");
			out.println("Line 2");
			out.println("Line 3");
			out.close();
			log("Testing file access:", "Wrote 3 lines Ok.");
			in = new BufferedReader(new FileReader(file));
			if (!in.readLine().equals("Line 1"))
				ok = false;
			if (!in.readLine().equals("Line 2"))
				ok = false;
			if (!in.readLine().equals("Line 3"))
				ok = false;
			in.close();
			log("Testing file access:", "Read 3 lines Ok.");
			if (file.delete())
				res = "File deleted ok.";
			else
				res = "Delete failed!";
			log("Deleting file:", res);
			try {
				in = new BufferedReader(new FileReader(file));
				res = "File was not gone, Error!";
			}
			catch (FileNotFoundException fnfe) {
				res = "File was gone, Ok.";
			}
			log("Testing file deletion:", res);
		}
		else
			_link.log("Framework lacks filesystem support, no error.");
	}

	/**
	 * Tests double manifest tags.
	 */
	void testDoubleManifestTags() throws Exception {
		Bundle tb = null;
		Dictionary h;
		Enumeration e;
		Object key;
		log(".", "");
		log("Testing double Manifest tags.", "");
		tb = _context.installBundle(_tcHome + "tb4.jar");
		try {
			h = tb.getHeaders();
			log("", "The Import-Package header was " + h.get("Import-Package"));
			tb.uninstall();
		}
		catch (Exception ex) {
			log(
					"got exception while uninstalling test bundle i test double manifest tags",
					"FAIL");
			ex.printStackTrace();
		}
		System.out.println("Uninstalled");
	}

	Enumeration sort(Enumeration e) {
		Vector result = new Vector();
		while (e.hasMoreElements()) {
			String a = e.nextElement() + "";
			result.addElement(a);
		}
		/* do a case insensitive sort */
		Collections.sort(result, new Comparator() {
			public int compare(Object o1, Object o2) {
				String s1 = (String) o1;
				String s2 = (String) o2;
				s1 = s1.toLowerCase();
				s2 = s2.toLowerCase();
				return s1.compareTo(s2);
			}
		});
		return result.elements();
	}

	void testBundleZero() {
		//Bundle(0).update is tested in permission/tc5
		try {
			(_context.getBundle(0)).start();
			log("bundle(0).start returned without Exception", "OK");
		}
		catch (Exception e) {
			log("Exception when excecuting bundle(0).start", "Fail");
			e.printStackTrace();
		}
		try {
			(_context.getBundle(0)).uninstall();
		}
		catch (BundleException e) {
			log("BundleException thrown when excecuting bundle(0).uninstall",
					"OK");
			e.printStackTrace();
		}
		catch (Exception e) {
			log("Unexpected exception when excecuting bundle(0).uninstall",
					"Fail");
			e.printStackTrace();
		}
	}

	String extractNestedMessages(BundleException be) {
		String message = be.getMessage();
		while (be.getNestedException() != null
				&& be.getNestedException() instanceof BundleException) {
			message += " ";
			message += be.getNestedException().getMessage();
			be = (BundleException) be.getNestedException();
		}
		if (be.getNestedException() != null) {
			message += " ";
			message += be.getNestedException().getMessage();
		}
		return message;
	}

	void testEERequirement() {
		String oneAvailableEEOneMatch = "AA/BB";
		String twoAvailableEEsOneMatch = "XX/YY,CC/DD";
		String oneAvailableEENoMatch = "XX/YY";
		String twoAvailableEEsNoMatch = "UU/VV,XX/YY";
		Bundle tb = null;
		try {
			// Install w/o available EE, should fail
			tb = _context.installBundle(_tcHome + "tb7.jar");
			log(
					"Install: No BundleException thrown when EE requirements not fulfilled",
					"Fail");
		}
		catch (BundleException be) {
			log(
					"Install: BundleException thrown when EE requirements not fulfilled.",
					"OK");
			be.printStackTrace();
		}
		try {
			// Set available EE that does not match, install again, should fail
			System.getProperties().put(
					Constants.FRAMEWORK_EXECUTIONENVIRONMENT,
					oneAvailableEENoMatch);
			tb = _context.installBundle(_tcHome + "tb7.jar");
			log(
					"Install: No BundleException thrown when EE requirements not fulfilled",
					"Fail");
		}
		catch (BundleException be) {
			log(
					"Install: BundleException thrown when EE requirements not fulfilled.",
					"OK");
			be.printStackTrace();
		}
		try {
			// Set available EE that matches, install again, should succeed
			System.getProperties().put(
					Constants.FRAMEWORK_EXECUTIONENVIRONMENT,
					oneAvailableEEOneMatch);
			tb = _context.installBundle(_tcHome + "tb7.jar");
			log(
					"Install: No BundleException thrown when EE requirements fulfilled.",
					"OK");
		}
		catch (BundleException be) {
			log(
					"Install: BundleException thrown when EE requirements fulfilled: "
							+ extractNestedMessages(be), "Fail");
		}
		try {
			// Set available EE that does not match, update, should fail
			System.getProperties().put(
					Constants.FRAMEWORK_EXECUTIONENVIRONMENT,
					twoAvailableEEsNoMatch);
			tb.update();
			log(
					"Update: No BundleException thrown when EE requirements not fulfilled.",
					"Fail");
		}
		catch (BundleException be) {
			log(
					"Update: BundleException thrown when EE requirements not fulfilled.",
					"OK");
		}
		try {
			// Set two available EEs with one match, update again, should
			// succeed
			System.getProperties().put(
					Constants.FRAMEWORK_EXECUTIONENVIRONMENT,
					twoAvailableEEsOneMatch);
			tb.update();
			log(
					"Update: No BundleException thrown when EE requirements fulfilled.",
					"OK");
		}
		catch (BundleException be) {
			log(
					"Update: BundleException thrown when EE requirements fulfilled: "
							+ extractNestedMessages(be), "Fail");
		}
		System.getProperties().remove(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		try {
			tb.uninstall();
		}
		catch (Throwable ignored) {
		}
	}
}
