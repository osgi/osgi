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
package org.osgi.test.cases.async.secure.junit.impl;

import java.security.AccessController;
import org.osgi.test.cases.async.secure.junit.AsyncTestUtils;
import org.osgi.test.cases.async.secure.services.MyService;
import org.osgi.util.promise.Deferred;

public class MyServiceImpl implements MyService {
	private static final long timeToSleep = 500;
	protected final Deferred<String> lastMethodCalled = new Deferred<String>();
	protected final Deferred<Integer> success = new Deferred<Integer>();

	public MyServiceImpl() {
		// always check permissions. This is either going to be called while
		// registering the service or while mediating
		AccessController.checkPermission(new MyPermission("constructor"));
	}

	public int countSlowly(int times, boolean withSecuriyCheck) throws Exception {
		try {
			int result = doCountSlowly(times);
			if (withSecuriyCheck) {
				AccessController.checkPermission(new MyPermission("countSlowly"));
			}
			success.resolve(result);
			return result;
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

	public String lastMethodCalled() throws InterruptedException {
		return AsyncTestUtils.awaitResolve(lastMethodCalled.getPromise());
	}

	public Integer awaitSuccess() throws InterruptedException {
		return AsyncTestUtils.awaitResolve(success.getPromise());
	}
}
