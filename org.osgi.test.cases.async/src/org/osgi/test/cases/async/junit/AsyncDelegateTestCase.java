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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.test.cases.async.junit.impl.AsyncErrorListener;
import org.osgi.test.cases.async.junit.impl.MyServiceAsyncDelegateFactory;
import org.osgi.test.cases.async.junit.impl.MyServiceException;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class AsyncDelegateTestCase extends OSGiTestCase  {
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;

	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private LogReaderService logReader;
	private AsyncErrorListener asyncErrors;
	private ExecutorService asyncExecutor;
	
	private MyServiceAsyncDelegateFactory myServiceDelegateFactory;

	private ServiceRegistration<MyService> delegateReg;
	
	protected void setUp() throws InterruptedException {
		asyncExecutor = Executors.newSingleThreadExecutor();
		myServiceDelegateFactory = new MyServiceAsyncDelegateFactory(asyncExecutor);
		delegateReg = getContext().registerService(MyService.class, myServiceDelegateFactory, null);

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
		delegateReg.unregister();
		asyncExecutor.shutdown();

		asyncTracker.close();
		logReader.removeLogListener(asyncErrors);
		logReaderTracker.close();
	}

	public void testCallDelegateReturnPromise() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		Promise<Integer> p = async.call(service.countSlowly(2));
		assertFalse(p.isDone());
		// Note that the delegate impl 2x the results so we can detect it was called
		assertEquals("Wrong value.", 2 * 2, AsyncTestUtils.awaitResolve(p).intValue());

		assertEquals("Wrong method called.", "async", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testCallDelegateReturnPromiseFail() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		Promise<Integer> p = async.call(service.failSlowly(2));
		assertFalse(p.isDone());
		assertTrue("Wrong failture type.", AsyncTestUtils.awaitFailure(p) instanceof MyServiceException);

		assertEquals("Wrong method called.", "async", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_failSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testCallDelegateReturnNull() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		Promise<Integer> p = async.call(service.nonDelegateCountSlowly(2));
		assertFalse(p.isDone());
		assertEquals("Wrong value.", 2, AsyncTestUtils.awaitResolve(p).intValue());

		assertEquals("Wrong method called.", "async", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_nonDelegateCountSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testCallDelegateReturnNullFail() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		Promise<Integer> p = async.call(service.nonDelegateFailSlowly(2));
		assertFalse(p.isDone());
		assertTrue("Wrong failture type.", AsyncTestUtils.awaitFailure(p) instanceof MyServiceException);

		assertEquals("Wrong method called.", "async", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_nonDelegateFailSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testCallDelegateException() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		Promise<Integer> p = async.call(service.delegateFail());
		// fails instantaneously
		// assertFalse(p.isDone());
		assertEquals("Wrong failture type", AsyncTestUtils.awaitFailure(p).getClass(), MyServiceException.class);

		assertEquals("Wrong method called.", "async", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertNull("No method should have been called.", myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testExecuteDelegateReturnTrue() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		service.countSlowly(2);
		async.execute();

		myServiceDelegateFactory.awaitUngetService();
		assertEquals("Wrong method called.", "execute", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
	}

	public void testExecuteDelegateReturnTrueFail() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		service.failSlowly(2);
		// cannot check for logging here since the work is done on a thread outside of Async impl
		async.execute();

		assertEquals("Wrong method called.", "execute", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_failSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testExecuteDelegateReturnFalse() throws Exception {
		async.mediate(delegateReg.getReference(), MyService.class).nonDelegateCountSlowly(2);

		async.execute();

		myServiceDelegateFactory.awaitUngetService();
		assertEquals("Wrong method called.", "execute", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_nonDelegateCountSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
	}

	public void testExecuteDelegateReturnFalseFail() throws Exception {
		MyService service = async.mediate(delegateReg.getReference(), MyService.class);

		service.nonDelegateFailSlowly(2);
		// cannot check for logging here since the work is done on a thread outside of Async impl
		async.execute();

		assertEquals("Wrong method called.", "execute", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertEquals("Wrong method called.", MyService.METHOD_nonDelegateFailSlowly, myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
		myServiceDelegateFactory.awaitUngetService();
	}

	public void testExecuteDelegateException() throws Exception {
		async.mediate(delegateReg.getReference(), MyService.class).delegateFail();

		Promise<LogEntry> asyncErrorPromise = asyncErrors.getAsyncError();
		async.execute();
		Throwable error = AsyncTestUtils.awaitResolve(asyncErrorPromise).getException();
		assertTrue("Wrong error type: " + error, error instanceof MyServiceException);

		myServiceDelegateFactory.awaitUngetService();
		assertEquals("Wrong method called.", "execute", myServiceDelegateFactory.getMySerivceImpl().getDelegateMethodCalled());
		assertNull("No method should have been called.", myServiceDelegateFactory.getMySerivceImpl().lastMethodCalled());
	}
}
