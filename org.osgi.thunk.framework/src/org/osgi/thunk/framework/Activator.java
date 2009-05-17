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


package org.osgi.thunk.framework;

import org.osgi.framework.ServiceException;
import org.osgi.wrapped.framework.TBundle;
import org.osgi.wrapped.framework.TBundleActivator;
import org.osgi.wrapped.framework.TBundleContext;
import org.osgi.wrapped.framework.TBundleEvent;
import org.osgi.wrapped.service.packageadmin.TPackageAdmin;

public class Activator implements TBundleActivator,
		TBundleTrackerCustomizer<BundleContextImpl> {

	private TBundleTracker<BundleContextImpl>	tracker;
	private static TPackageAdmin				packageAdmin;

	static TPackageAdmin getTPackageAdmin() {
		if (packageAdmin == null) {
			throw new ServiceException("No PackageAdmin service avalable",
					ServiceException.UNREGISTERED);
		}
		return packageAdmin;
	}

	public void start(TBundleContext tcontext) throws Exception {
		packageAdmin = tcontext.getTPackageAdmin();
		tracker = new TBundleTracker<BundleContextImpl>(tcontext,
				TBundle.ACTIVE,
				this);
		tracker.open();
	}

	public void stop(TBundleContext tcontext) throws Exception {
		tracker.close();
	}

	public BundleContextImpl addingBundle(TBundle bundle, TBundleEvent event) {
		BundleContextImpl targetContext = new BundleContextImpl(bundle
				.getBundleContext());
		try {
			targetContext.start();
			return targetContext;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void modifiedBundle(TBundle bundle, TBundleEvent event,
			BundleContextImpl object) {
		// do nothing
	}

	public void removedBundle(TBundle bundle, TBundleEvent event,
			BundleContextImpl targetContext) {
		try {
			targetContext.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
