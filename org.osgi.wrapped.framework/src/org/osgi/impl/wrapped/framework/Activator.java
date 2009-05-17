/*
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


package org.osgi.impl.wrapped.framework;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

public class Activator implements BundleActivator, BundleTrackerCustomizer {
	private BundleTracker	bundleTracker;

	public void start(BundleContext context) throws Exception {
		bundleTracker = new BundleTracker(context, Bundle.ACTIVE, this);
		bundleTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		bundleTracker.close();
	}

	public Object addingBundle(Bundle bundle, BundleEvent event) {
		TBundleContextImpl targetContext = new TBundleContextImpl(bundle
				.getBundleContext());
		try {
			targetContext.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return targetContext;
	}

	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		// do nothing
	}

	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		TBundleContextImpl targetContext = (TBundleContextImpl) object;
		try {
			targetContext.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
