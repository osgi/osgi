/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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
package org.osgi.test.cases.framework.weaving.tbx;

import junit.framework.Assert;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
*
*/
public class Activator implements BundleActivator {
	private final static String PACKAGE_NAME = "org.osgi.test.cases.framework.weaving.tbx.";
	private final static String TEST_NAME = "weaving.name";
	private final static String TEST_RESULT = "weaving.result";
	public void start(BundleContext context) throws Exception {
		String className = PACKAGE_NAME + System.getProperty(TEST_NAME);
		String expected = System.getProperty(TEST_RESULT);
		if ("NO TEST".endsWith(expected))
			return;
		String result = Class.forName(className).newInstance().toString();
		Assert.assertEquals("Wrong result", expected, result);
	}

	public void stop(BundleContext context) throws Exception {
		// empty
	}

}
