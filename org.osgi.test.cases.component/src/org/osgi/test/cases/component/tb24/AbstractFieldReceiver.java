/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

package org.osgi.test.cases.component.tb24;

import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;

public abstract class AbstractFieldReceiver {

	private static final AtomicInteger activationCount = new AtomicInteger();
	private static final AtomicInteger modificationCount = new AtomicInteger();
	private static final AtomicInteger deactivationCount = new AtomicInteger();

	public AbstractFieldReceiver() {
		super();
	}

	void activate(ComponentContext context) {
		activationCount.incrementAndGet();
		System.out.printf("activate: %s[%X]\n", context.getProperties().get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void modified(ComponentContext context) {
		modificationCount.incrementAndGet();
		System.out.printf("modified: %s[%X]\n", context.getProperties().get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	void deactivate(ComponentContext context) {
		deactivationCount.incrementAndGet();
		System.out.printf("deactivate: %s[%X]\n", context.getProperties().get(ComponentConstants.COMPONENT_NAME),
				System.identityHashCode(this));
	}

	public int getActivationCount() {
		return activationCount.get();
	}

	public int getModificationCount() {
		return modificationCount.get();
	}

	public int getDeactivationCount() {
		return deactivationCount.get();
	}

}
