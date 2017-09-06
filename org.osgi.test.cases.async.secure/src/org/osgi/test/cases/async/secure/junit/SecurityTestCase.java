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
package org.osgi.test.cases.async.secure.junit;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.async.Async;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.test.cases.async.secure.junit.impl.AsyncErrorListener;
import org.osgi.test.cases.async.secure.junit.impl.MyServiceImpl;
import org.osgi.test.cases.async.secure.services.MyService;
import org.osgi.test.support.OSGiTestCase;
import org.osgi.util.promise.Promise;
import org.osgi.util.tracker.ServiceTracker;

public class SecurityTestCase extends OSGiTestCase {
	private ServiceTracker<Async, Async>	asyncTracker;
	private Async	async;

	private ServiceTracker<LogReaderService, LogReaderService> logReaderTracker;
	private LogReaderService logReader;
	private AsyncErrorListener asyncErrors;

	private MyServiceImpl myService;
	private ServiceRegistration<MyService> normalReg;

	private Bundle tb1Bundle;
	private Bundle tb2Bundle;
	
	protected void setUp() throws InterruptedException, BundleException, IOException {
		myService = new MyServiceImpl();

		normalReg = getContext().registerService(MyService.class, myService, null);

		asyncTracker = new ServiceTracker<Async, Async>(getContext(), Async.class, null);
		asyncTracker.open();
		async = asyncTracker.waitForService(5000);

		assertNotNull("No Async service available within 5 seconds", async);

		logReaderTracker = new ServiceTracker<LogReaderService, LogReaderService>(getContext(), LogReaderService.class, null);
		logReaderTracker.open();
		logReader = logReaderTracker.waitForService(5000);
		assertNotNull("No LogReaderService available within 5 seconds", logReader);
		logReader.addLogListener(asyncErrors  = new AsyncErrorListener());

		tb1Bundle = install("tb1.jar");
		tb1Bundle.start();
		tb2Bundle = install("tb2.jar");
		tb2Bundle.start();
	}
	
	protected void tearDown() {
		normalReg.unregister();

		asyncTracker.close();

		logReader.removeLogListener(asyncErrors);
		logReaderTracker.close();

		try {
			tb1Bundle.stop();
		} catch (BundleException e) {
			// did out best
		}
		try {
			tb2Bundle.stop();
		} catch (BundleException e) {
			// did out best
		}
		// Note that we don't uninstall tb bundles each time
	}

	public void testmediateReferenceWithSecurityCheck_tb1() throws Exception {
		assertMediateSuccess();
	}

	public void testmediateReferenceWithSecurityCheck_tb2() throws Exception {
		assertMediateSuccess();
	}

	public void testmediateDirectWithSecurityCheck_tb1() throws Exception {
		assertMediateSuccess();
	}

	public void testmediateDirectWithSecurityCheck_tb2() throws Exception {
		assertMediateSuccess();
	}

	public void testcallWithSecurityCheck_tb1() throws Exception {
		assertCallFailure();
	}

	public void testcallWithoutSecurityCheck_tb1() throws Exception {
		assertCallSuccess();
	}

	public void testcallWithSecurityCheck_tb2() throws Exception {
		assertCallSuccess();
	}

	public void testcallWithoutSecurityCheck_tb2() throws Exception {
		assertCallSuccess();
	}

	public void testexecuteWithSecurityCheck_tb1() throws Exception {
		assertExecuteFailure();
	}

	public void testexecuteWithoutSecurityCheck_tb1() throws Exception {
		assertExecuteSuccess();
	}

	public void testexecuteWithSecurityCheck_tb2() throws Exception {
		assertExecuteSuccess();
	}

	public void testexecuteWithoutSecurityCheck_tb2() throws Exception {
		assertExecuteSuccess();
	}

	private void assertMediateSuccess() throws Exception {
		getCallable().call();
	}

	private void assertCallFailure() throws Exception {
		Callable<Promise<Integer>> callable = asPromiseInteger(getCallable());
		Promise<Integer> promise = callable.call();
		Throwable t = AsyncTestUtils.awaitFailure(promise);
		assertTrue("Wrong failure exception: " + t, t instanceof SecurityException);
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myService.lastMethodCalled());
	}

	private void assertCallSuccess() throws Exception {
		Callable<Promise<Integer>> callable = asPromiseInteger(getCallable());
		Promise<Integer> promise = callable.call();
		Integer result = AsyncTestUtils.awaitResolve(promise);
		assertEquals("Wrong result.", Integer.valueOf(2), result);
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myService.lastMethodCalled());
	}

	private void assertExecuteFailure() throws Exception {
		Callable<Void> callable = asVoid(getCallable());
		callable.call();
		Promise<LogEntry> securityError = asyncErrors.getSecurityError();
		Throwable t = AsyncTestUtils.awaitResolve(securityError).getException();
		assertTrue("Wrong failure exception: " + t, t instanceof SecurityException);
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myService.lastMethodCalled());
	}

	private void assertExecuteSuccess() throws Exception {
		Callable<Void> callable = asVoid(getCallable());
		callable.call();
		Integer result = myService.awaitSuccess();
		assertEquals("Wrong result.", Integer.valueOf(2), result);
		assertEquals("Wrong method called.", MyService.METHOD_countSlowly, myService.lastMethodCalled());
	}

	@SuppressWarnings("unchecked")
	private Callable<Promise<Integer>> asPromiseInteger(Callable<?> callable) {
		return (Callable<Promise<Integer>>) callable;
	}

	@SuppressWarnings("unchecked")
	private Callable<Void> asVoid(Callable<?> callable) {
		return (Callable<Void>) callable;
	}

	@SuppressWarnings("rawtypes")
	private Callable getCallable() throws InvalidSyntaxException {
		String testName = getName();
		testName = testName.substring("test".length());
		Collection<ServiceReference<Callable>> callable = getContext().getServiceReferences(Callable.class, "(" + MyService.TEST_KEY + "=" + testName + ")");
		assertFalse("No testCallable found: " + testName, callable.isEmpty());
		return getContext().getService(callable.iterator().next());
	}

}
