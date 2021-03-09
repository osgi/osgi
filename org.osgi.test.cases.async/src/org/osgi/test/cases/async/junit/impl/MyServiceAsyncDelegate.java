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
package org.osgi.test.cases.async.junit.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.osgi.service.async.delegate.AsyncDelegate;
import org.osgi.test.cases.async.junit.AsyncTestUtils;
import org.osgi.test.cases.async.services.AnotherService;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public class MyServiceAsyncDelegate extends MyServiceImpl implements AsyncDelegate {
	private enum DelegateType {
		ASYNC,
		FAIL,
		NON_ASYNC;
	}
	private static final Map<String, MyServiceAsyncDelegate.DelegateType> delegateTypes = new HashMap<String, MyServiceAsyncDelegate.DelegateType>();
	static {
		delegateTypes.put(MyService.METHOD_countSlowly, DelegateType.ASYNC);
		delegateTypes.put(MyService.METHOD_doSlowStuff, DelegateType.ASYNC);
		delegateTypes.put(MyService.METHOD_failSlowly, DelegateType.ASYNC);
		delegateTypes.put(AnotherService.METHOD_otherSlowStuff, DelegateType.ASYNC);
		delegateTypes.put(MyService.METHOD_delegateFail, DelegateType.FAIL);
		delegateTypes.put(MyService.METHOD_nonDelegateCountSlowly, DelegateType.NON_ASYNC);
		delegateTypes.put(MyService.METHOD_nonDelegateFailSlowly, DelegateType.NON_ASYNC);
	}

	private final ExecutorService asyncExecutor;
	private final Deferred<String> delegateMethodCalled = new Deferred<String>();

	public MyServiceAsyncDelegate(ExecutorService asyncExecutor) {
		this.asyncExecutor = asyncExecutor;
	}
	public Promise<?> async(final Method m, final Object[] args) throws Exception {
		delegateMethodCalled.resolve("async");
		return doAsync(m, args);
	}
	private Promise<?> doAsync(final Method m, final Object[] args) throws Exception {
		MyServiceAsyncDelegate.DelegateType delegateType = delegateTypes.get(m.getName());
		if (delegateType == null) {
			throw new NoSuchMethodError(m.getName());
		}
		switch (delegateType) {
			case ASYNC: {
				final Deferred<Object> deferred = new Deferred<Object>();
				asyncExecutor.execute(new Runnable() {
					public void run() {
						try {
							Object result = m.invoke(MyServiceAsyncDelegate.this, args);
							// We 2x the results so we can detect that our async was really called.
							if (result instanceof Integer) {
								result = Integer.valueOf(2 * ((Integer) result).intValue());
							}
							deferred.resolve(result);
						} catch (IllegalAccessException e) {
							deferred.fail(e);
						} catch (InvocationTargetException e) {
							deferred.fail(e.getTargetException());
						}
					}
				});
				return deferred.getPromise();
			}
			case FAIL : {
				lastMethodCalled.resolve(null);
				throw new MyServiceException();
			}
			case NON_ASYNC : {
				return null;
			}
			default: {
				throw new IllegalArgumentException("Invalid delegateType: " + delegateType);
			}
		}
	}
	public boolean execute(Method m, Object[] args) throws Exception {
		delegateMethodCalled.resolve("execute");
		return doAsync(m, args) != null;
	}

	public String getDelegateMethodCalled() throws InterruptedException, InvocationTargetException {
		return AsyncTestUtils.awaitResolve(delegateMethodCalled.getPromise());
	}
}
