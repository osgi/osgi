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
package org.osgi.test.cases.jakartars.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ServiceUpdateHelper {

	ServiceTracker<JakartarsServiceRuntime,Set<Deferred<Void>>> tracker;

	public ServiceUpdateHelper(BundleContext context) {
		tracker = new ServiceTracker<>(context, JakartarsServiceRuntime.class,
				new ServiceTrackerCustomizer<JakartarsServiceRuntime,Set<Deferred<Void>>>() {

					@Override
					public Set<Deferred<Void>> addingService(
							ServiceReference<JakartarsServiceRuntime> reference) {
						return new HashSet<>();
					}

					@Override
					public void modifiedService(
							ServiceReference<JakartarsServiceRuntime> reference,
							Set<Deferred<Void>> service) {
						resolveAll(service);
					}

					@Override
					public void removedService(
							ServiceReference<JakartarsServiceRuntime> reference,
							Set<Deferred<Void>> service) {
						resolveAll(service);
					}

					private void resolveAll(Set<Deferred<Void>> pending) {
						List<Deferred<Void>> toResolve;
						synchronized (pending) {
							toResolve = new ArrayList<>(pending);
							pending.clear();
						}
						for (Deferred<Void> d : toResolve) {
							d.resolve(null);
						}
					}
				});
	}

	public void open() {
		tracker.open();
	}

	public void close() {
		tracker.close();
	}

	public Promise<Void> awaitModification(
			ServiceReference<JakartarsServiceRuntime> whiteboard, long time) {

		final Set<Deferred<Void>> pending = tracker.getService(whiteboard);
		if (pending == null) {
			return Promises.failed(new IllegalStateException(
					"No whiteboard for " + whiteboard));
		}

		final Deferred<Void> deferred = new Deferred<>();
		synchronized (pending) {
			pending.add(deferred);
		}

		return deferred.getPromise().timeout(time).onResolve(new Runnable() {
			public void run() {
				synchronized (pending) {
					pending.remove(deferred);
				}
			}
		});
	}

	public ServiceReference<JakartarsServiceRuntime> awaitRuntime(long time) {
		try {
			Object o = tracker.waitForService(time);
			if (o != null) {
				ServiceReference<JakartarsServiceRuntime> ref = tracker
						.getServiceReference();
				if (ref != null) {
					return ref;
				}
			}
		} catch (InterruptedException e) {}
		throw new RuntimeException("No JaxrsServiceRuntime");
	}
}
