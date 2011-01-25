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

package org.osgi.test.cases.framework.secure.junit.hooks.weaving;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.test.support.wiring.Wiring;

public class WeavingHookTests extends OSGiTestCase {
	private final List bundles = new ArrayList();
	FrameworkWiring frameworkWiring;
	
	
	public Bundle install(String bundle) {
		Bundle result = null;
		try {
			result = super.install(bundle);
		} catch (BundleException e) {
			fail("failed to install bundle: " + bundle, e);
		} catch (IOException e) {
			fail("failed to install bundle: " + bundle, e);
		}
		if (bundle == null)
			fail("Failed to install bundle: " + bundle);
		if (!bundles.contains(result))
			bundles.add(result);
		return result;
	}

	protected void setUp() throws Exception {
		bundles.clear();
		frameworkWiring = (FrameworkWiring) getContext().getBundle(0).adapt(FrameworkWiring.class);
	}

	protected void tearDown() throws Exception {
		for (Iterator iBundles = bundles.iterator(); iBundles.hasNext();) {
			Bundle bundle = (Bundle) iBundles.next();
			try {
				if (!(bundle.getState() == Bundle.UNINSTALLED))
					bundle.uninstall();
			} catch (BundleException e) {
				// nothing
			}
		}
		Wiring.synchronousRefreshBundles(getContext(), bundles);
		bundles.clear();
	}

	public void testWeavingPermission() {
		Bundle tb1 = install("weaving.tb1.jar");

		try {
			tb1.start();
		} catch (BundleException e) {
			if (e.getCause() instanceof AssertionError)
				throw (AssertionError) e.getCause();
			fail("Failed to start test bundle", e);

		}
	}
}
