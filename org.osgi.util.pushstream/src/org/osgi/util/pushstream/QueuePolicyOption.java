/*
 * Copyright (c) OSGi Alliance (2015). All Rights Reserved.
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

/**
 * {@link QueuePolicyOption} provides a standard set of simple
 * {@link QueuePolicy} implementations.
 * 
 * @see QueuePolicy
 */
public enum QueuePolicyOption {
	/**
	 * Attempt to add the supplied event to the queue. If the queue is unable to
	 * immediately accept the value then discard the value at the head of the
	 * queue and try again. Repeat this process until the event is enqueued.
	 */
	DISCARD_OLDEST {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy() {
			return (queue, event) -> {
				while (!queue.offer(event)) {
					queue.poll();
				}
			};
		}
	},
	/**
	 * Attempt to add the supplied event to the queue, blocking until the
	 * enqueue is successful.
	 */
	BLOCK {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy() {
			return (queue, event) -> {
				try {
					queue.put(event);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}
	},
	/**
	 * Attempt to add the supplied event to the queue, throwing an exception if
	 * the queue is full.
	 */
	FAIL {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy() {
			return (queue, event) -> queue.add(event);
		}
	};

	/**
	 * @return a {@link QueuePolicy} implementation
	 */
	public abstract <T, U extends BlockingQueue<PushEvent<? extends T>>> QueuePolicy<T, U> getPolicy();

}
