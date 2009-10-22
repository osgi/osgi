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
import java.util.Dictionary;
import java.util.Enumeration;
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
	protected ServiceReference paRef;
	protected PackageAdmin pa;

	public void setUp() {
		compRef = getContext().getServiceReference(CompositeAdmin.class.getName());
		assertNotNull(compRef);
		compAdmin = (CompositeAdmin) getContext().getService(compRef);
		assertNotNull(compAdmin);
		paRef = getContext().getServiceReference(PackageAdmin.class.getName());
		assertNotNull(paRef);
		pa = (PackageAdmin) getContext().getService(paRef);
		assertNotNull(pa);
	}

	public void tearDown() {
		if (compAdmin != null)
			getContext().ungetService(compRef);
		compAdmin = null;
		compRef = null;
		refreshRootPackages();
		if (pa != null)
			getContext().ungetService(paRef);
		pa = null;
		paRef = null;
	}

	public void refreshRootPackages() {
		TestFrameworkListener listener = new TestFrameworkListener(FrameworkEvent.PACKAGES_REFRESHED);
		getContext().addFrameworkListener(listener);
		pa.refreshPackages(null);
		listener.getResults(new FrameworkEvent[1], false);
	}

	public CompositeBundle createCompositeBundle(CompositeBundle parent, String location, Map compositeManifest, Map configuration, String[] constituents) {
		CompositeAdmin ca = (CompositeAdmin) (parent == null ? compAdmin : getService(parent.getSystemBundleContext(), CompositeAdmin.class.getName()));
		CompositeBundle composite = createCompositeBundle(ca, location, compositeManifest, configuration);
		for (int i = 0; i < constituents.length; i++)
			installConstituent(composite, location + '.' + constituents[i], constituents[i]);
		return composite;
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
				assertTrue("Bundle is in the wrong state: " + bundles[i].getLocation(), ((Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING) & bundles[i].getState()) == 0);
			else
				assertEquals("System Bundle is in the wrong state", Bundle.STARTING, bundles[i].getState());
		}

	}

	protected void updateCompositeBundle(CompositeBundle composite, Map manifest) {
		Bundle[] bundles = composite.getSystemBundleContext().getBundles();
		int previousState = composite.getState();
		try {
			composite.update(manifest);
		} catch (BundleException e) {
			fail("Unexpected error updating composite", e); //$NON-NLS-1$
		}
		if (previousState == Bundle.ACTIVE)
			return;
		// Bundles must not be active
		for (int i = 0; i < bundles.length; i++) {
			if (bundles[i].getBundleId() != 0)
				assertTrue("Bundle is in the wrong state: " + bundles[i].getLocation(), ((Bundle.STARTING | Bundle.ACTIVE | Bundle.STOPPING) & bundles[i].getState()) == 0);
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

	private Bundle[] installConstituents(CompositeBundle composite, String[] names) {
		Bundle[] bundles = new Bundle[0];
		if (names != null) {
			bundles = new Bundle[names.length];
			for (int i = 0; i < names.length; i++) {
				if (composite != null)
					bundles[i] = installConstituent(composite, null, names[i]);
				else
					try {
						bundles[i] = install(names[i]);
					} catch (Exception e) {
						fail("Unexpected installing bundle: " + names[i], e);
					}
			}
		}
		return bundles;
	}
	private void startConstituents(Bundle[] bundles) {
		int i = 0;
		try {
			for (; i < bundles.length; i++)
				bundles[i].start();
		} catch (BundleException e) {
			fail("Unexpected error starting bundle: " + bundles[i].getLocation(), e);
		}
	}

	protected void doTestExportImportPolicy01(Map manifestExport, Map manifestImport, String[] exportNames, String[] importNames, String clientName, boolean clientFail, TestHandler handler) {
		// test export policy to parent and import policy to another composite
		CompositeBundle compositeExport = createCompositeBundle(compAdmin, getName() + "_export", manifestExport, null);
		startCompositeBundle(compositeExport);
		Bundle[] exportConstituents = installConstituents(compositeExport, exportNames);

		CompositeBundle compositeImport = createCompositeBundle(compAdmin, getName() + "_import", manifestImport, null);
		startCompositeBundle(compositeImport);
		Bundle[] importConstituents = installConstituents(compositeImport, importNames);
		
		startConstituents(exportConstituents);
		startConstituents(importConstituents);

		Bundle client = installConstituent(compositeImport, null, clientName);

		try {
			client.start();
			if (clientFail)
				fail("Expected client to fail to start: " + client.getLocation());
		} catch (BundleException e) {
			if (clientFail) {
				if (handler != null)
					handler.handleException(e);
			} else
				fail("Unexpected error starting", e);
		}
		if (handler != null)
			handler.handleBundles(exportConstituents, importConstituents, client);
		uninstallCompositeBundle(compositeExport);
		uninstallCompositeBundle(compositeImport);
	}

	protected void doTestExportImportPolicy02(Map manifestExport1, Map manifestExport2, Map manifestImport1, Map manifestImport2, String[] exportNames, String[] importNames, String clientName, boolean clientFail, TestHandler handler) {
		// test export policy to a parent from two level nested composite 
		// and import policy into another two level nested composite
		CompositeBundle compositeExport1 = createCompositeBundle(compAdmin, getName() + "_export1", manifestExport1, null);
		startCompositeBundle(compositeExport1);
		CompositeAdmin compAdminExport1 = (CompositeAdmin) getService(compositeExport1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle compositeExport2 = createCompositeBundle(compAdminExport1, getName() + "_export2", manifestExport2, null);
		startCompositeBundle(compositeExport2);
		Bundle[] exportConstituents = installConstituents(compositeExport2, exportNames);

		CompositeBundle compositeImport1 = createCompositeBundle(compAdmin, getName() + "_import1", manifestImport1, null);
		startCompositeBundle(compositeImport1);
		CompositeAdmin compAdminImport1 = (CompositeAdmin) getService(compositeImport1.getSystemBundleContext(), CompositeAdmin.class.getName());
		CompositeBundle compositeImport2 = createCompositeBundle(compAdminImport1, getName() + "_import2", manifestImport2, null);
		startCompositeBundle(compositeImport2);
		Bundle[] importConstituents = installConstituents(compositeImport2, importNames);
		
		startConstituents(exportConstituents);
		startConstituents(importConstituents);

		Bundle client = installConstituent(compositeImport2, null, clientName);
		try {
			client.start();
			if (clientFail)
				fail("Expected client to fail to start: " + client.getLocation());
		} catch (BundleException e) {
			if (clientFail) {
				if (handler != null)
					handler.handleException(e);
			} else
				fail("Unexpected error starting", e);
		}
		if (handler != null)
			handler.handleBundles(exportConstituents, importConstituents, client);
		uninstallCompositeBundle(compositeExport1);
		uninstallCompositeBundle(compositeImport1);
	}

	protected void doTestExportPolicy01(Map manifest, String[] exportNames, String[] importNames, String clientName, boolean clientFail, TestHandler handler) {
		// Test export policy to parent
		Bundle client = null;
		try {
			client = install(clientName);
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		Bundle[] parentBundles = installConstituents(null, importNames);
		try {
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle[] constituentBundles = installConstituents(composite, exportNames);

			startConstituents(parentBundles);
			startConstituents(constituentBundles);

			try {
				client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + client.getLocation());
			} catch (BundleException e) {
				if (clientFail) {
					if (handler != null)
						handler.handleException(e);
				} else
					fail("Unexpected error starting", e);
			}
			if (handler != null)
				handler.handleBundles(constituentBundles, parentBundles, client);
			uninstallCompositeBundle(composite);
		} finally {
			try {
				client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			for (int i = 0; i < parentBundles.length; i++)
				try {
					parentBundles[i].uninstall();
				} catch (BundleException e) {
					// nothing
				}
		}
	}

	protected void doTestExportPolicy02(Map manifest1, Map manifest2, String[] exportNames, String[] importNames, String clientName, boolean clientFail, TestHandler handler) {
		// test export policy to parent from two level nested composite
		Bundle client = null;
		try {
			client = install(clientName);
		} catch (Exception e) {
			fail("Unexpected error installing test bundle", e);
		}
		Bundle[] parentBundles = installConstituents(null, importNames);
		try {
			CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + "_1", manifest1, null);
			startCompositeBundle(composite1);
			CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
			CompositeBundle composite2 = createCompositeBundle(compAdmin1, getName() + "_2", manifest2, null);
			startCompositeBundle(composite2);
			Bundle[] constituentBundles = installConstituents(composite2, exportNames);

			startConstituents(parentBundles);
			startConstituents(constituentBundles);

			try {
				client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + client.getLocation());
			} catch (BundleException e) {
				if (!clientFail)
					fail("Unexpected error starting", e);
			}
			if (handler != null)
				handler.handleBundles(constituentBundles, parentBundles, client);
			uninstallCompositeBundle(composite1);
		} finally {
			try {
				client.uninstall();
			} catch (BundleException e) {
				// nothing
			}
			for (int i = 0; i < parentBundles.length; i++)
				try {
					parentBundles[i].uninstall();
				} catch (BundleException e) {
					// nothing
				}
		}
	}

	protected void doTestImportPolicy01(Map manifest, String[] exportNames, String[] importNames, String clientName, boolean clientFail, TestHandler handler) {
		// Test import policy into a composite
		Bundle[] parentBundles = installConstituents(null, exportNames);
		try {
			CompositeBundle composite = createCompositeBundle(compAdmin, getName(), manifest, null);
			startCompositeBundle(composite);
			Bundle[] constituentBundles = installConstituents(composite, importNames);
			Bundle client = installConstituent(composite, null, clientName);

			startConstituents(parentBundles);
			startConstituents(constituentBundles);

			try {
				client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + client.getLocation());
			} catch (BundleException e) {
				if (clientFail) {
					if (handler != null)
						handler.handleException(e);
				} else
					fail("Unexpected error starting", e);
			}
			if (handler != null)
				handler.handleBundles(parentBundles, constituentBundles, client);
			uninstallCompositeBundle(composite);
		} finally {
			for (int i = 0; i < parentBundles.length; i++)
				try {
					parentBundles[i].uninstall();
				} catch (BundleException e) {
					// nothing
				}
		}
	}

	protected void doTestImportPolicy02(Map manifest1, Map manifest2, String[] exportNames, String[] importNames, String clientName, boolean clientFail, TestHandler handler) {
		// Test import policy into two level nested composite
		Bundle[] parentBundles = installConstituents(null, exportNames);
		try {
			CompositeBundle composite1 = createCompositeBundle(compAdmin, getName() + "_1", manifest1, null);
			startCompositeBundle(composite1);
			CompositeAdmin compAdmin1 = (CompositeAdmin) getService(composite1.getSystemBundleContext(), CompositeAdmin.class.getName());
			CompositeBundle composite2 = createCompositeBundle(compAdmin1, getName() + "_2", manifest2, null);
			startCompositeBundle(composite2);
			Bundle[] constituentBundles = installConstituents(composite2, importNames);
			Bundle client = installConstituent(composite2, "tb3client", clientName);

			startConstituents(parentBundles);
			startConstituents(constituentBundles);

			try {
				client.start();
				if (clientFail)
					fail("Expected client to fail to start: " + client.getLocation());
			} catch (BundleException e) {
				if (clientFail) {
					if (handler != null)
						handler.handleException(e);
				} else
					fail("Unexpected error starting: " +client.getLocation(), e);
			}
			if (handler != null)
				handler.handleBundles(parentBundles, constituentBundles, client);
			uninstallCompositeBundle(composite1);
		} finally {
			for (int i = 0; i < parentBundles.length; i++)
				try {
					parentBundles[i].uninstall();
				} catch (BundleException e) {
					// nothing
				}
		}
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

	public static Map createMap(Dictionary dictionary) {
		Map result = new HashMap();
		for (Enumeration keys = dictionary.keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			result.put(key, dictionary.get(key));
		}
		return result;
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
			return getResults(results, true);
		}

		synchronized public Object[] getResults(Object[] results, boolean ensureLast) {
			if (!isSynchronous())
				while (events.size() <= results.length) {
					if (!ensureLast && events.size() == results.length)
						break; // do not wait after last
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
