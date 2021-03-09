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
package org.osgi.test.cases.async.secure.tb;

import java.util.Hashtable;
import java.util.concurrent.Callable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.async.Async;
import org.osgi.test.cases.async.secure.services.MyService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private ServiceTracker<Async, Async>	asyncTracker;

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void start(BundleContext context) throws Exception {
		String name = context.getBundle().getHeaders("").get(Constants.BUNDLE_NAME);
		asyncTracker = new ServiceTracker<Async, Async>(context, Async.class, null);
		asyncTracker.open();
		final Async async = asyncTracker.getService();
		final ServiceReference<MyService> myServiceRef = context.getServiceReference(MyService.class);
		final Object myService = context.getService(myServiceRef);

		Hashtable<String, String> props = new Hashtable<String, String>();

		props.put(MyService.TEST_KEY, MyService.TEST_mediateReferenceWithSecurityCheck + "_" + name);
		context.registerService(Callable.class.getName(), new Callable<Object>() {
			public Object call() throws Exception {
				return async.<MyService> mediate(myServiceRef, (Class) myService.getClass());
			}
		}, props);

		props.put(MyService.TEST_KEY, MyService.TEST_mediateDirectWithSecurityCheck + "_" + name);
		context.registerService(Callable.class.getName(), new Callable() {
			public Object call() throws Exception {
				return async.mediate(myService, (Class<Object>) myService.getClass());
			}
		}, props);

		props.put(MyService.TEST_KEY, MyService.TEST_callWithSecurityCheck + "_" + name);
		context.registerService(Callable.class.getName(), new Callable() {
			public Object call() throws Exception {
				return async.call(async.mediate(myServiceRef, MyService.class).countSlowly(2, true));
			}
		}, props);

		props.put(MyService.TEST_KEY, MyService.TEST_callWithoutSecurityCheck + "_" + name);
		context.registerService(Callable.class.getName(), new Callable() {
			public Object call() throws Exception {
				return async.call(async.mediate(myServiceRef, MyService.class).countSlowly(2, false));
			}
		}, props);

		props.put(MyService.TEST_KEY, MyService.TEST_executeWithSecurityCheck + "_" + name);
		context.registerService(Callable.class.getName(), new Callable() {
			public Object call() throws Exception {
				async.mediate(myServiceRef, MyService.class).countSlowly(2, true);
				async.execute();
				return null;
			}
		}, props);

		props.put(MyService.TEST_KEY, MyService.TEST_executeWithoutSecurityCheck + "_" + name);
		context.registerService(Callable.class.getName(), new Callable() {
			public Object call() throws Exception {
				async.mediate(myServiceRef, MyService.class).countSlowly(2, false);
				async.execute();
				return null;
			}
		}, props);
	}

	public void stop(BundleContext context) throws Exception {
		asyncTracker.close();
	}

}
