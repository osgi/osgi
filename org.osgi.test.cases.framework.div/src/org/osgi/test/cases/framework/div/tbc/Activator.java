/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.div.tbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.test.cases.framework.div.tb6.BundleClass;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetBundleContext;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetEntry;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetEntryPaths;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetHeaders;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetResource;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetResources;
import org.osgi.test.cases.framework.div.tbc.Bundle.GetSymbolicName;
import org.osgi.test.cases.framework.div.tbc.Bundle.LoadClass;
import org.osgi.test.cases.framework.div.tbc.BundleContext.RegisterService;
import org.osgi.test.cases.framework.div.tbc.BundleException.GetCause;
import org.osgi.test.cases.framework.div.tbc.BundleException.InitCause;
import org.osgi.test.cases.framework.div.tbc.Version.CompareTo;
import org.osgi.test.cases.framework.div.tbc.Version.Equals;
import org.osgi.test.cases.framework.div.tbc.Version.GetMajor;
import org.osgi.test.cases.framework.div.tbc.Version.GetMicro;
import org.osgi.test.cases.framework.div.tbc.Version.GetMinor;
import org.osgi.test.cases.framework.div.tbc.Version.HashCode;
import org.osgi.test.cases.framework.div.tbc.Version.InstanceOf;
import org.osgi.test.cases.util.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Radio Systems AB
 */
public class Activator extends DefaultTestBundleControl implements FrameworkListener {
	static final int			TESTS		= 5;
	String[] methods		= new String[] {
			"testManifestHeaders", "testMissingManifestHeaders",
			"testBundleClassPath", "testNativeCode", "testFrameworkListener",
			"testFileAccess", "testBundleZero", "testEERequirement",
			"testNativeCodeFilterOptional", "testNativeCodeFilterNoOptional",
			"testNativeCodeFilterAlias", "testNativeCodeFragment",
			"testNativeCodeLanguage", "testNativeCodeLanguageSuccess",
			"testNativeCodeVersion", "testNativeCodeVersionSuccess",
			"testBundleEventConstants", "testBundleGetEntryPath",
			"testBundleGetEntryPaths", "testBundleGetResources",
			"testBundleGetResource", "testBundleGetSymbolicName",
			"testBundleGetBundleContext",
			"testBundleHashCode", "testBundleLoadClass",
			"testBundleExceptionGetCause", "testBundleExceptionInitCause",
			"testFrameworkEventConstants",
			"testVersionConstructors", "testVersionEquals",
			"testVersionGetMajor", "testVersionGetMinor",
			"testVersionGetMicro", "testVersionCompareTo",
			"testVersionInstanceOf", "testBundleGetHeaders",
			"testBundleContextRegisterService"};

	protected void prepare() throws Exception {
		log("Test bundle control started Ok.");
	}


	public void log(String test, String result) {
		if (result == null)
			result = "";
		log(test + " " + result);
	}


	public String [] getMethods() { return methods; }


	/**
	 * Logs the manifest headers.
	 */
	public void testManifestHeaders() throws Exception {
		Bundle tb;
		Dictionary h;
		log(".", "");
		log("Testing Manifest header syntax.");
		try {
			tb = getContext().installBundle(getWebServer() + "tb1.jar");
			tb.start();
			h = tb.getHeaders();
			log(h.size() + " headers.");
			// The order of the headers isn't specified, so we'd better sort
			// them.
			for (Enumeration e = sort(h.keys()); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				if (!key.equalsIgnoreCase("import-package")) {
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
					log(key.toLowerCase() + ": " + result);
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
	public void testMissingManifestHeaders() throws Exception {
		Bundle tb;
		Dictionary h;
		log(".", "");
		log("Testing missing manifest headers.");
		try {
			tb = getContext().installBundle(getWebServer() + "tb5.jar");
			tb.start();
			h = tb.getHeaders();
			log(h.size() + " headers.");
			// The order of the headers isn't specified, so we'd better sort
			// them.
			for (Enumeration e = sort(h.keys()); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				log(key.toLowerCase() + ": " + h.get(key));
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
	public void testBundleClassPath() {
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
	public void testBundleLocation() {
		Bundle tb;
		try {
			log(".", "");
			log(
					"Tests if bundle location remains unchanged after an bundle update.",
					"");
			tb = getContext().installBundle(getWebServer() + "tb1.jar");
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
	 * Tests basic native code invocation.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCode() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb2.jar");
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

	/**
	 * Tests native code selection filter. The bundle should be loaded even if
	 * no native code clause matches the selection filter, since there's an
	 * optional clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterOptional() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb12.jar");
			try {
				tb.start();
				log(".", "");
				log("Testing Native code selection filter optional:",
						"Started Ok.");
			}
			catch (BundleException be) {
				log(
						"Error: Selection filter should not match any native code clause ",
						"" + be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log("Bundle not installed:", "" + be);
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code:", "" + ule);
		}
	}

	/**
	 * Tests native code selection filter. The bundle should NOT be loaded if no
	 * native code clause matches the selection filter, since there's no
	 * optional clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterNoOptional() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb15.jar");
			log("Error: Bundle should NOT be loaded", "");
			try {
				tb.start();
			}
			catch (BundleException be) {
				log("Error starting bundle ", "" + be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log(".", "");
			log("Testing Native code selection filter no optional:",
					"Not loaded Ok.");
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code with no optional clause:", "" + ule);
		}
	}

	/**
	 * Tests native code selection filter. The bundle should only be loaded if
	 * at least one native code clause matches the selection filter, since
	 * there's no optional clause present (*). This test also checks if the new
	 * osname alias (win32) matches properly (OSGi R4).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFilterAlias() throws Exception {
		Bundle tb;
		String res;
		try {
			Properties props = System.getProperties();
			props.put("org.osgi.framework.windowing.system", "xyz");
			System.setProperties(props);
			tb = getContext().installBundle(getWebServer() + "tb16.jar");
			try {
				tb.start();
				log(".", "");
				log(
						"Testing Native code selection filter with new osname alias:",
						"Started Ok.");
			}
			catch (BundleException be) {
				log("Error starting bundle with osname alias ", "" + be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log("Bundle with osname alias not installed:", "" + be);
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code with osname alias:", "" + ule);
		}
	}

	/**
	 * Tests native code from a fragment bundle. The native code should be
	 * loaded from a fragment bundle of the host bundle.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeFragment() throws Exception {
		Bundle tb;
		Bundle tbFragment;
		String res;
		try {
			tbFragment = getContext().installBundle(getWebServer() + "tb18.jar");
			tb = getContext().installBundle(getWebServer() + "tb17.jar");
			try {
				tb.start();
				log(".", "");
				log("Testing Native code from a fragment bundle:",
						"Started Ok.");
			}
			catch (BundleException be) {
				log("Error loading native code from a fragment bundle ", ""
						+ be);
			}
			tb.uninstall();
			tbFragment.uninstall();
		}
		catch (BundleException be) {
			log(
					"Error loading bundle with native code from a fragment bundle ",
					"" + be);
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code from a fragment bundle:", "" + ule);
		}
	}

	/**
	 * Tests native code language filter. The bundle should NOT be loaded if no
	 * native code clause matches the os language, since there's no optional
	 * clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeLanguage() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb19.jar");
			log("Error: Bundle should NOT be loaded:",
					"language should not match");
			try {
				tb.start();
			}
			catch (BundleException be) {
				log("Error starting native code language bundle ", "" + be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log(".", "");
			log("Testing Native code os language:", "Not loaded Ok.");
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code os language:", "" + ule);
		}
	}

	/**
	 * Tests native code language filter. The bundle should be loaded since all
	 * valid languages are included in the filter.
	 * 
	 * @see http://ftp.ics.uci.edu/pub/ietf/http/related/iso639.txt for valid
	 *      language codes.
	 *      
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeLanguageSuccess() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb20.jar");
			try {
				tb.start();
				log(".", "");
				log("Testing Native code successful os language:",
						"Started Ok.");
			}
			catch (BundleException be) {
				log("Error starting positive native code language bundle ", ""
						+ be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log("Error loading positive native code language bundle:", "" + be);
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing positive native code os language:", "" + ule);
		}
	}

	/**
	 * Tests native code os version. The bundle should NOT be loaded if no
	 * native code clause matches the os version range, since there's no
	 * optional clause present (*).
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeVersion() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb21.jar");
			log("Error: Bundle should NOT be loaded", "os version out of range");
			try {
				tb.start();
			}
			catch (BundleException be) {
				log("Error starting native code version bundle", "" + be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log(".", "");
			log("Testing Native code osversion:", "Not loaded Ok.");
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing native code os version:", "" + ule);
		}
	}

	/**
	 * Tests successful native code os version. The bundle should be loaded
	 * since the version range should contain all valid os versions.
	 * 
	 * @spec BundleContext.installBundle(String)
	 * @spec Bundle.start()
	 * @spec Bundle.uninstall()
	 */
	public void testNativeCodeVersionSuccess() throws Exception {
		Bundle tb;
		String res;
		try {
			tb = getContext().installBundle(getWebServer() + "tb22.jar");
			try {
				tb.start();
				log(".", "");
				log("Testing Native code successful osversion:", "Started Ok.");
			}
			catch (BundleException be) {
				log("Error starting success native code version bundle", ""
						+ be);
			}
			tb.uninstall();
		}
		catch (BundleException be) {
			log("Error installing success native code osversion:", "" + be);
		}
		catch (UnsatisfiedLinkError ule) {
			log("Testing success native code os version:", "" + ule);
		}
	}

	void reportProcessorOS() throws IOException {
		String os = getContext().getProperty("org.osgi.framework.os.name");
		String proc = getContext().getProperty("org.osgi.framework.processor");
		log("Current os + processor", "osname=" + os + " processor=" + proc);
		log(
				"See for allowed constants: http://www2.osgi.org/Specifications/Reference",
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
	synchronized public void testFrameworkListener() throws Exception {
		Bundle tb;
		synced = false;
		getContext().addFrameworkListener(this);
		log(".", "");
		log("Testing framework event.", "Expecting BundleException");
		tb = getContext().installBundle(getWebServer() + "tb3.jar");
		tb.start();
		tb.uninstall();
		getContext().removeFrameworkListener(this);
		if (!synced)
			wait(10000);
		if (!synced)
			log("Test framework event", "event not received within 10 seconds");
	}

	/**
	 * Tests the file system.
	 */
	public void testFileAccess() throws Exception {
		File file;
		PrintWriter out;
		BufferedReader in;
		boolean ok = true;
		String res;
		file = getContext().getDataFile("testfile");
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
			log("Framework lacks filesystem support, no error.");
	}

	/**
	 * Tests double manifest tags.
	 */
	public void testDoubleManifestTags() throws Exception {
		Bundle tb = null;
		Dictionary h;
		log(".", "");
		log("Testing double Manifest tags.", "");
		tb = getContext().installBundle(getWebServer() + "tb4.jar");
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

	public void testBundleZero() {
		//Bundle(0).update is tested in permission/tc5
		try {
			(getContext().getBundle(0)).start();
			log("bundle(0).start returned without Exception", "OK");
		}
		catch (Exception e) {
			log("Exception when excecuting bundle(0).start", "Fail");
			e.printStackTrace();
		}
		try {
			(getContext().getBundle(0)).uninstall();
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

	public void testEERequirement() {
		String oneAvailableEEOneMatch = "AA/BB";
		String twoAvailableEEsOneMatch = "XX/YY,CC/DD";
		String oneAvailableEENoMatch = "XX/YY";
		String twoAvailableEEsNoMatch = "UU/VV,XX/YY";
		String originalEE = System.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		Bundle tb = null;
		try {
			// Install w/o available EE, should fail
			tb = getContext().installBundle(getWebServer() + "tb7.jar");
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
			tb = getContext().installBundle(getWebServer() + "tb7.jar");
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
			tb = getContext().installBundle(getWebServer() + "tb7.jar");
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
		if (originalEE == null) { //restore original EE value
			System.getProperties().remove(	
					Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		}
		else {
			System.getProperties().put(	
					Constants.FRAMEWORK_EXECUTIONENVIRONMENT,
					originalEE);
		}
		try {
			tb.uninstall();
		}
		catch (Throwable ignored) {
		}
	}

	public void testBundleGetEntryPath() {
		try {
			new GetEntry(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleGetEntryPaths() {
		try {
			new GetEntryPaths(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleGetResource() {
		try {
			new GetResource(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleGetResources() {
		try {
			new GetResources(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleGetSymbolicName() {
		try {
			new GetSymbolicName(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleGetBundleContext() {
		try {
			new GetBundleContext(getContext(), this, getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}
	
	public void testBundleHashCode() {
		try {
			new HashCode(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleLoadClass() {
		try {
			new LoadClass(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleEventConstants() {
		try {
			new org.osgi.test.cases.framework.div.tbc.BundleEvent.Constants(
					getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleExceptionGetCause() {
		try {
			new GetCause(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testBundleExceptionInitCause() {
		try {
			new InitCause(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testFrameworkEventConstants() {
		try {
			new org.osgi.test.cases.framework.div.tbc.FrameworkEvent.Constants(
					getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionConstructors() {
		try {
			new org.osgi.test.cases.framework.div.tbc.Version.Constructors(
					getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionEquals() {
		try {
			new Equals(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionGetMajor() {
		try {
			new GetMajor(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionGetMinor() {
		try {
			new GetMinor(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionGetMicro() {
		try {
			new GetMicro(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionCompareTo() {
		try {
			new CompareTo(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionInstanceOf() {
		try {
			new InstanceOf(getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionConstantsValues() {
		try {
			new org.osgi.test.cases.framework.div.tbc.Version.Constants(
					getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	/**
	 * Tests localization of manifest headers.
	 */
	public void testBundleGetHeaders() {
		try {
			(new GetHeaders(getContext(), getWebServer())).run();
		}
		catch (Exception e) {
			e.printStackTrace();
			log("Error.", e.getMessage());

		}
	}

	/**
	 * Tests service registration.
	 */
	public void testBundleContextRegisterService() {
		try {
			(new RegisterService(getContext(), getWebServer())).run();
		}
		catch (Exception e) {
			e.printStackTrace();
			log("Error.", e.getMessage());

		}
	}
	
}