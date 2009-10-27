/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;
import org.osgi.framework.hooks.service.ListenerHook;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;
import org.osgi.test.cases.composite.TestException;



public class CompositeHookTests extends AbstractCompositeTestCase {
	
	public void setUp() {
		super.setUp();
	}

	public void testHookImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", false, null);
	}

	public void testHookImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.LISTENER_HOOK));
	}

	public void testHookImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.FIND_HOOK));
	}

	public void testHookImport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.EVENT_HOOK));
	}

	public void testHookExportt01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", false, null);
	}

	public void testHookExport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + "))");
		doTestExportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.LISTENER_HOOK));
	}

	public void testHookExport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.FIND_HOOK));
	}

	public void testHookExport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportPolicy01(manifest, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.EVENT_HOOK));
	}

	public void testHookExportImport01a() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb6.jar"}, null, "tb6client.jar", false, null);
	}

	public void testHookExportImport01b() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + "))");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.LISTENER_HOOK));
	}

	public void testHookExportImport01c() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.FIND_HOOK));
	}

	public void testHookExportImport01d() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(|(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + EventHook.class.getName() + ")(objectClass=" + FindHook.class.getName() + ")(objectClass=" + ListenerHook.class.getName() + "))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb6.jar"}, null, "tb6client.jar", true, new SimpleTestHandler(TestException.EVENT_HOOK));
	}
}
