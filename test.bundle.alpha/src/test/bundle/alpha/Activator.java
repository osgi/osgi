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


package test.bundle.alpha;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.bundle.BundleActivator;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

public class Activator implements BundleActivator,
		BundleTrackerCustomizer<Bundle> {
	
	private BundleTracker<Bundle>	tracker;

	public void start(BundleContext context) throws Exception {
		System.out.println("Hello World! " + context.toString());
		tracker = new BundleTracker<Bundle>(context, Bundle.ACTIVE, this);
		// tracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
		System.out.println("Goodbye World! " + context.toString());
	}

	public Bundle addingBundle(Bundle bundle, BundleEvent event) {
		System.out.println(bundle.toString());
		return bundle;
	}

	public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle tracked) {
		// empty
	}

	public void removedBundle(Bundle bundle, BundleEvent event, Bundle tracked) {
		// empty
	}

}
