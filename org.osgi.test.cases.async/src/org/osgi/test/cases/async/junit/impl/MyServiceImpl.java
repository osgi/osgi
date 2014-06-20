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
package org.osgi.test.cases.async.junit.impl;

import org.osgi.test.cases.async.services.MyService;
import org.osgi.test.cases.async.services.types.Argument;

public class MyServiceImpl implements MyService {
	volatile String lastMethodCalled;
	private final long timeToSleep = 500;

	public void doSlowStuff(int times) throws Exception  {
		countSlowly(times);
		lastMethodCalled = METHOD_doSlowStuff;
	}

	public int countSlowly(int times) throws Exception {
		for(int i = 0; i < times; i++) {
			Thread.sleep(timeToSleep);
		}
		lastMethodCalled = METHOD_countSlowly;
		return times;
	}

	public int failSlowly(int times) throws Exception {
		countSlowly(times);
		lastMethodCalled = METHOD_failSlowly;
		throw new MyServiceException();
	}

	public int slowNonDelegateStuff(int times) throws Exception {
		int result =  countSlowly(times);
		lastMethodCalled = METHOD_slowNonAsyncStuff;
		return result;
	}

	public int delegateFail() throws Exception {
		lastMethodCalled = METHOD_delegateFail;
		throw new MyServiceException();
	}

	public void take(Argument arg) {
		lastMethodCalled = METHOD_take;
	}

	public String lastMethodCalled() {
		return lastMethodCalled;
	}
}