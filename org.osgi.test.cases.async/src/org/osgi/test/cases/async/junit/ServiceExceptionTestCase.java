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
package org.osgi.test.cases.async.junit;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.test.cases.async.junit.impl.AsyncErrorListener;
import org.osgi.test.cases.async.junit.impl.MyServiceFactory;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceExceptionTestCase extends OSGiTestCase {
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;

	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private LogReaderService logReader;
	private AsyncErrorListener asyncErrors;

	private Bundle asyncBundle;
	private Bundle clientBundle;

	private MyServiceFactory factory;
	private ServiceRegistration<MyService> factoryReg;

	protected void setUp() throws InterruptedException, BundleException, IOException {
		factory = new MyServiceFactory();
		factoryReg = getContext().registerService(MyService.class, factory, null);

		asyncTracker = new ServiceTracker<Async, Async>(getContext(), Async.class, null);
		asyncTracker.open();
		async = asyncTracker.waitForService(5000);
		asyncBundle = asyncTracker.getServiceReference().getBundle();

		assertNotNull("No Async service available within 5 seconds", async);

		clientBundle = install("tb1.jar");
		clientBundle.start();

		logReaderTracker = new ServiceTracker<LogReaderService, LogReaderService>(getContext(), LogReaderService.class, null);
		logReaderTracker.open();
		logReader = logReaderTracker.waitForService(5000);
		assertNotNull("No LogReaderService available within 5 seconds", logReader);
		logReader.addLogListener(asyncErrors  = new AsyncErrorListener());
	}
	
	protected void tearDown() {
		asyncTracker.close();

		logReader.removeLogListener(asyncErrors);
		logReaderTracker.close();

		try {
			factoryReg.unregister();
		} catch (IllegalStateException e) {
			// may have been unregistered as part of the test
		}
		try {
			clientBundle.stop();
		} catch (BundleException e) {
			// did out best
		}
		// Note that we don't uninstall clientBundle each time
	}

	private Async getClientAsync() {
		BundleContext context = clientBundle.getBundleContext();
		ServiceReference<Async> asyncRef = context.getServiceReference(Async.class);
		assertNotNull("No Async service for client.", asyncRef);
		Async clientAsync = context.getService(asyncRef);
		assertNotNull("Client Async is null.", clientAsync);
		return clientAsync;
	}

	public void testAsyncCallAfterUnregistered() throws Exception {
		MyService mediated = async.mediate(factoryReg.getReference(), MyService.class);
		int mediatedResult = mediated.countSlowly(10);
		factoryReg.unregister();

		AsyncTestUtils.checkForAsyncFailure(async.call(mediatedResult));
	}

	public void testAsyncExecuteAfterUnregistered() throws Exception {
		async.mediate(factoryReg.getReference(), MyService.class).countSlowly(10);

		factoryReg.unregister();

		Promise<LogEntry> asyncErrorPromise = asyncErrors.getAsyncError();
		async.execute();
		AsyncTestUtils.checkForAsyncFailure(AsyncTestUtils.awaitResolve(asyncErrorPromise).getException());
	}

	public void testAsyncCallWhenFactoryReturnsNull() throws Exception {
		MyService mediated = async.mediate(factoryReg.getReference(), MyService.class);
		int mediatedResult = mediated.countSlowly(10);
		factory.setReturnNull(true);

		AsyncTestUtils.checkForAsyncFailure(async.call(mediatedResult));
	}

	public void testAsyncExecuteWhenFactoryReturnsNull() throws Exception {
		async.mediate(factoryReg.getReference(), MyService.class).countSlowly(10);

		factory.setReturnNull(true);

		Promise<LogEntry> asyncErrorPromise = asyncErrors.getAsyncError();
		async.execute();
		AsyncTestUtils.checkForAsyncFailure(AsyncTestUtils.awaitResolve(asyncErrorPromise).getException());
	}

	public void testAsyncCallWhenImplStopped() throws Exception {
		// Note that no test is done in this case for execute because there is no way
		// the impl can log after it is stopped.
		MyService mediated = async.mediate(factoryReg.getReference(), MyService.class);
		int mediatedResult = mediated.countSlowly(10);
		try {
			asyncBundle.stop();
			AsyncTestUtils.checkForAsyncFailure(async.call(mediatedResult));
		} finally {
			asyncBundle.start();
		}
	}

	public void testAsyncCallWhenClientStopped() throws Exception {
		Async clientAsync = getClientAsync();
		MyService mediated = clientAsync.mediate(factoryReg.getReference(), MyService.class);
		int mediatedResult = mediated.countSlowly(10);
		try {
			clientBundle.stop();
			AsyncTestUtils.checkForAsyncFailure(clientAsync.call(mediatedResult));
		} finally {
			clientBundle.start();
		}
	}

	public void testAsyncExecuteWhenClientStopped() throws Exception {
		Async clientAsync = getClientAsync();
		clientAsync.mediate(factoryReg.getReference(), MyService.class).countSlowly(10);
		try {
			clientBundle.stop();
			Promise<LogEntry> asyncErrorPromise = asyncErrors.getAsyncError();
			clientAsync.execute();
			AsyncTestUtils.checkForAsyncFailure(AsyncTestUtils.awaitResolve(asyncErrorPromise).getException());
		} finally {
			clientBundle.start();
		}
	}

	// TODO not possible for impl to log when it is stopped!
	// public void testAsyncExecuteWhenImplStopped()

	// TODO This may be impossible since it is not possible to create a mediator with an invalid method
	//public void testAsyncCallClassWithInvalidTypes()
	//public void testAsyncExecuteClassWithInvalidTypes()
}
