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

package org.osgi.util.pushstream;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.util.promise.PromiseExecutors;

class PushStreamExecutors extends PromiseExecutors {
	PushStreamExecutors(Executor executor, ScheduledExecutorService scheduler) {
		super(requireNonNull(executor), requireNonNull(scheduler));
	}

	void execute(Runnable operation) {
		executor().execute(operation);
	}

	ScheduledFuture< ? > schedule(Runnable operation, long delay,
			TimeUnit unit) {
		return scheduledExecutor().schedule(operation, delay, unit);
	}

	@Override
	protected Executor executor() {
		return super.executor();
	}

	@Override
	protected ScheduledExecutorService scheduledExecutor() {
		return super.scheduledExecutor();
	}
}
