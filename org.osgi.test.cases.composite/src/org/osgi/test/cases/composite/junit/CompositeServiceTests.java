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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.test.cases.composite.AbstractCompositeTestCase;
import org.osgi.test.cases.composite.TestException;
import org.osgi.test.cases.composite.TestHandler;


public class CompositeServiceTests extends AbstractCompositeTestCase {
	private static AbstractCompositeTestCase.SimpleTestHandler noService = new AbstractCompositeTestCase.SimpleTestHandler(TestException.NO_SERVICE);
	private static AbstractCompositeTestCase.SimpleTestHandler noServiceRef = new AbstractCompositeTestCase.SimpleTestHandler(TestException.NO_SERVICE_REFERENCE);
	private static AbstractCompositeTestCase.SimpleTestHandler wrongServiceProp = new AbstractCompositeTestCase.SimpleTestHandler(TestException.WRONG_SERVICE_PROPERTY);
	private static String TEST_SERVICE2 = "test.service2";
	
	public void testServiceImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceImport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceImport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestImportPolicy01(manifest, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceImport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, 
				new ServiceReferenceTestHandler("tb4v2Impl.jar", "org.osgi.test.cases.composite.tb4.SomeService", true));
	}

	public void testServiceImport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, 
				new ServiceReferenceTestHandler("tb4v1Impl.jar", "org.osgi.test.cases.composite.tb4.SomeService", false));
	}

	public void testServiceImport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService), (objectClass=org.osgi.test.cases.composite.tb4.SomeService2)");
		
		doTestImportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client2.jar", false, null);
	}

	private final static String KEY_POLICY_ONLY = "key1";
	private final static String KEY_POLICY_AND_LISTENER = "key2";
	private final static String KEY_LISTENER_ONLY = "key3";
	private final static String KEY_NONE = "key4";

	private final static String filter1 = "(key1=true)";
	private final static String filter2 = "(key2=true)";
	private final static String filter3 = "(key3=true)";
	private final static String POLICY_FILTER = "(&" + filter1 + filter2 + ")";
	private final static String LISTENER_FILTER = "(&" + filter2 + filter3 + ")";

	public void testServiceEventImport01() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, POLICY_FILTER);
		CompositeBundle composite = createCompositeBundle(compAdmin, null, manifest, null);
		startCompositeBundle(composite);
		Bundle tb1 = installConstituent(composite, null, "tb1.jar");
		try {
			tb1.start();
		} catch (BundleException e) {
			fail("Failed to start test bundle.", e);
		}
		doTestServiceEvent(getContext(), tb1.getBundleContext());
	}
	
	private void doTestServiceEvent(BundleContext producer, BundleContext consumer) {
		TestServiceListener testListener = new TestServiceListener();
		try {
			consumer.addServiceListener(testListener, LISTENER_FILTER);
		} catch (InvalidSyntaxException e) {
			fail("Failed to add test listener", e);
		}

		// register a service that should match both the policy and listener filters
		Hashtable props = new Hashtable();
		props.put(KEY_POLICY_ONLY, Boolean.TRUE);
		props.put(KEY_POLICY_AND_LISTENER, Boolean.TRUE);
		props.put(KEY_LISTENER_ONLY, Boolean.TRUE); //$NON-NLS-1$

		List<ServiceEvent> expected = new ArrayList<ServiceEvent>();
		// REGISTERED event
		ServiceRegistration<Object> registration = producer.registerService(Object.class, new Object(), props);
		ServiceReference<Object> reference = registration.getReference();
		expected.add(new ServiceEvent(ServiceEvent.REGISTERED, reference));
		try {

			// MODIFIED event
			props.put(KEY_NONE, Boolean.TRUE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED, reference));

			// MODIFIED_ENDMATCH event
			props.put(KEY_LISTENER_ONLY, Boolean.FALSE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED_ENDMATCH, reference));

			// No event
			props.put(KEY_NONE, Boolean.FALSE);
			registration.setProperties(props);

			// MODIFIED event
			props.put(KEY_LISTENER_ONLY, Boolean.TRUE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED, reference));

			// MODIFIED_ENDMATCH event
			props.put(KEY_POLICY_ONLY, Boolean.FALSE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED_ENDMATCH, reference));

			// No event
			props.put(KEY_NONE, Boolean.TRUE);
			registration.setProperties(props);

			// MODIFIED event
			props.put(KEY_POLICY_ONLY, Boolean.TRUE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED, reference));

			// MODIFIED_ENDMATCH event
			props.put(KEY_POLICY_AND_LISTENER, Boolean.FALSE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED_ENDMATCH, reference));

			// MODIFIED event
			props.put(KEY_POLICY_AND_LISTENER, Boolean.TRUE);
			registration.setProperties(props);
			expected.add(new ServiceEvent(ServiceEvent.MODIFIED, reference));

			// UNREGISTERING
			registration.unregister();
			registration = null;
			expected.add(new ServiceEvent(ServiceEvent.UNREGISTERING, reference));

			ServiceEvent[] actual = (ServiceEvent[]) testListener.getResults(new ServiceEvent[expected.size()], false);
			assertEquals(expected.toArray(new ServiceEvent[expected.size()]), actual);
			
		} finally {
			if (registration != null)
				registration.unregister();
		}
	}

	private void assertEquals(ServiceEvent[] expected, ServiceEvent[] actual){
		if (expected.length != actual.length)
			fail("Wrong number of events: " + expected.length + " " + actual.length);
		for (int i = 0; i < actual.length; i++) {
			if (actual[i].getType() != expected[i].getType())
				fail("Wrong event type for event #" + i + " " + expected[i].getType() + " " + actual[i].getType());
			if (!expected[i].getServiceReference().equals(actual[i].getServiceReference()))
				fail("Wrong service reference for event #" + i + " " + expected[i].getServiceReference() + " " + actual[i].getServiceReference());
		}
	}
	public void testServiceImport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2,new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceImport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceImport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceImport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceImport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestImportPolicy02(manifest1, manifest2, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceExport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceExport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceExport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceExport01d() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceExport01e() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestExportPolicy01(manifest, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceExport01f() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, 
				new ServiceReferenceTestHandler("tb4v2Impl.jar", "org.osgi.test.cases.composite.tb4.SomeService", true));
	}

	public void testServiceExport01g() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, 
				new ServiceReferenceTestHandler("tb4v1Impl.jar", "org.osgi.test.cases.composite.tb4.SomeService", false));
	}

	public void testServiceExport02a() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPolicy02(manifest1, manifest2,new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceExport02b() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceExport02c() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceExport02d() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceExport02e() {
		Map manifest1 = new HashMap();
		manifest1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_1 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifest1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifest2 = new HashMap(manifest1);
		manifest2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + "_2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportPolicy02(manifest1, manifest2, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	public void testServiceExport01h() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService), (objectClass=org.osgi.test.cases.composite.tb4.SomeService2)");

		doTestExportPolicy01(manifest, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client2.jar", false, null);
	}

	public void testServiceExportImport01a() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceExportImport01b() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceExportImport01c() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceExportImport01d() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceExportImport01e() {
		Map manifestExport = new HashMap();
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceImportCompositePeer01a() {
		// tests that when some peer constraint is specified then the service cannot be registered by a bundle in the parent
		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, 
				"\"(objectClass=org.osgi.test.cases.composite.tb4.SomeService)\"; " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"some.parent\"");
		doTestImportPolicy01(manifestImport, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", true, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer01a() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, exportBSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, 
				"\"(objectClass=org.osgi.test.cases.composite.tb4.SomeService)\"; " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer01b() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, exportBSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "\"(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))\"; " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer01c() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "\"(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))\"; " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + exportBSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer01d() {
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "\"(objectClass=org.osgi.test.cases.composite.tb4.SomeService)\"; " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"expect.failure\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer01e() {
		try {
			install("tb4v1.jar").start();
			install("tb4v2.jar").start();
			install("tb4v1Impl.jar").start();
			install("tb4v2Impl.jar").start();
		} catch (BundleException e) {
			fail("Unexpected exception installing bundle", e);
		}
		Map manifestExport = new HashMap();
		String exportBSN = getName() + ".export";
		String exportVersion = "2.0.0";
		manifestExport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport.put(Constants.BUNDLE_VERSION, exportVersion);
		manifestExport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestExport.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		Map manifestImport = new HashMap();
		manifestImport.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "\"(objectClass=org.osgi.test.cases.composite.tb4.SomeService)\"; " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"fail\"");
		doTestExportImportPolicy01(manifestExport, manifestImport, new String[] {"tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, noServiceRef);
	}

	public void testServiceExportImport02a() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	public void testServiceExportImport02b() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", false, null);
	}

	public void testServiceExportImport02c() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=1, org.osgi.test.cases.composite.tb4.params; tb4version=1, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v2client.jar", true, wrongServiceProp);
	}

	public void testServiceExportImport02d() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4; tb4version=2, org.osgi.test.cases.composite.tb4.params; tb4version=2, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(test.property=v1)(objectClass=org.osgi.test.cases.composite.tb4.SomeService))");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4v2.jar", "tb4v1Impl.jar", "tb4v2Impl.jar"}, null, "tb4v1client.jar", true, noServiceRef);
	}

	public void testServiceExportImport02e() {
		Map manifestExport1 = new HashMap();
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4Impl.jar"}, new String[] {"tb4v1.jar"}, "tb4client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer02a() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService); " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", false, null);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer02b() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService); " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[1.0.0, 1.1.0)\"");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", true, noServiceRef);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer02c() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService); " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export2BSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", true, noServiceRef);
	}

	// TODO this is experimental to support composite peer constraints
	public void testServiceExportImportCompositePeer02d() {
		Map manifestExport1 = new HashMap();
		String export1BSN = getName() + ".export1";
		String export1Version = "2.0.0";
		manifestExport1.put(Constants.BUNDLE_SYMBOLICNAME, export1BSN + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestExport1.put(Constants.BUNDLE_VERSION, export1Version);
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_EXPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params");
		manifestExport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite");
		manifestExport1.put(CompositeConstants.COMPOSITE_SERVICE_EXPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestExport2 = new HashMap(manifestExport1);
		String export2BSN = getName() + ".export2";
		String export2Version = "2.0.0";
		manifestExport2.put(Constants.BUNDLE_VERSION, export2Version);
		manifestExport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".export2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);

		Map manifestImport1 = new HashMap();
		manifestImport1.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import1; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport1.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.tb4, org.osgi.test.cases.composite.tb4.params, org.osgi.test.cases.composite");
		manifestImport1.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService)");
		Map manifestImport2 = new HashMap(manifestImport1);
		manifestImport2.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ".import2 ;" + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifestImport2.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(objectClass=org.osgi.test.cases.composite.tb4.SomeService); " +
				CompositeConstants.COMPOSITE_SYMBOLICNAME_DIRECTIVE + ":=\"" + export1BSN + "\"; " +
				CompositeConstants.COMPOSITE_VERSION_DIRECTIVE + ":=\"[2.0.0, 2.1.0)\"");

		doTestExportImportPolicy02(manifestExport1, manifestExport2, manifestImport1, manifestImport2, new String[] {"tb4v1.jar", "tb4Impl.jar"}, null, "tb4client.jar", true, noServiceRef);
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
				if (bundles[i].getLocation().startsWith(location)) {
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
