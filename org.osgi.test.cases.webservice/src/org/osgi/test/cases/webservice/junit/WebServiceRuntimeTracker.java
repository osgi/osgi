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
package org.osgi.test.cases.webservice.junit;

import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.junit.jupiter.api.Assertions;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.webservice.runtime.WebserviceServiceRuntime;
import org.osgi.service.webservice.runtime.dto.RuntimeDTO;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This tracker is used to:
 * 
 * <ul>
 *   <li>Track a {@link WebserviceServiceRuntime}</li>
 *   <li>Wait for the {@link WebserviceServiceRuntime} changecount to change so that the caller can react to updates</li>
 *   <li>Register <em>setup</em>, <em>test</em> and <em>error</em> functions to allow for simple reactive tests</li>
 */
public class WebServiceRuntimeTracker {

	private final ServiceTracker<WebserviceServiceRuntime, WebserviceServiceRuntime> tracker;
	
	private final Semaphore updateNotifier = new Semaphore(0);
	
	private long updateCount = Long.MIN_VALUE;
	
	private boolean hasChangeCount = false;
	
	/**
	 * Used to synchronise when we need to deal with 
	 * updateCount and updateNotifier at the same time
	 */
	private final Object lock = new Object();
	
	/**
	 * Create a tracker for the supplied {@link WebserviceServiceRuntime} reference
	 * @param ctx
	 * @param ref
	 */
	public WebServiceRuntimeTracker(BundleContext ctx, ServiceReference<WebserviceServiceRuntime> ref) {
		
		tracker = new ServiceTracker<WebserviceServiceRuntime, WebserviceServiceRuntime>(ctx, ref, null) {

			@Override
			public WebserviceServiceRuntime addingService(ServiceReference<WebserviceServiceRuntime> reference) {
				Object count = ref.getProperty(Constants.SERVICE_CHANGECOUNT);
				
				if(count instanceof Long) {
					synchronized (lock) {
						hasChangeCount = true;
						updateCount = (Long) count; 
						updateNotifier.release();
					}
				} else {
					hasChangeCount = false;
				}
				return ctx.getService(ref);
			}

			@Override
			public void modifiedService(ServiceReference<WebserviceServiceRuntime> reference,
					WebserviceServiceRuntime service) {
				synchronized (lock) {
					Object update = ref.getProperty(Constants.SERVICE_CHANGECOUNT);
					if(update instanceof Long) {
						long newCount = (Long) update;
						// The count must increase
						if(newCount > updateCount) {
							hasChangeCount = true;
							updateCount = newCount;
							updateNotifier.release();
						}
					} else {
						updateCount = Long.MIN_VALUE;
						hasChangeCount = false;
					}
				}
			}
			
		};
		tracker.open();
	}
	
	public WebserviceServiceRuntime getServiceRuntime() {
		return tracker.getService();
	}
	
	/**
	 * @return a snapshot of the current change count
	 */
	public long getCurrentChangeCount() {
		synchronized (lock) {
			return updateCount;
		}
	}
	
	/**
	 * @return <code>true</code> if the service has a change count set
	 */
	public boolean hasChangeCount() {
		synchronized (lock) {
			return hasChangeCount;
		}
	}
	
	/**
	 * Pauses processing until the {@link WebserviceServiceRuntime} change count has stabilised
	 * and remained the same for given time period. Commonly used to ensure stability when preparing tests.
	 * @param time
	 * @param maxTime
	 * @param unit
	 * @throws InterruptedException
	 */
	public void waitForQuiet(long time, long maxTime, TimeUnit unit) throws InterruptedException {
		long start = System.nanoTime();
		
		for(;;) {
			synchronized (lock) {
				updateNotifier.drainPermits();
			}
			if(updateNotifier.tryAcquire(time, unit)) {
				long elapsed = unit.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
				if(elapsed > maxTime) {
					Assertions.fail("The WebserviceServiceRuntime did not settle within " + maxTime + " " + unit);
				}
			} else {
				break;
			}
		}
	}
	
	/**
	 * Performs the supplied action and then waits indefinitely for the {@link WebserviceServiceRuntime}
	 * change count to change.
	 * @param <T>
	 * @param action The action to perform before waiting
	 * @return the result of calling <code>action</code>
	 * @throws Exception
	 */
	public <T> T waitForChange(Callable<T> action) throws Exception {
		return waitForChange(action, (x,y) -> true);
	}

	/**
	 * Performs the supplied action and then waits indefinitely for the {@link WebserviceServiceRuntime}
	 * change count to change. Each time it changes the supplied test is run. If the test returns <code>true</code>
	 * then control will return to the caller.
	 * @param <T>
	 * @param action The action to perform before waiting
	 * @param test The test to run to see whether the update has completed. Receives the result of <code>action.call()</code> and
	 * the latest snapshot of the {@link RuntimeDTO}
	 * @return the result of calling <code>action</code>
	 * @throws Exception
	 */
	public <T> T waitForChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test) throws Exception {
		synchronized (lock) {
			updateNotifier.drainPermits();
		}
		T t = action.call();
		for(;;) {
			updateNotifier.acquire();
			if(test.test(t, getServiceRuntime().getRuntimeDTO())) {
				break;
			}
		}
		return t;
	}
	
	/**
	 * Performs the supplied action and then waits for up to the specified time for the {@link WebserviceServiceRuntime}
	 * change count to change.
	 * @param <T>
	 * @param action The action to perform before waiting
	 * @param maxTime the maximum time to wait
	 * @param unit the time unit for <code>maxTime</code>
	 * @return the result of calling <code>action</code>
	 * @throws Exception if no change occurred before the time expires
	 */
	public <T> T waitForChange(Callable<T> action, long maxTime, TimeUnit unit) throws Exception {
		return waitForChange(action, (x,y) -> true, maxTime, unit);
	}

	/**
	 * Performs the supplied action and then waits for up to the specified time for the {@link WebserviceServiceRuntime}
	 * change count to change. Each time it changes the supplied test is run. If the test returns <code>true</code>
	 * then control will return to the caller.
	 * @param <T>
	 * @param action The action to perform before waiting
	 * @param test The test to run to see whether the update has completed. Receives the result of <code>action.call()</code> and
	 * the latest snapshot of the {@link RuntimeDTO}
	 * @param maxTime the maximum time to wait
	 * @param unit the time unit for <code>maxTime</code>
	 * @return the result of calling <code>action</code>
	 * @throws Exception if no change occurred before the time expires
	 */
	public <T> T waitForChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test,
 			long maxTime, TimeUnit unit) throws Exception {
		return waitForChange(action, (x,y) -> true,
				(x,y) -> "The WebserviceServiceRuntime did not update within " + maxTime + " " + unit,
				maxTime, unit);
	}
	
	/**
	 * Performs the supplied action and then waits for up to the specified time for the {@link WebserviceServiceRuntime}
	 * change count to change. Each time it changes the supplied test is run. If the test returns <code>true</code>
	 * then control will return to the caller.
	 * @param <T>
	 * @param action The action to perform before waiting
	 * @param test The test to run to see whether the update has completed. Receives the result of <code>action.call()</code> and
	 * the latest snapshot of the {@link RuntimeDTO}
	 * @param errorMessage called when the timeout occurs to generate an error message
	 * @param maxTime the maximum time to wait
	 * @param unit the time unit for <code>maxTime</code>
	 * @return the result of calling <code>action</code>
	 * @throws Exception if no change occurred before the time expires
	 */
	public <T> T waitForChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test,
			BiFunction<T, RuntimeDTO, String> errorMessage, long maxTime, TimeUnit unit) throws Exception {
		synchronized (lock) {
			updateNotifier.drainPermits();
		}
		T t = action.call();
		long start = System.nanoTime();
		long remaining = unit.toNanos(maxTime);
		for(;;) {
			if(!updateNotifier.tryAcquire(remaining, TimeUnit.NANOSECONDS)) {
				Assertions.fail(errorMessage.apply(t, getServiceRuntime().getRuntimeDTO()));
			} else {
				if(test.test(t, getServiceRuntime().getRuntimeDTO())) {
					break;
				} else {
					remaining = unit.toNanos(maxTime) - (System.nanoTime() - start);
				}
			}
		}
		return t;
	}

	/**
	 * Wait for the specified time, failing if the change count is updated
	 * @param <T>
	 * @param action
	 * @param maxTime
	 * @param unit
	 * @return
	 * @throws Exception
	 */
	public <T> T waitForNoChange(Callable<T> action,
			long maxTime, TimeUnit unit) throws Exception {
		return waitForNoChange(action, (x,y) -> false, maxTime, unit);
	}
	
	/**
	 * Wait for the specified time, failing if the {@link WebserviceServiceRuntime} change count
	 * updates and the supplied test fails
	 * @param <T>
	 * @param action
	 * @param test
	 * @param maxTime
	 * @param unit
	 * @return
	 * @throws Exception
	 */
	public <T> T waitForNoChange(Callable<T> action, BiPredicate<T,RuntimeDTO> test,
			long maxTime, TimeUnit unit) throws Exception {
		synchronized (lock) {
			updateNotifier.drainPermits();
		}
		T t = action.call();
		long start = System.nanoTime();
		long remaining = unit.toNanos(maxTime);
		for(;;) {
			if(!updateNotifier.tryAcquire(remaining, TimeUnit.NANOSECONDS)) {
				break;
			} else {
				if(test.test(t, getServiceRuntime().getRuntimeDTO())) {
					remaining = unit.toNanos(maxTime) - (System.nanoTime() - start);
				} else {
					Assertions.fail("The WebserviceServiceRuntime unexpectedly changed within " + maxTime + " " + unit);
				}
			}
		}
		return t;
	}
	
	public void close() {
		tracker.close();
	}
}
