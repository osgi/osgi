/*
 * Copyright (c) OSGi Alliance (2010, 2020). All Rights Reserved.
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

package org.osgi.test.cases.framework.secure.junit.permissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.test.support.OSGiTestCase;

public class ConditionServicePermisionTest extends OSGiTestCase {
	private final List<Bundle>								bundles			= new ArrayList<>();

	public Bundle install(String bundle) {
		Bundle result = null;
		try {
			result = super.install(bundle);
		} catch (BundleException e) {
			fail("failed to install bundle: " + bundle, e);
		} catch (IOException e) {
			fail("failed to install bundle: " + bundle, e);
		}
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		bundles.clear();
	}

	protected void tearDown() throws Exception {
		for (Iterator<Bundle> iBundles = bundles.iterator(); iBundles
				.hasNext();)
			try {
				iBundles.next().uninstall();
			} catch (BundleException e) {
				// nothing
			} catch (IllegalStateException e) {
				// happens if the test uninstalls the bundle itself
			}
		bundles.clear();
	}


	public void testImpliedConditonServicePermision() throws BundleException {
		// install bundle with osgi.ee requirement
		Bundle tester = install("permissions.condition.jar");
		tester.start();
	}

}
