/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.composite.junit;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.test.cases.composite.junit.exceptions.TestException;


public class CompositeServiceTests extends AbstractCompositeTestCase {
	private static AbstractCompositeTestCase.SimpleTestHandler noService = new AbstractCompositeTestCase.SimpleTestHandler(TestException.NO_SERVICE);
	private static AbstractCompositeTestCase.SimpleTestHandler noServiceRef = new AbstractCompositeTestCase.SimpleTestHandler(TestException.NO_SERVICE_REFERENCE);
	private static AbstractCompositeTestCase.SimpleTestHandler wrongServiceProp = new AbstractCompositeTestCase.SimpleTestHandler(TestException.WRONG_SERVICE_PROPERTY);

	public void testServiceImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceImport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceImport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestImportPolicy01(manifest, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceImport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, 
				new ServiceReferenceTestHandler("tb4v2Impl.jar", "org.osgi.test.cases.composite.tb4.SomeService", true));
	}

	public void testServiceImport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, 
				new ServiceReferenceTestHandler("tb4v1Impl.jar", "org.osgi.test.cases.composite.tb4.SomeService", false));
	}

	public void testServiceImport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite.junit.exceptions");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2,new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceImport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceImport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceImport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite.junit.exceptions");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceImport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceExportImport01a() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceExportImport01b() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceExportImport01c() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceExportImport01d() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceExportImport01e() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite.junit.exceptions");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceExportImport02a() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceExportImport02b() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceExportImport02c() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceExportImport02d() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite.junit.exceptions");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceExportImport02e() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite.junit.exceptions");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	class ServiceReferenceTestHandler implements TestHandler {
		private final String serviceProvider;
		private final String serviceClass;
		private final boolean expectFailure;


		public ServiceReferenceTestHandler(String serviceProvider,
				String serviceClass, boolean expectFailure) {
			this.serviceProvider = getLocation(serviceProvider);
			this.serviceClass = serviceClass;
			this.expectFailure = expectFailure;
		}

		private String getLocation(String name) {
			URL entry = getContext().getBundle().getEntry(name);
			assertNotNull("Could not find bundle: " + name);
			return entry.toExternalForm();
		}

		public void handleBundles(Bundle[] exportBundles, Bundle[] importBundles,
				Bundle client) {
			testGetServiceReference(exportBundles, importBundles, client);
			testServiceEvents(exportBundles, importBundles, client);
		}

		private void testServiceEvents(Bundle[] exportBundles,
				Bundle[] importBundles, Bundle client) {
			Bundle provider = getBundle(serviceProvider, exportBundles);
			assertEquals("Provider is not active", Bundle.ACTIVE, provider.getState());
			TestServiceListener listener = new TestServiceListener();
			try {
				client.getBundleContext().addServiceListener(listener, "(objectClass=" + serviceClass + ")");
				provider.stop();
				provider.start();
				ServiceEvent[] results;
				int[] types;
				if (expectFailure) {
					results = new ServiceEvent[0];
					types = new int[0];
				} else {
					results = new ServiceEvent[2];
					types = new int[] {ServiceEvent.UNREGISTERING, ServiceEvent.REGISTERED};
				}
				results = (ServiceEvent[]) listener.getResults(results);
				compareServiceEvents(serviceClass, provider, types, results);
			} catch (Exception e) {
				fail("Unexpected exception", e);
			} finally {
				client.getBundleContext().removeServiceListener(listener);
			}
			
		}

		private void compareServiceEvents(String service,
				Bundle provider, int[] types, ServiceEvent[] results) {
			assertEquals("Unexpected event number", types.length, results.length);
			Filter serviceFilter = null;
			try {
				serviceFilter = getContext().createFilter("(objectclass=" + serviceClass + ")");
			} catch (InvalidSyntaxException e) {
				fail("Failed to create filter", e);
			}
			for (int i = 0; i < results.length; i++) {
				assertEquals("Wrong event type: " + i, types[i], results[i].getType());
				assertTrue("Wrong service type: " + results[i].toString(), serviceFilter.match(results[i].getServiceReference()));
			}
		}

		private void testGetServiceReference(Bundle[] exportBundles, Bundle[] importBundles,
				Bundle client) {
			Bundle provider = getBundle(serviceProvider, exportBundles);
			ServiceReference ref = getReference(provider, serviceClass);
			try {
				Object service = client.getBundleContext().getService(ref);
				client.getBundleContext().ungetService(ref);
				if (expectFailure)
					fail("Expected failure to get service: " + serviceClass);
				else if (service == null)
					fail("Expected to find a service: " + serviceClass);
			} catch (IllegalArgumentException e) {
				if (expectFailure)
					return;
				fail("Unexpected exception", e);
			}
		}

		private ServiceReference getReference(Bundle provider, String service) {
			ServiceReference[] refs = provider.getRegisteredServices();
			assertNotNull("Services are null.", refs);
			Filter serviceFilter = null;
			try {
				serviceFilter = getContext().createFilter("(objectclass=" + serviceClass + ")");
			} catch (InvalidSyntaxException e) {
				fail("Failed to create filter", e);
			}
			//don't register the service if this bundle has already registered it declaratively
			for (int i = 0; i < refs.length; i++)
				if (serviceFilter.match(refs[i]))
					return refs[i];
			fail("Could not find service: " + service);
			return null;
		}

		private Bundle getBundle(String location, Bundle[] bundles) {
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getLocation().equals(location)) {
					return bundles[i];
				}
			}
			fail("Could not find bundle: " + location);
			return null;
		}

		public void handleException(Throwable t) {
			// Do nothing
		}
	}
}
