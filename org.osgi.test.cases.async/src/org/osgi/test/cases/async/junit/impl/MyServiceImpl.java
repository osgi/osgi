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

import org.osgi.test.cases.async.junit.AsyncTestUtils;
import org.osgi.test.cases.async.services.AnotherService;
import org.osgi.test.cases.async.services.MyService;
import org.osgi.util.promise.Deferred;

public class MyServiceImpl implements MyService, AnotherService {
	private static final long timeToSleep = 500;
	protected final Deferred<String> lastMethodCalled = new Deferred<String>();

	public void doSlowStuff(int times) throws Exception  {
		doCountSlowly(times);
		lastMethodCalled.resolve(METHOD_doSlowStuff);
	}

	public int countSlowly(int times) throws Exception {
		try {
			return doCountSlowly(times);
		} finally {
			lastMethodCalled.resolve(METHOD_countSlowly);
		}
	}

	private int doCountSlowly(int times) throws Exception {
		for(int i = 0; i < times; i++) {
			Thread.sleep(timeToSleep);
		}
		return times;
	}

	public int failSlowly(int times) throws Exception {
		doCountSlowly(times);
		lastMethodCalled.resolve(METHOD_failSlowly);
		throw new MyServiceException();
	}

	public int nonDelegateCountSlowly(int times) throws Exception {
		try {
			return doCountSlowly(times);
		} finally {
			lastMethodCalled.resolve(METHOD_nonDelegateCountSlowly);
		}
	}

	public int nonDelegateFailSlowly(int times) throws Exception {
		doCountSlowly(times);
		lastMethodCalled.resolve(METHOD_nonDelegateFailSlowly);
		throw new MyServiceException();
	}

	public int delegateFail() throws Exception {
		lastMethodCalled.resolve(METHOD_delegateFail);
		// Do not fail here
		return 0;
	}

	public String lastMethodCalled() throws InterruptedException, InvocationTargetException {
		return AsyncTestUtils.awaitResolve(lastMethodCalled.getPromise());
	}

	public void otherSlowStuff(int times) throws Exception {
		doSlowStuff(times);
		lastMethodCalled.resolve(METHOD_otherSlowStuff);
	}

	public static final class FinalMyServiceImpl extends MyServiceImpl {
		// To test mediated types for final classes
	}

	public static class FinalMethodMyServiceImpl extends MyServiceImpl {
		// To test mediated type classes with final methods
		public final int someFinalMethod(int times) {
			return 0;
		}
	}

	public static class ExtendedFinalMethodMyServiceImpl extends FinalMethodMyServiceImpl {
		// To test mediated type classes with final methods in hierarchy
	}

	public static class NoZeroArgConstructorMyServiceImpl extends MyServiceImpl {
		// To test mediated type classes with no zero arg constructors
		public NoZeroArgConstructorMyServiceImpl(int someArg) {
			super();
		}
	}
}
