/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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

	public Future<String> getFuture(int millis) {
		FutureTask<String> task = new FutureTask<>(() -> waitFor(millis),
				AsyncTypes.RESULT);
		executor.execute(task);
		return task;
	}

	private void waitFor(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public CompletableFuture<String> getCompletableFuture(int millis) {
		return CompletableFuture.runAsync(() -> waitFor(millis), executor)
				.thenApply(x -> AsyncTypes.RESULT);
	}

	public CompletionStage<String> getCompletionStage(int millis) {
		return getCompletableFuture(millis);
	}

	public Promise<String> getPromise(int millis) {
		Deferred<String> d = new Deferred<>();

		executor.execute(() -> {
			waitFor(millis);
			d.resolve(AsyncTypes.RESULT);
		});

		return d.getPromise();
	}

}
