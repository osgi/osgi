/*
 * Copyright (c) OSGi Alliance (2018). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.test.cases.framework.junit.wiring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Namespace;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class ImportJavaPackagesTests extends WiringTest {
	
	private static final String	JAVA_LANG	= "java.lang";
	private static final String	JAVA_UTIL	= "java.util";
	private Bundle				testBundle;
	
	
	
	@Override
	public void setUp() throws Exception {
		super.setUp();

	}

	@Override
	public void tearDown() throws Exception {
		if (testBundle != null) {
			testBundle.uninstall();
		}
		super.tearDown();
	}

	private File createBundle(String bundleName, Map<String,String> headers)
			throws IOException {
		Manifest m = new Manifest();
		Attributes attributes = m.getMainAttributes();
		attributes.putValue("Manifest-Version", "1.0");
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			attributes.putValue(entry.getKey(), entry.getValue());
		}
		File file = getContext().getDataFile(bundleName + ".jar");
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(file), m);
		jos.flush();
		jos.close();
		return file;
	}

	public void testExportPackageCannotContainJavaPackages() throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Constants.BUNDLE_MANIFESTVERSION, "2");
		headers.put(Constants.BUNDLE_SYMBOLICNAME, getName());
		headers.put(Constants.EXPORT_PACKAGE, JAVA_LANG);
		File bundle = createBundle(getName(), headers);
		try {
			testBundle = getContext().installBundle(bundle.toURI().toString());
			testBundle.start();
			fail("Failed to test Export-Package header");
		} catch (BundleException e) {
			assertEquals("It should throw a bundle exception of type manifest error", BundleException.MANIFEST_ERROR, e.getType());
		}

	}
	
	
	public void testImportPackageCanContainJavaPackages() throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Constants.BUNDLE_MANIFESTVERSION, "2");
		headers.put(Constants.BUNDLE_SYMBOLICNAME, getName());
		headers.put(Constants.IMPORT_PACKAGE, JAVA_LANG);
		File bundle = createBundle(getName(), headers);
		try {
			testBundle = getContext().installBundle(bundle.toURI().toString());
			testBundle.start();
			Dictionary<String, String> testHeaders = testBundle.getHeaders();
			assertTrue(Constants.IMPORT_PACKAGE + " does not contain the java.* package", testHeaders.get(Constants.IMPORT_PACKAGE).contains(JAVA_LANG));
			List<BundleWire> pkgWires = testBundle.adapt(BundleWiring.class).getRequiredWires(PackageNamespace.PACKAGE_NAMESPACE);
			assertEquals("Wrong number of package requiremens: ", 1, pkgWires.size());
			assertEquals("Wrong package found: " + pkgWires.get(0), JAVA_LANG, pkgWires.get(0).getCapability().getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE));
			assertEquals("Wrong exporter of java package.", getContext().getBundle(Constants.SYSTEM_BUNDLE_LOCATION).adapt(BundleRevision.class), pkgWires.get(0).getProvider());
		} catch (BundleException e) {
			fail("Failed to test Import-Package header");
		} 
	}
	
	
	public void testSystemPackages() throws Exception {

		Bundle systemBundle = getContext().getBundle(Constants.SYSTEM_BUNDLE_LOCATION);
		BundleRevision br = systemBundle.adapt(BundleRevision.class);

		Dictionary<String, String> testHeaders = systemBundle.getHeaders();
		assertTrue(Constants.EXPORT_PACKAGE + " does not contain the java.* package", testHeaders.get(Constants.EXPORT_PACKAGE).contains(JAVA_LANG));
		assertTrue(Constants.EXPORT_PACKAGE + " does not contain the java.* package", testHeaders.get(Constants.EXPORT_PACKAGE).contains(JAVA_UTIL));

		String systemPackages = getContext().getProperty(Constants.FRAMEWORK_SYSTEMPACKAGES);
		assertTrue("System packages should include java.* packages", systemPackages.contains(JAVA_LANG));
		assertTrue("System packages should include java.* packages", systemPackages.contains(JAVA_UTIL));

		List<Capability> capabilities = br.getCapabilities(PackageNamespace.PACKAGE_NAMESPACE);
		int count = 0;
		for(Capability capability: capabilities) {
			if (capability.getAttributes().get(PackageNamespace.PACKAGE_NAMESPACE).toString().startsWith("java.")) {
				count++;
			}
		}
		
		assertTrue("No java.* packages found", count > 0);

		Collection<BundleCapability> bundleCapabilities = frameworkWiring.findProviders(new Requirement() {
			@Override
			public Resource getResource() {
				return null;
			}
			
			@Override
			public String getNamespace() {
				return PackageNamespace.PACKAGE_NAMESPACE;
			}
			
			@Override
			public Map<String,String> getDirectives() {
				return Collections.singletonMap(Namespace.REQUIREMENT_FILTER_DIRECTIVE, "("+PackageNamespace.PACKAGE_NAMESPACE+"=java.*)");
			}
			
			@Override
			public Map<String,Object> getAttributes() {
				return Collections.emptyMap();
			}
		});
		
		assertFalse("Capabities should be non empty",bundleCapabilities.isEmpty());
		
		for(BundleCapability cap: bundleCapabilities) {
			assertEquals("java.* capabilities should only come from system bundle", br, cap.getRevision());
		}
	}
}
