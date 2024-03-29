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

import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class EventListenerTestResults extends TestResults implements BundleListener, FrameworkListener {
	private int	mask;
	public EventListenerTestResults(int mask) {
		this.mask = mask;
	}
	public void bundleChanged(BundleEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}

	public void frameworkEvent(FrameworkEvent event) {
		if ((event.getType() & mask) != 0)
			addEvent(event);
	}
}
