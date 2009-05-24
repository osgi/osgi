/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.junit.div;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.test.cases.framework.div.tb6.BundleClass;
import org.osgi.test.cases.framework.junit.div.Bundle.GetEntry;
import org.osgi.test.cases.framework.junit.div.Bundle.GetEntryPaths;
import org.osgi.test.cases.framework.junit.div.Bundle.GetHeaders;
import org.osgi.test.cases.framework.junit.div.Bundle.GetResource;
import org.osgi.test.cases.framework.junit.div.Bundle.GetResources;
import org.osgi.test.cases.framework.junit.div.Bundle.GetSymbolicName;
import org.osgi.test.cases.framework.junit.div.Bundle.LoadClass;
import org.osgi.test.cases.framework.junit.div.BundleContext.RegisterService;
import org.osgi.test.cases.framework.junit.div.BundleException.GetCause;
import org.osgi.test.cases.framework.junit.div.BundleException.InitCause;
import org.osgi.test.cases.framework.junit.div.Version.CompareTo;
import org.osgi.test.cases.framework.junit.div.Version.Equals;
import org.osgi.test.cases.framework.junit.div.Version.GetMajor;
import org.osgi.test.cases.framework.junit.div.Version.GetMicro;
import org.osgi.test.cases.framework.junit.div.Version.GetMinor;
import org.osgi.test.cases.framework.junit.div.Version.HashCode;
import org.osgi.test.cases.framework.junit.div.Version.InstanceOf;
import org.osgi.test.support.FrameworkEventCollector;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Radio Systems AB
 */
// TODO remove FrameworkListener
public class DivTests extends DefaultTestBundleControl implements
		FrameworkListener {

	// TODO delete
	public static void log(String test, String result) {
		if (result == null)
			result = "";
		log(test + " " + result);
	}

	/**
	 * Logs the manifest headers.
	 */
	public void testManifestHeaders() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb1.jar");
		try {
			tb.start();
			Dictionary h = tb.getHeaders("");
			assertEquals("numeric first char", h.get("5-"));
			assertEquals("org.osgi.test.cases.framework.div.tb1.CheckManifest",
					h.get("bundle-activator"));
			assertEquals("should contain the bundle category", h
					.get("bundle-category"));
			assertEquals("., foo/bar/dummy.jar", h.get("bundle-classpath"));
			assertEquals("info@ericsson.com", h.get("bundle-contactaddress"));
			assertEquals("should contain the bundle copyright", h
					.get("bundle-copyright"));
			assertEquals("Contains the manifest checked by the test case.", h
					.get("bundle-description"));
			assertEquals("http://www.ericsson.com", h.get("bundle-docurl"));
			assertEquals("org.osgi.test.cases.framework.div.tb1", h
					.get("bundle-name"));
			assertEquals("www.ericsson.se", h.get("bundle-updatelocation"));
			assertEquals("Ericsson Radio Systems AB", h.get("bundle-vendor"));
			assertEquals("Improper value for bundle manifest version 2", h
					.get("bundle-version"));
			assertEquals("12                          34", h.get("continue"));
			assertEquals(
					"org.osgi.dummy1;                         version=\"0.0\", org.osgi.dummy2,org.osgi.dummy3;version=\"19.67.34\"",
					h.get("export-package"));
			assertEquals(
					"should contain the exported services, not used by framework",
					h.get("export-service"));
			assertEquals(
					"This bundle is defined by developer and should be ignored by framework",
					h.get("fakeheader"));
			assertEquals(
					"should contain the imported services, not used by framework",
					h.get("import-service"));
			assertEquals("1.0", h.get("manifest-version"));
			assertEquals(
					"xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxxxx xxxxxxEND",
					h.get("max-length"));
			assertEquals("\u00d0\u00de", h.get("unicode-test"));

			tb.stop();
		}
		catch (BundleException e) {
			fail("Exception in manifest headers", e);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests empty manifest headers.
	 */
	public void testMissingManifestHeaders() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb5.jar");
		try {
			tb.start();
			Dictionary h = tb.getHeaders();
			assertEquals(1, h.size());
			assertEquals("1.0", h.get("manifest-version"));
			tb.stop();
		}
		catch (BundleException e) {
			log("Exception in testing missing manifest headers", "" + e);
			log("NestedException", "" + e.getNestedException());
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests extended classpath
	 */
	public void testBundleClassPath() {
		// instantiate an object from tbcinner.jar
		BundleClass dummy = null;
		try {
			dummy = new BundleClass();
		}
		catch (Throwable e) {
			fail(
					"We didn't manage to instansiate a class from the extended bundle class path",
					e);
		}
		assertEquals("different class loader", dummy.getClass()
				.getClassLoader(), this.getClass().getClassLoader());
	}

	/**
	 * Tests that location remains the same after an update
	 */
	public void testBundleLocation() throws Exception {
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb5.jar");
		try {
			String originalLocation = tb.getLocation();
			long originalLastModified = tb.getLastModified();
			tb.update();
			assertEquals("bundle location changed after update.",
					originalLocation, tb.getLocation());
			assertFalse("bundle last modified did not change",
					originalLastModified == tb.getLastModified());
		}
		finally {
			tb.uninstall();
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
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb2.jar");
		try {
			tb.start();
		}
		catch (BundleException be) {
			fail("Native code not installed. " + reportProcessorOS(), be);
		}
		finally {
			tb.uninstall();
		}
	}

	/**
	 * Tests to add a FrameworkListener.
	 */
	public void testFrameworkListener() throws Exception {
		FrameworkEventCollector fec = new FrameworkEventCollector(
				FrameworkEvent.ERROR);
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb3.jar");
		getContext().addFrameworkListener(fec);
		tb.start();
		tb.uninstall();
		List result = fec.getList(1, 10000 * OSGiTestCaseProperties
				.getScaling());
		getContext().removeFrameworkListener(fec);
		assertEquals("No FrameworkEvent received", 1, result.size());
		FrameworkEvent fe = (FrameworkEvent) result.get(0);
		assertEquals("No FrameworkEvent received", tb, fe.getBundle());
		Throwable t = fe.getThrowable();
		assertNotNull(t);
		assertTrue(t instanceof BundleException);
	}

	/**
	 * Tests the file system.
	 */
	public void testFileAccess() throws Exception {
		File file = getContext().getDataFile("testfile");
		if (file == null) {
			log("Framework lacks filesystem support, no error.");
			return;
		}
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.println("Line 1");
		out.println("Line 2");
		out.println("Line 3");
		out.close();
		BufferedReader in = new BufferedReader(new FileReader(file));
		assertEquals("Line 1", in.readLine());
		assertEquals("Line 2", in.readLine());
		assertEquals("Line 3", in.readLine());
		in.close();
		assertTrue(file.delete());
		try {
			in = new BufferedReader(new FileReader(file));
			fail("File was not gone, Error!");
		}
		catch (FileNotFoundException fnfe) {
			// expected
		}
	}

	public void testBundleZero() {
		// Bundle(0).update is tested in permission/tc5
		try {
			getContext().getBundle(0).start();
		}
		catch (BundleException e) {
			fail("bundle(0).start threw Exception", e);
		}
		try {
			getContext().getBundle(0).uninstall();
			fail("bundle(0).uninstall returned without Exception");
		}
		catch (BundleException e) {
			// expected
		}
	}

	// TODO can we get this to work?
	private void testEERequirement() throws Exception {
		final Properties sysProps = System.getProperties();
		final String ee = sysProps
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
		log("EE: " + ee);
		Bundle tb = getContext().installBundle(getWebServer() + "div.tb7a.jar");
		try {
			tb.start();
		}
		catch (BundleException e) {
			//
		}
		finally {
			tb.uninstall();
		}

		tb = getContext().installBundle(getWebServer() + "div.tb7b.jar");
		try {
			tb.start();
		}
		catch (BundleException e) {
			//
		}
		finally {
			tb.uninstall();
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
			tb = getContext().installBundle(getWebServer() + "div.tb12.jar");
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
			tb = getContext().installBundle(getWebServer() + "div.tb15.jar");
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
			tb = getContext().installBundle(getWebServer() + "div.tb16.jar");
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
			tbFragment = getContext().installBundle(
					getWebServer() + "div.tb18.jar");
			tb = getContext().installBundle(getWebServer() + "div.tb17.jar");
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
			tb = getContext().installBundle(getWebServer() + "div.tb19.jar");
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
			tb = getContext().installBundle(getWebServer() + "div.tb20.jar");
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
			tb = getContext().installBundle(getWebServer() + "div.tb21.jar");
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
			tb = getContext().installBundle(getWebServer() + "div.tb22.jar");
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

	private String reportProcessorOS() {
		String os = getContext().getProperty("org.osgi.framework.os.name");
		String proc = getContext().getProperty("org.osgi.framework.processor");
		StringBuffer sb = new StringBuffer();
		sb.append("Current osname=\"").append(os).append("\" processor=\"")
				.append(proc);
		sb
				.append("\". For allowed constants see http://www.osgi.org/Specifications/Reference");
		return sb.toString();
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
	 * Tests double manifest tags.
	 */
	public void testDoubleManifestTags() throws Exception {
		Bundle tb = null;
		Dictionary h;
		log(".", "");
		log("Testing double Manifest tags.", "");
		tb = getContext().installBundle(getWebServer() + "div.tb4.jar");
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
		// try {
		// new GetBundleContext(getContext(), this, getWebServer()).run();
		// }
		// catch (Exception ex) {
		// ex.printStackTrace();
		// log(ex.getMessage(), "Fail");
		// }
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
			new org.osgi.test.cases.framework.junit.div.BundleEvent.Constants(
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
			new org.osgi.test.cases.framework.junit.div.FrameworkEvent.Constants(
					getContext(), getWebServer()).run();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			log(ex.getMessage(), "Fail");
		}
	}

	public void testVersionConstructors() {
		try {
			new org.osgi.test.cases.framework.junit.div.Version.Constructors(
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
			new org.osgi.test.cases.framework.junit.div.Version.Constants(
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

	public static void assertEquals(String message, Comparator comparator,
			List expected, List actual) {
		if (expected.size() != actual.size()) {
			fail(message);
		}

		for (int i = 0, l = expected.size(); i < l; i++) {
			assertEquals(message, 0, comparator.compare(expected.get(i), actual
					.get(i)));
		}
	}

}