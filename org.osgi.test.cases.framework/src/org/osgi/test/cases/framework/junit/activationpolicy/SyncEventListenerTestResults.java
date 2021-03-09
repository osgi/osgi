/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.framework.junit.activationpolicy;

import java.util.ArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;

public class SyncEventListenerTestResults extends EventListenerTestResults implements SynchronousBundleListener{
	private boolean getBundleContext = false;
	private ArrayList<BundleContext>	contexts			= new ArrayList<>();

	public SyncEventListenerTestResults(int mask) {
		super(mask);
	}

	public SyncEventListenerTestResults(int mask, boolean getBundleContext) {
		this(mask);
		this.getBundleContext = getBundleContext;
	}

	public synchronized void bundleChanged(BundleEvent event) {
		if (getBundleContext && event.getType() == BundleEvent.LAZY_ACTIVATION)
			contexts.add(event.getBundle().getBundleContext());
		super.bundleChanged(event);
	}

	public synchronized BundleContext[] getContexts() {
		BundleContext[] results = contexts.toArray(new BundleContext[contexts.size()]);
		contexts.clear();
		return results;
	}

	protected boolean isSynchronous() {
		return true;
	}
}
