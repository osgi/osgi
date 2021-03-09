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
package org.osgi.test.cases.remoteservices.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.osgi.test.cases.remoteservices.common.AsyncJava8Types;

public class AsyncJava8TypesImpl implements AsyncJava8Types {

	private final Executor		executor	= Executors
			.newSingleThreadExecutor();

	void waitFor(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public CompletableFuture<String> getCompletableFuture(final int millis) {
		return CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				waitFor(millis);
			}
		}, executor).thenApply(new Function<Void,String>() {
			@Override
			public String apply(Void x) {
				return AsyncJava8Types.RESULT;
			}
		});
	}

	@Override
	public CompletionStage<String> getCompletionStage(int millis) {
		return getCompletableFuture(millis);
	}
}
