/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.test.cases.framework.div.tbc.Constants;

import java.lang.reflect.Field;

import org.osgi.framework.*;
import org.osgi.test.service.TestCaseLink;

/**
 * Test the constants of the class org.osgi.framework.Constants.
 * 
 * @version $Revision$
 */
public class ConstantsValues {

	private BundleContext	context;
	private String			tcHome;
	private TestCaseLink	link;
	final static String [] constants = {
		"SYSTEM_BUNDLE_LOCATION", "System Bundle",
		"SYSTEM_BUNDLE_SYMBOLICNAME", "system.bundle",
		"BUNDLE_CATEGORY", "Bundle-Category",
		"BUNDLE_CLASSPATH", "Bundle-ClassPath",
		"BUNDLE_COPYRIGHT", "Bundle-Copyright",
		"BUNDLE_DESCRIPTION", "Bundle-Description",
		"BUNDLE_NAME", "Bundle-Name",
		"BUNDLE_NATIVECODE", "Bundle-NativeCode",
		"EXPORT_PACKAGE", "Export-Package",
		"EXPORT_SERVICE", "Export-Service",
		"IMPORT_PACKAGE", "Import-Package",
		"DYNAMICIMPORT_PACKAGE", "DynamicImport-Package",
		"IMPORT_SERVICE", "Import-Service",
		"BUNDLE_VENDOR", "Bundle-Vendor",
		"BUNDLE_VERSION", "Bundle-Version",
		"BUNDLE_DOCURL", "Bundle-DocURL",
		"BUNDLE_CONTACTADDRESS", "Bundle-ContactAddress",
		"BUNDLE_ACTIVATOR", "Bundle-Activator",
		"BUNDLE_UPDATELOCATION", "Bundle-UpdateLocation",
		"PACKAGE_SPECIFICATION_VERSION", "specification-version",
		"BUNDLE_NATIVECODE_PROCESSOR", "processor",
		"BUNDLE_NATIVECODE_OSNAME", "osname",
		"BUNDLE_NATIVECODE_OSVERSION", "osversion",
		"BUNDLE_NATIVECODE_LANGUAGE", "language",
		"BUNDLE_REQUIREDEXECUTIONENVIRONMENT", "Bundle-RequiredExecutionEnvironment",
		"FRAMEWORK_VERSION", "org.osgi.framework.version",
		"FRAMEWORK_VENDOR", "org.osgi.framework.vendor",
		"FRAMEWORK_LANGUAGE", "org.osgi.framework.language",
		"FRAMEWORK_OS_NAME", "org.osgi.framework.os.name",
		"FRAMEWORK_OS_VERSION", "org.osgi.framework.os.version",
		"FRAMEWORK_PROCESSOR", "org.osgi.framework.processor",
		"FRAMEWORK_EXECUTIONENVIRONMENT", "org.osgi.framework.executionenvironment",
		"SUPPORTS_FRAMEWORK_EXTENSION", "org.osgi.supports.framework.extension",
		"SUPPORTS_BOOTCLASSPATH_EXTENSION", "org.osgi.supports.bootclasspath.extension",
		"OBJECTCLASS", "objectClass",
		"SERVICE_ID", "service.id",
		"SERVICE_PID", "service.pid",
		"SERVICE_RANKING", "service.ranking",
		"SERVICE_VENDOR", "service.vendor",
		"SERVICE_DESCRIPTION", "service.description",
		"BUNDLE_SYMBOLICNAME", "Bundle-SymbolicName",
		"SINGLETON_DIRECTIVE", "singleton",
		"FRAGMENT_ATTACHMENT_DIRECTIVE", "fragment-attachment",
		"FRAGMENT_ATTACHMENT_ALWAYS", "always",
		"FRAGMENT_ATTACHMENT_RESOLVETIME", "resolve-time",
		"FRAGMENT_ATTACHMENT_NEVER", "never",
		"BUNDLE_LOCALIZATION", "Bundle-Localization",
		"BUNDLE_LOCALIZATION_DEFAULT_BASENAME", "OSGI-INF/l10n/bundle",
		"REQUIRE_BUNDLE", "Require-Bundle",
		"BUNDLE_VERSION_ATTRIBUTE", "bundle-version",
		"FRAGMENT_HOST", "Fragment-Host",
		"SELECTION_FILTER_ATTRIBUTE", "selection-filter",
		"BUNDLE_MANIFESTVERSION", "Bundle-ManifestVersion",
		"VERSION_ATTRIBUTE", "version",
		"BUNDLE_SYMBOLICNAME_ATTRIBUTE", "bundle-symbolic-name",
		"RESOLUTION_DIRECTIVE", "resolution",
		"RESOLUTION_MANDATORY", "mandatory",
		"RESOLUTION_OPTIONAL", "optional",
		"USES_DIRECTIVE", "uses",
		"INCLUDE_DIRECTIVE", "include",
		"EXCLUDE_DIRECTIVE", "exclude",
		"MANDATORY_DIRECTIVE", "mandatory",
		"VISIBILITY_DIRECTIVE", "visibility",
		"VISIBILITY_PRIVATE", "private",
		"VISIBILITY_REEXPORT", "reexport",
		//"REEXPORT_PACKAGE", "Reexport-Package",
	};

	/**
	 * Creates a new ConstantsValues
	 * 
	 * @param _context the bundle context
	 * @param _link the link with test director
	 * @param _tcHome the test case home
	 */
	public ConstantsValues(BundleContext _context, TestCaseLink _link,
			String _tcHome) {
		context = _context;
		link = _link;
		tcHome = _tcHome;
	}

	/**
	 * Run the tests
	 */
	public void run() throws Exception {
		testConstantValues0001();
	}

	/**
	 * Test the constant values
	 */
	public void testConstantValues0001() throws Exception {
		for ( int i=0; i<constants.length; i+=2 ) {
			String name = constants[i];
			String value = constants[i+1];
			testConstant(Constants.class, name, value );
		}
	}
	
	private void testConstant(Class clazz, String constant, String value) throws Exception {
		Field f = clazz.getField(constant);
		String actual = (String) f.get(null);
		if ( actual == null )
			throw new ConstantsTestException(constant +  " is null");
		if ( ! actual.equals(value))
			throw new ConstantsTestException(constant +  " is not equal to " + value );			
	}
}