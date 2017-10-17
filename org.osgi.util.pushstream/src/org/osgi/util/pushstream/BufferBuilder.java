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

import org.osgi.annotation.versioning.ProviderType;

/**
 * Create a buffered section of a Push-based stream
 *
 * @param <R> The type of object being built
 * @param <T> The type of objects in the {@link PushEvent}
 * @param <U> The type of the Queue used in the user specified buffer
 */
@ProviderType
public interface BufferBuilder<R, T, U extends BlockingQueue<PushEvent<? extends T>>> {

	/**
	 * The BlockingQueue implementation to use as a buffer
	 * 
	 * @param queue
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withBuffer(U queue);

	/**
	 * Set the {@link QueuePolicy} of this Builder
	 * 
	 * @param queuePolicy
	 * @return this builder
	 */
	BufferBuilder<R,T,U> withQueuePolicy(QueuePolicy<T,U> queuePolicy);

	/**
	 * Set the {@link QueuePolicy} of this Builder
	 * 
	 * @param queuePolicyOption
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withQueuePolicy(QueuePolicyOption queuePolicyOption);

	/**
	 * Set the {@link PushbackPolicy} of this builder
	 * 
	 * @param pushbackPolicy
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withPushbackPolicy(PushbackPolicy<T, U> pushbackPolicy);

	/**
	 * Set the {@link PushbackPolicy} of this builder
	 * 
	 * @param pushbackPolicyOption
	 * @param time
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withPushbackPolicy(PushbackPolicyOption pushbackPolicyOption, long time);

	/**
	 * Set the maximum permitted number of concurrent event deliveries allowed
	 * from this buffer
	 * 
	 * @param parallelism
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withParallelism(int parallelism);

	/**
	 * Set the {@link Executor} that should be used to deliver events from this
	 * buffer
	 * 
	 * @param executor
	 * @return this builder
	 */
	BufferBuilder<R, T, U> withExecutor(Executor executor);

	/**
	 * Set the {@link ScheduledExecutorService} that should be used to trigger
	 * timed events after this buffer
	 * 
	 * @param scheduler
	 * @return this builder
	 */
	BufferBuilder<R,T,U> withScheduler(ScheduledExecutorService scheduler);
	
	/**
	 * @return the object being built
	 */
	R build();

}
