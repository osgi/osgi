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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.test.cases.composite.junit.exceptions.TestException;


public class CompositeServiceTests extends AbstractCompositeTestCase {
	private static ServiceTestHandler noService = new ServiceTestHandler(TestException.NO_SERVICE);
	private static ServiceTestHandler noServiceRef = new ServiceTestHandler(TestException.NO_SERVICE_REFERENCE);
	private static ServiceTestHandler wrongServiceProp = new ServiceTestHandler(TestException.WRONG_SERVICE_PROPERTY);
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

	static class ServiceTestHandler implements TestExceptionHandler {
		private final int testCode;
		
		public ServiceTestHandler(int testCode) {
			this.testCode = testCode;
		}

		public void handleException(Throwable t) {
			if (!(t.getCause() instanceof TestException))
				fail("Unexpected exception type.", t);
			TestException cause = (TestException) t.getCause();
			if (testCode != cause.getTestCode())
				fail("Unexpected exception type.", cause);
		}


		
	}
}
