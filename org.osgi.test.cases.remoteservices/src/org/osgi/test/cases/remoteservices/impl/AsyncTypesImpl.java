/*
 * Copyright (c) OSGi Alliance (2017, 2018). All Rights Reserved.
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
package org.osgi.test.cases.remoteservices.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.osgi.test.cases.remoteservices.common.AsyncTypes;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

public class AsyncTypesImpl implements AsyncTypes {

	private final Executor		executor	= Executors
			.newSingleThreadExecutor();

	@Override
	public Future<String> getFuture(final int millis) {
		FutureTask<String> task = new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				waitFor(millis);
			}
		},
				AsyncTypes.RESULT);
		executor.execute(task);
		return task;
	}

	void waitFor(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Promise<String> getPromise(final int millis) {
		final Deferred<String> d = new Deferred<>();

		executor.execute(new Runnable() {
			@Override
			public void run() {
				waitFor(millis);
				d.resolve(AsyncTypes.RESULT);
			}
		});

		return d.getPromise();
	}

}
