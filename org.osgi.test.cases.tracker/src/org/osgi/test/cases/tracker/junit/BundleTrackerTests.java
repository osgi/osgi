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

package org.osgi.test.cases.tracker.junit;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

public class BundleTrackerTests extends OSGiTestCase {

	public void testOpenClose() {
		BundleTracker bt = new BundleTracker(getContext(), Bundle.ACTIVE, null);
		int size = bt.size();
		assertEquals("size not zero", 0, size);
		Bundle[] bundles = bt.getBundles();
		assertNull("getBundles() not null", bundles);

		bt.open();
		try {
			size = bt.size();
			assertTrue("size zero", 0 < size);
			bundles = bt.getBundles();
			assertNotNull("getBundles() null", bundles);
			assertEquals("size() != getBundles().length", size, bundles.length);
			for (int i = 0, l = bundles.length; i < l; i++) {
				Bundle bundle = bundles[i];
				Object tracked = bt.getObject(bundle);
				assertEquals("default tracked != bundle", bundle, tracked);
			}
		}
		finally {
			bt.close();
		}
		size = bt.size();
		assertEquals("size not zero", 0, size);
		bundles = bt.getBundles();
		assertNull("getBundles() not null", bundles);

		bt.open();
		try {
			size = bt.size();
			assertTrue("size zero", 0 < size);
			bundles = bt.getBundles();
			assertNotNull("getBundles() null", bundles);
			assertEquals("size() != getBundles().length", size, bundles.length);
			for (int i = 0, l = bundles.length; i < l; i++) {
				Bundle bundle = bundles[i];
				Object tracked = bt.getObject(bundle);
				assertEquals("default tracked != bundle", bundle, tracked);
			}
		}
		finally {
			bt.close();
		}
		size = bt.size();
		assertEquals("size not zero", 0, size);
		bundles = bt.getBundles();
		assertNull("getBundles() not null", bundles);
	}

	public void testCustomizer() throws Exception {
		final boolean[] customizerCalled = new boolean[] {false, false, false};
		BundleTracker bt = new BundleTracker(getContext(), Bundle.ACTIVE,
				new BundleTrackerCustomizer() {

					public Object addingBundle(Bundle bundle, BundleEvent event) {
						synchronized (customizerCalled) {
							customizerCalled[0] = true;
						}
						return new BundleWrapper(bundle);
					}

					public void modifiedBundle(Bundle bundle,
							BundleEvent event, Object tracked) {
						synchronized (customizerCalled) {
							customizerCalled[1] = true;
						}
					}

					public void removedBundle(Bundle bundle, BundleEvent event,
							Object tracked) {
						synchronized (customizerCalled) {
							customizerCalled[2] = true;
						}
					}

				});
		testCustomizerInternal(bt, customizerCalled);
	}

	public void testSubclass() throws Exception {
		final boolean[] customizerCalled = new boolean[] {false, false, false};
		BundleTracker bt = new BundleTracker(getContext(), Bundle.ACTIVE, null) {

			public Object addingBundle(Bundle bundle, BundleEvent event) {
				synchronized (customizerCalled) {
					customizerCalled[0] = true;
				}
				return new BundleWrapper(bundle);
			}

			public void modifiedBundle(Bundle bundle, BundleEvent event,
					Object tracked) {
				synchronized (customizerCalled) {
					customizerCalled[1] = true;
				}
			}

			public void removedBundle(Bundle bundle, BundleEvent event,
					Object tracked) {
				synchronized (customizerCalled) {
					customizerCalled[2] = true;
				}
			}
		};
		testCustomizerInternal(bt, customizerCalled);
	}

	private void testCustomizerInternal(BundleTracker bt,
			boolean[] customizerCalled) throws Exception {
		synchronized (customizerCalled) {
			assertFalse("adding called", customizerCalled[0]);
			assertFalse("modified called", customizerCalled[1]);
			assertFalse("removed called", customizerCalled[2]);
		}

		Bundle[] bundles;
		bt.open();
		try {
			bundles = bt.getBundles();
			assertNotNull("getBundles() null", bundles);
			for (int i = 0, l = bundles.length; i < l; i++) {
				Bundle bundle = bundles[i];
				BundleWrapper tracked = (BundleWrapper) bt.getObject(bundle);
				assertEquals("tracked.getBundle() != bundle", bundle, tracked
						.getBundle());
			}
			synchronized (customizerCalled) {
				assertTrue("adding not called", customizerCalled[0]);
				customizerCalled[0] = false;
				assertFalse("modified called", customizerCalled[1]);
				assertFalse("removed called", customizerCalled[2]);
			}
		}
		finally {
			bt.close();
		}
		bundles = bt.getBundles();
		assertNull("getBundles() not null", bundles);
		synchronized (customizerCalled) {
			assertFalse("adding called", customizerCalled[0]);
			assertFalse("modified called", customizerCalled[1]);
			assertTrue("removed not called", customizerCalled[2]);
			customizerCalled[2] = false;
		}

		bt.open();
		try {
			bundles = bt.getBundles();
			assertNotNull("getBundles() null", bundles);
			for (int i = 0, l = bundles.length; i < l; i++) {
				Bundle bundle = bundles[i];
				BundleWrapper tracked = (BundleWrapper) bt.getObject(bundle);
				assertEquals("tracked.getBundle() != bundle", bundle, tracked
						.getBundle());
			}
			synchronized (customizerCalled) {
				assertTrue("adding not called", customizerCalled[0]);
				customizerCalled[0] = false;
				assertFalse("modified called", customizerCalled[1]);
				assertFalse("removed called", customizerCalled[2]);
			}

			Bundle tb1 = install("tb1.jar");
			try {
				synchronized (customizerCalled) {
					assertFalse("adding called", customizerCalled[0]);
					assertFalse("modified called", customizerCalled[1]);
					assertFalse("removed called", customizerCalled[2]);
				}
				tb1.start();
				synchronized (customizerCalled) {
					assertTrue("adding not called", customizerCalled[0]);
					customizerCalled[0] = false;
					assertFalse("modified called", customizerCalled[1]);
					assertFalse("removed called", customizerCalled[2]);
				}
				tb1.stop();
				synchronized (customizerCalled) {
					assertFalse("adding called", customizerCalled[0]);
					assertFalse("modified called", customizerCalled[1]);
					assertTrue("removed not called", customizerCalled[2]);
					customizerCalled[2] = false;
				}
				tb1.start();
				synchronized (customizerCalled) {
					assertTrue("adding not called", customizerCalled[0]);
					customizerCalled[0] = false;
					assertFalse("modified called", customizerCalled[1]);
					assertFalse("removed called", customizerCalled[2]);
				}
				bt.remove(tb1);
				synchronized (customizerCalled) {
					assertFalse("adding called", customizerCalled[0]);
					assertFalse("modified called", customizerCalled[1]);
					assertTrue("removed not called", customizerCalled[2]);
					customizerCalled[2] = false;
				}
			}
			finally {
				tb1.uninstall();
			}
		}
		finally {
			bt.close();
		}
		bundles = bt.getBundles();
		assertNull("getBundles() not null", bundles);
		synchronized (customizerCalled) {
			assertFalse("adding called", customizerCalled[0]);
			assertFalse("modified called", customizerCalled[1]);
			assertTrue("removed not called", customizerCalled[2]);
			customizerCalled[2] = false;
		}
	}

	public void testTrackingCount() {
		BundleTracker bt = new BundleTracker(getContext(), Bundle.ACTIVE, null);
		assertEquals("size not zero", 0, bt.size());
		assertEquals("tracking count not -1", -1, bt.getTrackingCount());
		bt.open();
		try {
			assertTrue("size zero", 0 < bt.size());
			int trackingCount = bt.getTrackingCount();
			assertTrue("tracking count zero", 0 < trackingCount);
			Bundle[] bundles = bt.getBundles();
			for (int i = 0, l = bundles.length; i < l; i++, trackingCount = bt
					.getTrackingCount()) {
				bt.remove(bundles[i]);
				assertTrue("tracking count not incremented", trackingCount < bt
						.getTrackingCount());
			}
			assertEquals("size not zero", 0, bt.size());
		}
		finally {
			bt.close();
		}
		assertEquals("tracking count not -1", -1, bt.getTrackingCount());
	}

	public void testEvents() throws Exception {
		Bundle[] bundles = getContext().getBundles();
		final List extraneousBundles = new ArrayList(bundles.length);
		for (int i = 0, l = bundles.length; i < l; i++) {
			Bundle b = bundles[i];
			if ((b.getState() & (Bundle.INSTALLED | Bundle.RESOLVED)) != 0) {
				extraneousBundles.add(b);
			}
		}
		final BundleEvent[] events = new BundleEvent[] {null, null, null};
		BundleTracker bt = new BundleTracker(getContext(), Bundle.INSTALLED
				| Bundle.RESOLVED,
				null) {

			public Object addingBundle(Bundle bundle, BundleEvent event) {
				if (extraneousBundles.contains(bundle)) {
					return null;
				}
				synchronized (events) {
					events[0] = event;
				}
				return bundle;
			}

			public void modifiedBundle(Bundle bundle, BundleEvent event,
					Object tracked) {
				synchronized (events) {
					events[1] = event;
				}
			}

			public void removedBundle(Bundle bundle, BundleEvent event,
					Object tracked) {
				synchronized (events) {
					events[2] = event;
				}
			}
		};
		synchronized (events) {
			assertNull("adding called", events[0]);
			assertNull("modified called", events[1]);
			assertNull("removed called", events[2]);
		}
		bt.open();
		try {
			bundles = bt.getBundles();
			assertNull("getBundles() not null", bundles);
			synchronized (events) {
				assertNull("adding called", events[0]);
				assertNull("modified called", events[1]);
				assertNull("removed called", events[2]);
			}
			Bundle tb1 = install("tb1.jar");
			try {
				synchronized (events) {
					assertNotNull("adding not called", events[0]);
					assertEquals("event type not installed",
							BundleEvent.INSTALLED, events[0].getType());
					assertEquals("event bundle not tb1", tb1, events[0]
							.getBundle());
					events[0] = null;
					assertNull("modified called", events[1]);
					assertNull("removed called", events[2]);
				}

				tb1.start();
				synchronized (events) {
					assertNull("adding called", events[0]);
					assertNotNull("modified not called", events[1]);
					assertEquals("event type not resolved",
							BundleEvent.RESOLVED, events[1].getType());
					assertEquals("event bundle not tb1", tb1, events[1]
							.getBundle());
					events[1] = null;
					assertNotNull("removed not called", events[2]);
					assertEquals("event type not started",
							BundleEvent.STARTING,
							events[2].getType());
					assertEquals("event bundle not tb1", tb1, events[2]
							.getBundle());
					events[2] = null;
				}

				tb1.stop();
				synchronized (events) {
					assertNotNull("adding not called", events[0]);
					assertEquals("event type not installed",
							BundleEvent.STOPPED, events[0].getType());
					assertEquals("event bundle not tb1", tb1, events[0]
							.getBundle());
					events[0] = null;
					assertNull("modified called", events[1]);
					assertNull("removed called", events[2]);
				}
			}
			finally {
				tb1.uninstall();
			}
			synchronized (events) {
				assertNull("adding called", events[0]);
				assertNotNull("modified not called", events[1]);
				assertEquals("event type not unresolved",
						BundleEvent.UNRESOLVED, events[1].getType());
				assertEquals("event bundle not tb1", tb1, events[1].getBundle());
				events[1] = null;
				assertNotNull("removed not called", events[2]);
				assertEquals("event type not uninstalled",
						BundleEvent.UNINSTALLED, events[2].getType());
				assertEquals("event bundle not tb1", tb1, events[2].getBundle());
				events[2] = null;
			}
		}
		finally {
			bt.close();
		}
		bundles = bt.getBundles();
		assertNull("getBundles() not null", bundles);
		synchronized (events) {
			assertNull("adding called", events[0]);
			assertNull("modified called", events[1]);
			assertNull("removed called", events[2]);
		}
	}

	static class BundleWrapper {
		private final Bundle	bundle;

		BundleWrapper(Bundle bundle) {
			this.bundle = bundle;
		}

		Bundle getBundle() {
			return bundle;
		}
	}
}
