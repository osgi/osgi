/*
 * $Header$
 * 
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
package org.osgi.test.cases.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.service.ListenerHook;

public class TestRegistryListenerHook implements
		ListenerHook {
	public static TestRegistryListenerHook instance = new TestRegistryListenerHook();
	private List added = new ArrayList();
	private List removed = new ArrayList();

	private TestRegistryListenerHook() {
		// prevent outside construction
	}

	public synchronized void added(Collection listeners) {
		for (Iterator iListeners = listeners.iterator(); iListeners.hasNext();) {
			Bundle b = ((ListenerInfo) iListeners.next()).getBundleContext().getBundle();
			if (!added.contains(b))
				added.add(b);
		}
	}

	public synchronized void removed(Collection listeners) {
		for (Iterator iListeners = listeners.iterator(); iListeners.hasNext();) {
			Bundle b = ((ListenerInfo) iListeners.next()).getBundleContext().getBundle();
			if (!removed.contains(b))
				removed.add(b);
		}
	}
	public synchronized Bundle[] getAdded() {
		Bundle[] result = (Bundle[]) added.toArray(new Bundle[added.size()]);
		added.clear();
		return result;
	}

	public synchronized Bundle[] getRemoved() {
		Bundle[] result = (Bundle[]) removed.toArray(new Bundle[removed.size()]);
		removed.clear();
		return result;
	}
}
