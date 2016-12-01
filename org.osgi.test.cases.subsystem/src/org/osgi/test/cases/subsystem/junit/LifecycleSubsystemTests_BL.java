/*
 * Copyright (c) OSGi Alliance (2016). All Rights Reserved.
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


package org.osgi.test.cases.subsystem.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

import junit.framework.TestCase;

public class LifecycleSubsystemTests_BL implements SynchronousBundleListener{
	final Map<Bundle, List<BundleEvent>> events = new HashMap<Bundle, List<BundleEvent>>();
	final List<BundleEvent> orderedEvents = new ArrayList<BundleEvent>();
	final int captureEvents;

	public LifecycleSubsystemTests_BL(int captureEvents) {
		this.captureEvents = captureEvents;
	}
	public void bundleChanged(BundleEvent event) {
		if ((event.getType() & captureEvents) == 0)
			return;
		synchronized (events) {
			List<BundleEvent> e = events.get(event.getBundle());
			if (e == null) {
				e = new ArrayList<BundleEvent>();
				events.put(event.getBundle(), e);
			}
			e.add(event);
			orderedEvents.add(event);
		}
	}
	public void assertEvents(Bundle b, BundleEvent... expected) {
		List<BundleEvent> current;
		synchronized (events) {
			current = events.get(b);
			current = current == null ? new ArrayList<BundleEvent>() : new ArrayList<BundleEvent>(current);
		}
		TestCase.assertEquals("Wrong number of bundle events:" + b,
				expected.length, current.size());
		for (int i = 0; i < expected.length; i++) {
			assertEquals(i, expected[i], current.get(i));
		}
	}

	public void assertEvents(BundleEvent... expected) {
		List<BundleEvent> current;
		synchronized (events) {
			current = new ArrayList<BundleEvent>(orderedEvents);
		}
		TestCase.assertEquals("Wrong number of bundle events", expected.length,
				current.size());
		for (int i = 0; i < expected.length; i++) {
			assertEquals(i, expected[i], current.get(i));
		}
	}

	private void assertEquals(int index, BundleEvent expected, BundleEvent actual) {
		TestCase.assertEquals("Wrong bundle for event: " + index,
				expected.getBundle(), actual.getBundle());
		TestCase.assertEquals("Wrong bundle event type: " + index,
				expected.getType(), actual.getType());
	}

	public void clear() {
		synchronized (events) {
			events.clear();
			orderedEvents.clear();
		}
	}
}