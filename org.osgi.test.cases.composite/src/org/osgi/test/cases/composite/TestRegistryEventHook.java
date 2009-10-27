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
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.hooks.service.EventHook;

public class TestRegistryEventHook implements EventHook {
	public static final TestRegistryEventHook instance = new TestRegistryEventHook();
	private List bundles = new ArrayList();

	private TestRegistryEventHook() {
		// prevent outside construction;
	}

	public synchronized void event(ServiceEvent event, Collection contexts) {
		if (!bundles.contains(event.getServiceReference().getBundle()))
			bundles.add(event.getServiceReference().getBundle());
	}
	public synchronized Bundle[] getBundles() {
		Bundle[] result = (Bundle[]) bundles.toArray(new Bundle[bundles.size()]);
		bundles.clear();
		return result;
	}
}
