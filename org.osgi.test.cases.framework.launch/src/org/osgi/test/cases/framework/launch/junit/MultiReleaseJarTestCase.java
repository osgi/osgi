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
package org.osgi.test.cases.framework.launch.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.namespace.PackageNamespace;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Namespace;
import org.osgi.test.support.sleep.Sleep;

public class MultiReleaseJarTestCase extends LaunchTest {

	private final static String	RNF		= "RNF";
	private final static String	CNFE	= "CNFE";
	private final static String	defaultVersion		= "100";

	private File mrJarBundle;
	private Bundle mrBundle;
	private Framework			framework;
	private static final String	PROJECT_NAME	= "org.osgi.test.cases.framework.launch";
	private Map<String,String>	configuration;
	private Map<String,String>	originalProperties	= new HashMap<String,String>();


	@Override
	public void setUp() throws Exception {
		super.setUp();
		for (String prop : getJavaVersionConfiguration().keySet()) {
			originalProperties.put(prop, System.getProperty(prop)); 
		}
		configuration = getFrameworkConfiguration(defaultVersion, true);
		framework = createFramework(configuration);
		startFramework(framework);
		mrJarBundle = createMRJarBundle();
		mrBundle = framework.getBundleContext().installBundle(mrJarBundle.toURI().toString());
		mrBundle.start();
	}

	@Override
	public void tearDown() throws Exception {
		stopFramework(framework);
		super.tearDown();
		for (Entry<String,String> property : originalProperties.entrySet()) {
			System.setProperty(property.getKey(), property.getValue());
		}
	}

	private Map<String,String> getJavaVersionConfiguration() {
		return getJavaVersionConfiguration("");
	}

	private Map<String,String> getJavaVersionConfiguration(String version) {
		Map<String,String> properties = new HashMap<String,String>();
		Properties fwproperties = System.getProperties();

		for (Object propName : fwproperties.keySet()) {
			if (propName.toString()
					.startsWith(PROJECT_NAME + ".java.version." + version)) {
				String property = System.getProperty(propName.toString());
				if (property != null) {
					for (StringTokenizer tok = new StringTokenizer(property,
							","); tok.hasMoreTokens();) {
						String fwproperty = tok.nextToken();
						StringTokenizer equaltok = new StringTokenizer(
								fwproperty.trim(), "=");
						String name = equaltok.nextToken().trim();
						String value = null;
						try {
							value = URLDecoder.decode(
									equaltok.nextToken().trim(), "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// Valid Java implementations must support UTF-8
							throw new AssertionError();
						}
						properties.put(name, value);
					}
				}
			}
		}
		return properties;
	}

	private Map<String,String> getFrameworkConfiguration(String version, boolean delete) {
		Map<String,String> properties = getJavaVersionConfiguration(version);

		for (Entry<String,String> property : properties.entrySet()) {
			System.setProperty(property.getKey(), property.getValue());
		}
		properties.put(Constants.FRAMEWORK_STORAGE,
				getStorageArea(getName(), delete).getAbsolutePath());
		return properties;

	}

	private File createMRJarBundle() throws Exception {

		Bundle base = installBundle(framework, "/mrjars.tb1.jar");
		File mrJarBundle = framework.getBundleContext().getDataFile("mrJarBundleTest.jar");
		if (mrJarBundle.exists()) {
			return mrJarBundle;
		}
		File classpathMrJar = framework.getBundleContext().getDataFile("classpathMrJar.jar");

		Map<String, String> bundleHeaders = new LinkedHashMap<String, String>();
		bundleHeaders.put(Constants.BUNDLE_MANIFESTVERSION, "2");
		bundleHeaders.put(Constants.BUNDLE_SYMBOLICNAME, "mrBundle");
		bundleHeaders.put(Constants.BUNDLE_VERSION, "1.0.0");
		bundleHeaders.put(Constants.IMPORT_PACKAGE, "pkgbase");
		bundleHeaders.put(Constants.REQUIRE_CAPABILITY, "capbase");
		bundleHeaders.put(Constants.EXPORT_PACKAGE, "pkgbase, pkg8, pkg99, pkg100, pkg101");
		bundleHeaders.put(Constants.PROVIDE_CAPABILITY, "capbase, cap8, cap99, cap100, cap101");
		bundleHeaders.put(Constants.BUNDLE_CLASSPATH, "., " + classpathMrJar.getName() + ", classPathDir");

		Map<String, byte[]> bundleEntries = new LinkedHashMap<String, byte[]>();

		bundleEntries.put("org/", null);
		bundleEntries.put("org/osgi/", null);
		bundleEntries.put("org/osgi/test/", null);
		bundleEntries.put("org/osgi/test/cases/", null);
		bundleEntries.put("org/osgi/test/cases/framework/", null);
		bundleEntries.put("org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestClassBase.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClassBase.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestClass8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass8.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestClass99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass99.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestClass100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass100.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestClass101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass101.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestService.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestService.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestService99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestService99.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/TestServiceBase.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestServiceBase.class", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt", base));
		bundleEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt", base));

		bundleEntries.put("META-INF/services/", null);
		bundleEntries.put("META-INF/services/org.osgi.test.cases.framework.launch.mrjars.test.TestService", "org.osgi.test.cases.framework.launch.mrjars.test.TestServiceBase".getBytes("UTF-8"));
		bundleEntries.put("META-INF/versions/", null);
		bundleEntries.put("META-INF/versions/8/", null);
		bundleEntries.put("META-INF/versions/8/org/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/test/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/test/cases/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("META-INF/versions/8/test/TestClass8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass8.class", base, new byte[] {'0', '0', '8'}));
		bundleEntries.put("META-INF/versions/8/test/TestClassAdd8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd8.class", base));
		bundleEntries.put("META-INF/versions/8/test/testResource8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt", base, new byte[] {'0', '0', '8'}));
		bundleEntries.put("META-INF/versions/8/test/testResourceAdd8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt", base));
		
		
		bundleEntries.put("META-INF/versions/99/", null);
		bundleEntries.put("META-INF/versions/99/META-INF/", null);
		bundleEntries.put("META-INF/versions/99/META-INF/addedFor99.txt", "added for 99".getBytes("UTF-8"));
		bundleEntries.put("META-INF/versions/99/META-INF/addedDirFor99/", null);
		bundleEntries.put("META-INF/versions/99/META-INF/addedDirFor99/addedFor99.txt", "added for 99".getBytes("UTF-8"));
		bundleEntries.put("META-INF/versions/99/META-INF/services/", null);
		bundleEntries.put("META-INF/versions/99/META-INF/services/test.TestService", "test.TestService99".getBytes("UTF-8"));
		bundleEntries.put("META-INF/versions/99/org/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/TestClass99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass99.class", base, new byte[] {'0', '9', '9'}));
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd99.class", base));
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt", base, new byte[] {'0', '9', '9'}));
		bundleEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt", base));
		
		
		bundleEntries.put("META-INF/versions/100/", null);
		bundleEntries.put("META-INF/versions/100/org/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/TestClass100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass100.class", base, new byte[] {'1', '0', '0'}));
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd100.class", base));
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt", base, new byte[] {'1', '0', '0'}));
		bundleEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd100.txt", base));
		
		bundleEntries.put("META-INF/versions/101/", null);
		bundleEntries.put("META-INF/versions/101/org/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/TestClass101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClass101.class", base, new byte[] {'1', '0', '1'}));
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/TestClassAdd101.class", base));
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt", base, new byte[] {'1', '0', '1'}));
		bundleEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd101.txt", base));

		bundleEntries.put("META-INF/versions/8/OSGI-INF/", null);
		bundleEntries.put("META-INF/versions/8/OSGI-INF/MANIFEST.MF", getBytes("org/osgi/test/cases/framework/launch/mrjars/manifests/manifest8.mf", base));
		bundleEntries.put("META-INF/versions/99/OSGI-INF/", null);
		bundleEntries.put("META-INF/versions/99/OSGI-INF/MANIFEST.MF", getBytes("org/osgi/test/cases/framework/launch/mrjars/manifests/manifest99.mf", base));
		bundleEntries.put("META-INF/versions/100/OSGI-INF/", null);
		bundleEntries.put("META-INF/versions/100/OSGI-INF/MANIFEST.MF", getBytes("org/osgi/test/cases/framework/launch/mrjars/manifests/manifest100.mf", base));
		bundleEntries.put("META-INF/versions/101/OSGI-INF/", null);
		bundleEntries.put("META-INF/versions/101/OSGI-INF/MANIFEST.MF", getBytes("org/osgi/test/cases/framework/launch/mrjars/manifests/manifest101.mf", base));

		Map<String, byte[]> classPathJarEntries = new LinkedHashMap<String, byte[]>();
		classPathJarEntries.put("org/", null);
		classPathJarEntries.put("org/osgi/", null);
		classPathJarEntries.put("org/osgi/test/", null);
		classPathJarEntries.put("org/osgi/test/cases/", null);
		classPathJarEntries.put("org/osgi/test/cases/framework/", null);
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/", null);
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/", null);
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/", null);
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/", null);
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassBase.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassBase.class", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass8.class", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass99.class", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass100.class", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass101.class", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceBase.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceBase.txt", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt", base));
		classPathJarEntries.put("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt", base));

		classPathJarEntries.put("META-INF/versions/", null);
		classPathJarEntries.put("META-INF/versions/8/", null);
		classPathJarEntries.put("META-INF/versions/8/org/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub/", null);
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass8.class", base, new byte[] {'0', '0', '8'}));
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd8.class", base));
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt", base, new byte[] {'0', '0', '8'}));
		classPathJarEntries.put("META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd8.txt", base));

		classPathJarEntries.put("META-INF/versions/99/", null);
		classPathJarEntries.put("META-INF/versions/99/org/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub/", null);
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass99.class", base, new byte[] {'0', '9', '9'}));
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd99.class", base));
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt", base, new byte[] {'0', '9', '9'}));
		classPathJarEntries.put("META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd99.txt", base));

		classPathJarEntries.put("META-INF/versions/100/", null);
		classPathJarEntries.put("META-INF/versions/100/org/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub/", null);
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass100.class", base, new byte[] {'1', '0', '0'}));
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd100.class", base));
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt", base, new byte[] {'1', '0', '0'}));
		classPathJarEntries.put("META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd100.txt", base));

		classPathJarEntries.put("META-INF/versions/101/", null);
		classPathJarEntries.put("META-INF/versions/101/org/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub/", null);
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClass101.class", base, new byte[] {'1', '0', '1'}));
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/TestClassAdd101.class", base));
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt", base, new byte[] {'1', '0', '1'}));
		classPathJarEntries.put("META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd101.txt", base));

		createMRJar(classpathMrJar, Collections.<String, String> emptyMap(), classPathJarEntries);
		bundleEntries.put(classpathMrJar.getName(),
				getBytes(new FileInputStream(classpathMrJar), -1, 4000));

		// This will not be required by the spec, but equinox does support exploded inner jars in a bundle
		bundleEntries.put("classPathDir/", null);
		bundleEntries.put("classPathDir/org/", null);
		bundleEntries.put("classPathDir/org/osgi/", null);
		bundleEntries.put("classPathDir/org/osgi/test/", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2", null);
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassBase.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassBase.class", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass8.class", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass99.class", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass100.class", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass101.class", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceBase.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceBase.txt", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt", base));
		bundleEntries.put("classPathDir/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt", base));

		String classPathDirManifest = //
				"Manifest-Version: 1\n" + //
						"Multi-Release: true\n\n";
		bundleEntries.put("classPathDir/META-INF/", null);
		bundleEntries.put("classPathDir/META-INF/MANIFEST.MF", classPathDirManifest.getBytes(Charset.forName("UTF-8")));
		bundleEntries.put("classPathDir/META-INF/versions/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub2/", null);
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass8.class", base, new byte[] {'0', '0', '8'}));
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd8.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd8.class", base));
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt", base, new byte[] {'0', '0', '8'}));
		bundleEntries.put("classPathDir/META-INF/versions/8/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd8.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd8.txt", base));

		bundleEntries.put("classPathDir/META-INF/versions/99/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub2/", null);
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass99.class", base, new byte[] {'0', '9', '9'}));
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd99.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd99.class", base));
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt", base, new byte[] {'0', '9', '9'}));
		bundleEntries.put("classPathDir/META-INF/versions/99/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd99.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd99.txt", base));

		bundleEntries.put("classPathDir/META-INF/versions/100/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub2/", null);
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass100.class", base, new byte[] {'1', '0', '0'}));
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd100.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd100.class", base));
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt", base, new byte[] {'1', '0', '0'}));
		bundleEntries.put("classPathDir/META-INF/versions/100/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd100.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd100.txt", base));

		bundleEntries.put("classPathDir/META-INF/versions/101/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub2/", null);
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClass101.class", base, new byte[] {'1', '0', '1'}));
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd101.class", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/TestClassAdd101.class", base));
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt", base, new byte[] {'1', '0', '1'}));
		bundleEntries.put("classPathDir/META-INF/versions/101/org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd101.txt", getBytes("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd101.txt", base));

		createMRJar(mrJarBundle, bundleHeaders, bundleEntries);
		return mrJarBundle;
	}

	static void createMRJar(File file, Map<String, String> headers, Map<String, byte[]> entries) throws IOException {
		Manifest m = new Manifest();
		Attributes attributes = m.getMainAttributes();
		attributes.putValue("Manifest-Version", "1.0");
		attributes.putValue("Multi-Release", "true");
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			attributes.putValue(entry.getKey(), entry.getValue());
		}
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(file), m);
		if (entries != null) {
			for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
				jos.putNextEntry(new JarEntry(entry.getKey()));
				if (entry.getValue() != null) {
					jos.write(entry.getValue());
				}
				jos.closeEntry();
			}
		}
		jos.flush();
		jos.close();
	}

	private byte[] getBytes(String path, Bundle b) throws IOException {
		return getBytes(path, b, null);
	}

	private byte[] getBytes(String path, Bundle b, byte[] replace) throws IOException {
		URL entry = b.getEntry(path);
		if (entry == null) {
			throw new FileNotFoundException("No entry found for: " + path);
		}
		byte[] result = getBytes(entry.openStream(), -1, 4000);

		if (replace != null) {
			for (int i = 0; i < result.length - 2; i++) {
				if (result[i] == 'X' && result[i + 1] == 'X' && result[i + 2] == 'X') {
					result[i] = replace[0];
					result[i + 1] = replace[1];
					result[i + 2] = replace[2];
				}
			}
		}
		return result;
	}

	private byte[] getBytes(InputStream in, int length, int BUF_SIZE) throws IOException {
		byte[] classbytes;
		int bytesread = 0;
		int readcount;
		try {
			if (length > 0) {
				classbytes = new byte[length];
				for (; bytesread < length; bytesread += readcount) {
					readcount = in.read(classbytes, bytesread,
							length - bytesread);
					if (readcount <= 0) /* if we didn't read anything */
						break; /* leave the loop */
				}
			} else /* does not know its own length! */ {
				length = BUF_SIZE;
				classbytes = new byte[length];
				readloop: while (true) {
					for (; bytesread < length; bytesread += readcount) {
						readcount = in.read(classbytes, bytesread,
								length - bytesread);
						if (readcount <= 0) /* if we didn't read anything */
							break readloop; /* leave the loop */
					}
					byte[] oldbytes = classbytes;
					length += BUF_SIZE;
					classbytes = new byte[length];
					System.arraycopy(oldbytes, 0, classbytes, 0, bytesread);
				}
			}
			if (classbytes.length > bytesread) {
				byte[] oldbytes = classbytes;
				classbytes = new byte[bytesread];
				System.arraycopy(oldbytes, 0, classbytes, 0, bytesread);
			}
		} finally {
			try {
				in.close();
			} catch (IOException ee) {
				// nothing to do here
			}
		}
		return classbytes;
	}


	public void testMultiReleaseClassLoad() throws Exception {
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassBase", false));
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClass8", false));
		assertEquals("Wrong class.",  CNFE, loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassAdd8", true));
		assertEquals("Wrong class.", "BASE099", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClass99", false));
		assertEquals("Wrong class.", "ADD099" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassAdd99", true));
		assertEquals("Wrong class.", "BASE100", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClass100", false));
		assertEquals("Wrong class.", "ADD100" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassAdd100", true));
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClass101", false));
		assertEquals("Wrong class.",  CNFE, loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassAdd101", true));

		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClassBase", false));
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClass8", false));
		assertEquals("Wrong class.", CNFE, loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassAdd8", true));
		assertEquals("Wrong class.", "BASE099" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClass99", false));
	    assertEquals("Wrong class.", "ADD099" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClassAdd99", true));
		assertEquals("Wrong class.", "BASE100" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClass100", false));
		assertEquals("Wrong class.", "ADD100" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClassAdd100", true));
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClass101", false));
		assertEquals("Wrong class.",  CNFE, loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub.TestClassAdd101", true));

		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClassBase", false));
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClass8", false));
		assertEquals("Wrong class.", CNFE, loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassAdd8", true));
		assertEquals("Wrong class.", "BASE099" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClass99", false));
		assertEquals("Wrong class.", "ADD099" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClassAdd99", true));
		assertEquals("Wrong class.", "BASE100" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClass100", false));
		assertEquals("Wrong class.", "ADD100" , loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClassAdd100", true));
		assertEquals("Wrong class.", "BASEXXX", loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClass101", false));
		assertEquals("Wrong class.",  CNFE, loadClass("org.osgi.test.cases.framework.launch.mrjars.test.sub2.TestClassAdd101", true));
	}

	private String loadClass(String name, boolean cnfeExpected) throws Exception {
		try {
			return mrBundle.loadClass(name).getConstructor().newInstance().toString();
		} catch (ClassNotFoundException e) {
			if (cnfeExpected) {
				return CNFE;
			}
			throw e;
		}
	}


	public void testMultiReleaseGetResource() throws Exception {
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE 099" , readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt"));
		assertEquals("Wrong resource.", "ADD 099", readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE 100", readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt"));
		assertEquals("Wrong resource.", "ADD 100", readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd101.txt"));

		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE 099", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt"));
		assertEquals("Wrong resource.", "ADD 099", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE 100", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt"));
		assertEquals("Wrong resource.", "ADD 100", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd101.txt"));

		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, readResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE 099", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt"));
		assertEquals("Wrong resource.", "ADD 099" , readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE 100", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt"));
		assertEquals("Wrong resource.", "ADD 100", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, readResource("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd101.txt"));
	}
	
	private String readResource(String name) throws Exception {
		BundleWiring wiring = mrBundle.adapt(BundleWiring.class);
		assertNotNull("No bundle wiring!", wiring);
		URL url = wiring.getClassLoader().getResource(name);
		String result = readURL(url);

		int lastSlash = name.lastIndexOf('/');
		Collection<String> resourcePaths = wiring.listResources(name.substring(0, lastSlash + 1), name.substring(lastSlash + 1), 0);
		if (result == RNF) {
			if (!resourcePaths.isEmpty()) {
				fail("listResources found path for '" + name + "'");
			}
		} else {
			assertEquals("Found too many resource paths for '" + name + "'", 1, resourcePaths.size());
			assertEquals("Wrong path listed.", name, resourcePaths.iterator().next());
			assertURLCopy(result, url);
		}
		return result;
	}

	private void assertURLCopy(String expected, URL url) throws Exception {
		Class<?> testClassBase = mrBundle.loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestClassBase");
		URL copy = (URL) testClassBase.getDeclaredMethod("createURL", String.class).invoke(null, url.toExternalForm());
		String copyResult = readURL(copy);
		assertEquals(expected, copyResult);
	}

	private String readURL(URL url) throws IOException {
		if (url == null) {
			return RNF;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		try {
			String line = br.readLine();
			return line;
		} finally {
			br.close();
		}
	}

	
	public void testMultiReleaseGetEntry() throws Exception {
		assertEquals("Wrong resource.", "RESOURCE XXX", getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt"));
		assertEquals("Wrong resource.",  RNF, getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt"));
		assertEquals("Wrong resource.",  RNF, getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, getEntry("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd101.txt"));
	}

	private String getEntry(String path) throws Exception {
		URL url = mrBundle.getEntry(path);
		return readURL(url);
	}
	
	
	public void testMultiReleaseGetResources() throws Exception {
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE 099", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt"));
		assertEquals("Wrong resource.", "ADD 099", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE 100", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt"));
		assertEquals("Wrong resource.", "ADD 100", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd101.txt"));

		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE 099", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt"));
		assertEquals("Wrong resource.", "ADD 099", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE 100", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt"));
		assertEquals("Wrong resource.", "ADD 100", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd101.txt"));

		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceBase.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt"));
		assertEquals("Wrong resource.",  RNF, readResources("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd8.txt"));
		assertEquals("Wrong resource.", "RESOURCE 099", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt"));
		assertEquals("Wrong resource.", "ADD 099", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd99.txt"));
		assertEquals("Wrong resource.", "RESOURCE 100", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt"));
		assertEquals("Wrong resource.", "ADD 100", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd100.txt"));
		assertEquals("Wrong resource.", "RESOURCE XXX", readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt"));
		assertEquals("Wrong resource.",  RNF, readResources("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd101.txt"));
	}

	private String readResources(String name) throws IOException {
		BundleWiring wiring = mrBundle.adapt(BundleWiring.class);
		List<URL> urls = Collections.list(wiring.getClassLoader().getResources(name));
		if (urls.isEmpty()) {
			return RNF;
		}
		assertEquals("Wrong number of resources.", 1, urls.size());
		return readURL(urls.get(0));
	}
	
	public void testMultiReleaseGetEntryPaths() throws Exception {
		Set<String> expectedEntryPaths = new HashSet<String>();
		
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestClassBase.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestClass8.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestClass99.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestClass100.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestClass101.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestService.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestService99.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/TestServiceBase.class");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt");
		expectedEntryPaths.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt");
		
		Enumeration<String> entries = mrBundle.getEntryPaths("org/osgi/test/cases/framework/launch/mrjars/test");
		int count = 0;
		while(entries.hasMoreElements()) {
			String entry = entries.nextElement();
			assertTrue("Expected entry not found", expectedEntryPaths.contains(entry));
			count++;
		}
		assertEquals("Number of entries not as expected", expectedEntryPaths.size(), count);
	}
	

	public void testMultiReleaseListResources() throws Exception {
		Collection<String> expected = new ArrayList<String>();
		Collection<String> expectedRecurse = new ArrayList<String>();

		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResourceBase.txt");
		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource8.txt");
		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource99.txt");
		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource100.txt");
		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResource101.txt");
		
		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt");
		expected.add("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd100.txt");

		expectedRecurse.addAll(expected);
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceBase.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource8.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource99.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource100.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResource101.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceBase.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource8.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource99.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource100.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResource101.txt");

		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd99.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd99.txt");
		
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub/testResourceAdd100.txt");
		expectedRecurse.add("org/osgi/test/cases/framework/launch/mrjars/test/sub2/testResourceAdd100.txt");
		
		try {
			listResources("org/osgi/test/cases/framework/launch/mrjars/test", expected, 0);
			listResources("org/osgi/test/cases/framework/launch/mrjars/test", expectedRecurse, BundleWiring.LISTRESOURCES_RECURSE);
		} catch (Exception e) {
			// ignore;
		}
	}

	private void listResources(String path, Collection<String> expected, int options) {
		BundleWiring wiring = mrBundle.adapt(BundleWiring.class);
		Collection<String> found = wiring.listResources(path, "*.txt", options);
		Object[] expectedArray = expected.toArray();
		Object[] foundArray = found.toArray();
		Arrays.sort(expectedArray);
		Arrays.sort(foundArray);
		assertTrue("Wrong resource listing.", Arrays.equals(expectedArray, foundArray));
	}

	public void testFindEntries() throws Exception {
		assertFindEntries(mrBundle, "org/osgi/test/cases/framework/launch/mrjars/test", "*.txt", false, 5);
		assertFindEntries(mrBundle, "org/osgi/test/cases/framework/launch/mrjars", "*.txt", true, 5);
	}


	private void assertFindEntries(Bundle bundle, String path, String pattern,
			boolean recurse, int expectedNum) throws Exception {
		assertBundleFindEntries(bundle, path, pattern, recurse, expectedNum);
		assertWiringFindEntries(bundle, path, pattern, recurse, expectedNum);
	}

	private void assertBundleFindEntries(Bundle bundle, String path,
			String pattern, boolean recurse, int expectedNum) throws Exception {
		Enumeration<URL> entries = bundle.findEntries(path, pattern, recurse);
		int numFound = 0;
		if (expectedNum > 0)
			assertNotNull(entries);
		else {
			assertNull(entries);
			return;
		}
		
		while (entries.hasMoreElements()) {
			URL url = entries.nextElement();
			assertEquals("Wrong resource.", "RESOURCE XXX", readURL(url));
			numFound++;
		}
		assertEquals("Unexpected number of entries", expectedNum, numFound);
	}

	private void assertWiringFindEntries(Bundle bundle, String path, String pattern, boolean recurse, int expectedNum) throws IOException {
		BundleWiring wiring = bundle.adapt(BundleWiring.class);
		List<URL> entries = wiring.findEntries(path, pattern,
				recurse ? BundleWiring.FINDENTRIES_RECURSE : 0);
		int numFound = 0;
		assertNotNull("Entries should not be null.", entries);
		for(int i=0; i< entries.size(); i++) {
			URL url = entries.get(i);
			assertEquals("Wrong resource.", "RESOURCE XXX", readURL(url));
			numFound++;
		}
		assertEquals("Unexpected number of entries", expectedNum, numFound);
	}

	public void testMultiReleaseBundleManifestChangeRuntime() throws Exception {
		String[] rv = {"8","99","100","101"};
		String location = mrBundle.getLocation();
		stopFramework(framework);
		for (int i = 0; i < rv.length; i++) {
			doTestMultiReleaseBundleManifestChangeRuntime(rv[i], location);
		}
	}

	private void doTestMultiReleaseBundleManifestChangeRuntime(String version, String location) throws Exception {
		String expectedCap;
		String expectedPkg;
		int rv = Integer.parseInt(version);
		if (rv < 99) {
			expectedCap = "capbase";
			expectedPkg = "pkgbase";
		} else {
			expectedCap = "cap" + rv;
			expectedPkg = "pkg" + rv;
		}

		Framework framework2 = createFramework(getFrameworkConfiguration(version, false));

		try {
			startFramework(framework2);
			Bundle mrBundle = framework2.getBundleContext().getBundle(location);
			assertNotNull("No mrBundle found: " + rv, mrBundle);
			assertEquals("Wrong state of mrBundle: " + rv, Bundle.ACTIVE, mrBundle.getState());

			List<BundleWire> capWires = mrBundle.adapt(BundleWiring.class).getRequiredWires(expectedCap);
			assertEquals("Wrong number of capability wires: " + rv, 1, capWires.size());

			List<BundleRequirement> pkgReqs = mrBundle.adapt(BundleRevision.class).getDeclaredRequirements(PackageNamespace.PACKAGE_NAMESPACE);
			assertEquals("Wrong number of package requiremens: " + rv, 1, pkgReqs.size());
			String filter = pkgReqs.get(0).getDirectives().get(Namespace.REQUIREMENT_FILTER_DIRECTIVE);
			assertTrue("Wrong package filter: " + rv + " " + filter, filter.contains(expectedPkg));

		} finally {
			try {
				stopFramework(framework2);
			} catch (Exception e) {
				// ignore;
			}
		}
	}


	public void testMultiReleasePreventMetaInfServiceVersions() throws Exception {
		Class<?> testServiceClass = mrBundle.loadClass("org.osgi.test.cases.framework.launch.mrjars.test.TestService");
		ServiceLoader<?> loader = ServiceLoader.load(testServiceClass, mrBundle.adapt(BundleWiring.class).getClassLoader());
		Object testService = loader.iterator().next();
		assertEquals("Wrong service found.", "SERVICE_BASE", testService.toString());	
	}


	public void testMultiReleasePreventMetaInfResourceURLs() throws Exception {
		URL existingResource = mrBundle.getResource("org/osgi/test/cases/framework/launch/mrjars/test/testResourceAdd99.txt");
		assertNotNull("Did not find Java 99 added resource.", existingResource);
		URL metaInfResource = new URL(existingResource, "/META-INF/addedFor99.txt");
		try {
			metaInfResource.openStream().close();
			fail("Expected error opening versioned META-INF resource.");
		} catch (IOException e) {
				// expected
		}
	}


	public void testMultiReleasePreventMetaInfVersionListing() throws Exception {
		Collection<String> list = mrBundle.adapt(BundleWiring.class)
				.listResources("/META-INF/", "*.txt", 0);
		assertTrue("Found versioned META-INF resources: " + list,
				list.isEmpty());
	}

	public void testChangeJavaVersionLastModified() throws Exception {
		String originalLocation = mrBundle.getLocation();
		long originalLastModified = mrBundle.getLastModified();
		stopFramework(framework);
		Sleep.sleep(250); // Sleep to ensure we go past 1 ms granularity on timestamps

		Framework framework2 = createFramework(getFrameworkConfiguration("99", false));
		startFramework(framework2);
		Bundle b2 = framework2.getBundleContext().getBundle(originalLocation);
		assertNotNull(b2);
		assertEquals("bundle location changed after update in java version.", originalLocation, b2.getLocation());
		assertFalse("bundle last modified did not change.", originalLastModified == b2.getLastModified());
		stopFramework(framework2);
		Sleep.sleep(250); // Sleep to ensure we go past 1 ms granularity on timestamps

		Framework framework3 = createFramework(getFrameworkConfiguration("99", false));
		startFramework(framework3);
		Bundle b3 = framework3.getBundleContext().getBundle(originalLocation);
		assertNotNull(b3);
		assertEquals("bundle location changed after in java version.", b2.getLocation(), b3.getLocation());
		assertTrue("bundle last modified changed.", b2.getLastModified() == b3.getLastModified());
	}


	public void readFile(InputStream in, File file) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);

			byte buffer[] = new byte[1024];
			int count;
			while ((count = in.read(buffer, 0, buffer.length)) > 0) {
				fos.write(buffer, 0, count);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ee) {
					// nothing to do here
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ee) {
					// nothing to do here
				}
			}
		}
	}
}
