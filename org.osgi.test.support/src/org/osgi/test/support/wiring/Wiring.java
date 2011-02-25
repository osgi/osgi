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


package org.osgi.test.support.wiring;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.osgi.test.support.OSGiTestCase.fail;
import static org.osgi.test.support.OSGiTestCaseProperties.getScaling;
import static org.osgi.test.support.OSGiTestCaseProperties.getTimeout;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.wiring.FrameworkWiring;

public class Wiring {
	/**
	 * Refreshes the collection of bundle synchronously.
	 * 
	 * @param bundles The bundles to be refreshed, or {@code null} to refresh
	 *        the removal pending bundles.
	 */
	public static void synchronousRefreshBundles(BundleContext context,
			Collection<Bundle> bundles) {
		FrameworkWiring fwkWiring = context.getBundle(0).adapt(
				FrameworkWiring.class);
		assertNotNull("Framework wiring is null.", fwkWiring);
		final boolean[] done = new boolean[] {false};
		FrameworkListener listener = new FrameworkListener() {
			public void frameworkEvent(FrameworkEvent event) {
				if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
					synchronized (done) {
						done[0] = true;
						done.notify();
					}
				}
			}
		};
		fwkWiring.refreshBundles(bundles, listener);
		synchronized (done) {
			if (!done[0])
				try {
					done.wait(getTimeout() * getScaling());
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					fail("Unexepected interruption.", e);
				}
			if (!done[0])
				fail("Timed out waiting for refresh bundles to finish.");
		}
	}
}
