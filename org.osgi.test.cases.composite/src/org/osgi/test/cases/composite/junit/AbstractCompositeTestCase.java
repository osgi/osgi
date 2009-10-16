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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.composite.CompositeAdmin;
import org.osgi.service.composite.CompositeBundle;
import org.osgi.service.composite.CompositeConstants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractCompositeTestCase extends OSGiTestCase {
	protected ServiceReference compRef;
	protected CompositeAdmin compAdmin;

	public void setUp() {
		compRef = getContext().getServiceReference(CompositeAdmin.class.getName());
		assertNotNull(compRef);
		compAdmin = (CompositeAdmin) getContext().getService(compRef);
		assertNotNull(compAdmin);
	}

	public void tearDown() {
		if (compAdmin != null)
			getContext().ungetService(compRef);
		compAdmin = null;
		compRef = null;
	}

	CompositeBundle createCompositeBundle(CompositeAdmin factory, String location, Map compositeManifest, Map configuration) {
		if (configuration == null)
			configuration = new HashMap();

		if (compositeManifest == null)
			compositeManifest = new HashMap();

		if (compositeManifest.get(Constants.BUNDLE_SYMBOLICNAME) == null)
			compositeManifest.put(Constants.BUNDLE_SYMBOLICNAME, location + "; " + CompositeConstants.COMPOSITE_DIRECTIVE + ":=true");

		CompositeBundle composite = null;
		try {
			composite = factory.installCompositeBundle(location, compositeManifest, configuration);
		} catch (BundleException e) {
			fail("Unexpected exception creating composite bundle", e); //$NON-NLS-1$
		}
		assertNotNull("Composite is null", composite); //$NON-NLS-1$
		assertEquals("Wrong composite location", location, composite.getLocation()); //$NON-NLS-1$
		assertNotNull("Compoisite System Bundle context must not be null", composite.getSystemBundleContext());
		assertEquals("Wrong state for SystemBundle", Bundle.STARTING, composite.getSystemBundleContext().getBundle().getState()); //$NON-NLS-1$
		return composite;
	}

	protected void startCompositeBundle(CompositeBundle composite) {
		try {
			composite.start();
		} catch (BundleException e) {
			fail("Failed to start composite", e); //$NON-NLS-1$
		}
		assertEquals("Wrong state for SystemBundle", Bundle.ACTIVE, composite.getBundleContext().getBundle().getState()); //$NON-NLS-1$
	}

	protected void stopCompositeBundle(CompositeBundle composite) {
		Bundle[] bundles = composite.getSystemBundleContext().getBundles();
		try {
			composite.stop();
		} catch (BundleException e) {
			fail("Unexpected error stopping composite", e); //$NON-NLS-1$
		}
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getBundleId() != 0)
				assertEquals("Bundle is in the wrong state: " + bundles[i].getLocation(), Bundle.RESOLVED, bundles[i].getState());
			else
				assertEquals("System Bundle is in the wrong state", Bundle.STARTING, bundles[i].getState());
		}

	}

	protected void uninstallCompositeBundle(CompositeBundle composite) {
		Bundle[] constituents = composite.getSystemBundleContext().getBundles();
		try {
			composite.uninstall();
		} catch (BundleException e) {
			fail("Unexpected error uninstalling composite", e); //$NON-NLS-1$
		}
		for (int i = 0; i < constituents.length; i++)
			if (constituents[i].getBundleId() != 0)
				assertEquals("Wrong state for constituent", Bundle.UNINSTALLED, constituents[i].getState());
		BundleContext compositeContext = composite.getSystemBundleContext();
		assertNull("Composite system bundle context must be null", compositeContext);
	}

	protected Bundle installConstituent(CompositeBundle composite, String location, String name) {
		try {
			URL content = getBundleContent(name);
			String externalForm = content.toExternalForm();
			if (location == null)
				location = externalForm;
			BundleContext context = composite.getSystemBundleContext();
			return (externalForm.equals(location)) ? context.installBundle(location) : context.installBundle(location, content.openStream());		
		} catch (BundleException e) {
			fail("failed to install test bundle", e); //$NON-NLS-1$
		} catch (IOException e) {
			fail("failed to install test bundle", e); //$NON-NLS-1$
		}
		return null;
	}

	private URL getBundleContent(String name) {
		URL entry = getContext().getBundle().getEntry(name);
		assertNotNull("Can not find bundle: " + name, entry);
		return entry;
	}

	public Object getService(BundleContext context, String serviceName) {
		assertNotNull("context is null!", context);
		ServiceReference ref = context.getServiceReference(serviceName);
		assertNotNull(serviceName + " reference is null!", ref);
		Object service = context.getService(ref);
		assertNotNull(serviceName + " is null!", service);
		return service;
	}

	public static void compareEvents(Object[] expectedEvents, Object[] actualEvents) {
		assertEquals("number of results", expectedEvents.length, actualEvents.length);
		for (int i = 0; i < actualEvents.length; i++) {
			if (expectedEvents[i] instanceof BundleEvent) {
				BundleEvent expected = (BundleEvent) expectedEvents[i];
				BundleEvent actual = (BundleEvent) actualEvents[i];
				assertEquals("Event Bundles", expected.getBundle(), actual.getBundle());
				assertEquals("Event Type", expected.getType(), actual.getType());
			} else if (expectedEvents[i] instanceof FrameworkEvent) {
				FrameworkEvent expected = (FrameworkEvent) expectedEvents[i];
				FrameworkEvent actual = (FrameworkEvent) actualEvents[i];
				assertEquals("Event Bundles", expected.getSource(), actual.getSource());
				assertEquals("Event Type", expected.getType(), actual.getType());
			}
		}
	}

	class SyncTestBundleListener extends TestBundleListener implements SynchronousBundleListener {
		public SyncTestBundleListener(Bundle target, int types) {
			super(target, types);
		}
	
		public boolean isSynchronous() {
			return true;
		}	
	}
	
	class TestBundleListener extends TestListener implements BundleListener {
		private final Bundle target;
		private final int types;
		
		public TestBundleListener(Bundle target, int types) {
			this.target = target;
			this.types = types;
		}
	
		public synchronized void bundleChanged(BundleEvent event) {
			if (target == event.getBundle() && (types == 0 || (types & event.getType()) != 0)) {
				events.add(event);
				notify();
			}
		}
	}
	
	class TestFrameworkListener extends TestListener implements FrameworkListener {
		private final int types;
		
		public TestFrameworkListener(int types) {
			this.types = types;
		}
	
		public synchronized void frameworkEvent(FrameworkEvent event) {
			if ((types == 0 || (types & event.getType()) != 0)) {
					events.add(event);
					notify();
			}
		}
	}
	
	class TestListener {
		protected final List events = new ArrayList();
		synchronized public Object[] getResults(Object[] results) {
			if (!isSynchronous())
			while (events.size() <= results.length) {
				// We wait for an event even when we already have the expected number
				// to make sure no more events are fired after the last one
				int currentSize = events.size();
				try {
					wait(1000);
				} catch (InterruptedException e) {
					// do nothing
				}
				if (currentSize == events.size())
					break; // no new events occurred; break out
			}
			assertEquals("Did not fire expected number of events", results.length, events.size());
			results = events.toArray(results);
			events.clear();
			return results;
		}
	
		protected boolean isSynchronous() {
			return false;
		}
	}
}
