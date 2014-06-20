/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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
package org.osgi.test.cases.async.junit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.test.cases.async.junit.impl.AsyncErrorListener;
import org.osgi.test.cases.async.junit.impl.MyServiceAsyncDelegate;
import org.osgi.test.cases.async.junit.impl.MyServiceException;
import org.osgi.test.cases.async.junit.impl.MyServiceFactory;
import org.osgi.test.cases.async.junit.impl.MyServiceImpl;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class AsyncTestCase extends OSGiTestCase {
	
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;

	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private LogReaderService logReader;
	private AsyncErrorListener asyncErrors;
	private ExecutorService asyncExecutor;
	
	private MyServiceImpl myServiceImpl;
	private MyServiceAsyncDelegate myServiceDelegate;
	private MyServiceFactory myServiceFactory;

	private ServiceRegistration<MyService> normalReg;
	private ServiceRegistration<MyService> delegateReg;
	private ServiceRegistration<MyService> factoryReg;
	
	protected void setUp() throws InterruptedException {
		myServiceImpl = new MyServiceImpl();
		normalReg = getContext().registerService(MyService.class, myServiceImpl, null);

		asyncExecutor = Executors.newSingleThreadExecutor();
		myServiceDelegate = new MyServiceAsyncDelegate(asyncExecutor);
		delegateReg = getContext().registerService(MyService.class, myServiceDelegate, null);

		myServiceFactory = new MyServiceFactory();
		factoryReg = getContext().registerService(MyService.class, myServiceFactory, null);

		asyncTracker = new ServiceTracker<Async, Async>(getContext(), Async.class, null);
		asyncTracker.open();
		async = asyncTracker.waitForService(5000);

		assertNotNull("No Async service available within 5 seconds", async);

		logReaderTracker = new ServiceTracker<LogReaderService, LogReaderService>(getContext(), LogReaderService.class, null);
		logReaderTracker.open();
		logReader = logReaderTracker.waitForService(5000);
		assertNotNull("No LogReaderService available within 5 seconds", logReader);
		logReader.addLogListener(asyncErrors  = new AsyncErrorListener());
	}
	
	protected void tearDown() {

		normalReg.unregister();
		delegateReg.unregister();
		factoryReg.unregister();

		asyncExecutor.shutdown();

		asyncTracker.close();
		logReader.removeLogListener(asyncErrors);
		logReaderTracker.close();
	}
	
	/**
	 * A basic test that demonstrates that a call can be made asynchronously
	 * @throws Exception
	 */
	public void testAsyncCall() throws Exception {
		
		MyService service = async.mediate(normalReg.getReference());
		
		// This call waits for a second
		Promise<Integer> p = async.call(service.countSlowly(2));
		
		// The promise should not have resolved yet. Theoretically this is
		// a race, but it should not take over a second to get here from the
		// previous statement.
		assertFalse(p.isDone());
		
		assertEquals("Wrong value.", 2, AsyncTestUtils.awaitResolve(p).intValue());
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myServiceImpl.lastMethodCalled());
	}

	public void testAsyncCallGetUngetServiceCalls() throws Exception {
		MyService service = async.mediate(factoryReg.getReference());

		Promise<Integer> p = async.call(service.countSlowly(2));
		assertFalse(p.isDone());
		assertEquals("Wrong value.", 2, AsyncTestUtils.awaitResolve(p).intValue());

		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myServiceFactory.getMySerivceImpl().lastMethodCalled());
		myServiceFactory.awaitUngetService();
	}

	public void testAsyncExecuteGetUngetServiceCalls() throws Exception {
		async.mediate(factoryReg.getReference()).countSlowly(2);

		async.execute();

		myServiceFactory.awaitUngetService();
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myServiceFactory.getMySerivceImpl().lastMethodCalled());
	}

	public void testAsyncCallGetUngetServiceCallsWithFailure() throws Exception {
		MyService service = async.mediate(factoryReg.getReference());

		Promise<Integer> p = async.call(service.failSlowly(2));
		assertFalse(p.isDone());
		assertTrue("Wrong failture type.", AsyncTestUtils.awaitFailure(p) instanceof MyServiceException);

		assertEquals("Wrong method called.", MyService.METHOD_failSlowly, myServiceFactory.getMySerivceImpl().lastMethodCalled());
		myServiceFactory.awaitUngetService();
	}

	public void testAsyncExecuteGetUngetServiceCallsWithFailure() throws Exception {
		async.mediate(factoryReg.getReference()).failSlowly(2);

		Promise<LogEntry> asyncErrorPromise = asyncErrors.getAsyncError();
		async.execute();
		Throwable error = AsyncTestUtils.awaitResolve(asyncErrorPromise).getException();
		assertTrue("Wrong error type: " + error, error instanceof MyServiceException);

		myServiceFactory.awaitUngetService();
		assertEquals("Wrong method called.", MyService.METHOD_failSlowly, myServiceFactory.getMySerivceImpl().lastMethodCalled());
	}
}
