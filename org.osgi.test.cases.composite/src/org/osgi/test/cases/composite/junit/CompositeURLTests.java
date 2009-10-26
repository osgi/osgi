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

import java.io.IOException;
import java.net.ContentHandler;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.url.URLStreamHandlerService;
import org.osgi.test.cases.composite.junit.exceptions.TestException;



public class CompositeURLTests extends AbstractCompositeTestCase {

	public void testURLHandlerParent01() throws BundleException, IOException {
		Bundle tb5a = install("tb5a.jar");
		Bundle tb5aclient = install("tb5aclient.jar");
		tb5a.start();
		tb5aclient.start();
	}

	public void testURLHandlerParent02() throws BundleException, IOException {
		Bundle tb5a = install("tb5b.jar");
		Bundle tb5aclient = install("tb5bclient.jar");
		tb5a.start();
		tb5aclient.start();
	}
	
	public void testURLImport01a() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(objectClass=" + ContentHandler.class.getName() + ")(objectClass=" + URLStreamHandlerService.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb5a.jar"}, null, "tb5aclient.jar", false, null);
	}

	public void testURLImport01b() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(&(match=fail)(objectClass=" + URLStreamHandlerService.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb5a.jar"}, null, "tb5aclient.jar", true, new SimpleTestHandler(TestException.NO_PROTOCOL));
	}

	public void testURLImport01c() {
		Map manifest = new HashMap();
		manifest.put(Constants.BUNDLE_SYMBOLICNAME, getName() + ';' + CompositeConstants.COMPOSITE_DIRECTIVE + ":=" + true);
		manifest.put(CompositeConstants.COMPOSITE_PACKAGE_IMPORT_POLICY, "org.osgi.test.cases.composite.junit.exceptions");
		manifest.put(CompositeConstants.COMPOSITE_SERVICE_IMPORT_POLICY, "(|(&(match=fail)(objectClass=" + ContentHandler.class.getName() + "))(objectClass=" + URLStreamHandlerService.class.getName() + "))");
		doTestImportPolicy01(manifest, new String[] {"tb5a.jar"}, null, "tb5aclient.jar", true, new SimpleTestHandler(TestException.WRONG_CONTENT_HANDER));
	}
}
