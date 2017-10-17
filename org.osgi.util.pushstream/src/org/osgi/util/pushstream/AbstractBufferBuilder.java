/*
 * Copyright (c) OSGi Alliance (2015, 2017). All Rights Reserved.
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

abstract class AbstractBufferBuilder<R, T, U extends BlockingQueue<PushEvent< ? extends T>>>
		implements BufferBuilder<R,T,U> {

	protected Executor					worker;
	protected ScheduledExecutorService	timer;
	protected int						concurrency;
	protected PushbackPolicy<T,U>		backPressure;
	protected QueuePolicy<T,U>			bufferingPolicy;
	protected U							buffer;

	@Override
	public BufferBuilder<R,T,U> withBuffer(U queue) {
		this.buffer = queue;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withQueuePolicy(
			QueuePolicy<T,U> queuePolicy) {
		this.bufferingPolicy = queuePolicy;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withQueuePolicy(
			QueuePolicyOption queuePolicyOption) {
		this.bufferingPolicy = queuePolicyOption.getPolicy();
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withPushbackPolicy(
			PushbackPolicy<T,U> pushbackPolicy) {
		this.backPressure = pushbackPolicy;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withPushbackPolicy(
			PushbackPolicyOption pushbackPolicyOption, long time) {
		this.backPressure = pushbackPolicyOption.getPolicy(time);
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withParallelism(int parallelism) {
		this.concurrency = parallelism;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withExecutor(Executor executor) {
		this.worker = executor;
		return this;
	}

	@Override
	public BufferBuilder<R,T,U> withScheduler(
			ScheduledExecutorService scheduler) {
		this.timer = scheduler;
		return this;
	}
}
