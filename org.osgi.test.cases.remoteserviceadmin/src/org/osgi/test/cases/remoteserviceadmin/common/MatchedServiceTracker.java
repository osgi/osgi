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

package org.osgi.test.cases.remoteserviceadmin.common;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * A {@link ServiceTracker} that keeps track of services that match a given predicate.
 *
 * @param <S> the service type
 */
public class MatchedServiceTracker<S> extends ServiceTracker<S, S> {

	private final Predicate<ServiceReference<S>> predicate;
	private final Set<S> matching = new HashSet<>();
	private final BiConsumer<ServiceReference<S>, S> action;

	public MatchedServiceTracker(BundleContext context, Filter filter,
			 Predicate<ServiceReference<S>> predicate, BiConsumer<ServiceReference<S>, S> action) {
		super(context, filter, null);
		this.predicate = predicate;
		this.action = action;
	}

	@Override
	public S addingService(ServiceReference<S> ref) {
		S service = super.addingService(ref);
		synchronized (matching) {
			if (predicate.test(ref) && matching.add(service)) {
				if (action != null) {
					action.accept(ref, service);
				}
				matching.notifyAll();
			}
		}
		return service;
	}

	@Override
	public void modifiedService(ServiceReference<S> ref, S service) {
		synchronized (matching) {
			if (!predicate.test(ref)) {
				matching.remove(service);
			} else if (matching.add(service)) {
				if (action != null) {
					action.accept(ref, service);
				}
				matching.notifyAll();
			}
		}
	}

	@Override
	public void removedService(ServiceReference<S> ref, S service) {
		synchronized (matching) {
			matching.remove(service);
		}
		super.removedService(ref, service);
	}

	public Set<S> getMatching() {
		synchronized (matching) {
			return new HashSet<>(matching);
		}
	}

	public S awaitMatch(long timeout, TimeUnit unit) throws InterruptedException {
		long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
		synchronized (matching) {
			while (matching.isEmpty()) {
				long remaining = deadline - System.currentTimeMillis();
				if (remaining <= 0)
					return null;
				matching.wait(remaining);
			}
			return matching.iterator().next();
		}
	}
}
