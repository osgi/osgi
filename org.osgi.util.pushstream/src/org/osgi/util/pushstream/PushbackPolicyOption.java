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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link PushbackPolicyOption} provides a standard set of simple
 * {@link PushbackPolicy} implementations.
 * 
 * @see PushbackPolicy
 */
public enum PushbackPolicyOption {

	/**
	 * Returns a fixed amount of back pressure, independent of how full the
	 * buffer is
	 */
	FIXED {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> value;
		}
	},
	/**
	 * Returns zero back pressure until the buffer is full, then it returns a
	 * fixed value
	 */
	ON_FULL_FIXED {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> q.remainingCapacity() == 0 ? value : 0;
		}
	},
	/**
	 * Returns zero back pressure until the buffer is full, then it returns an
	 * exponentially increasing amount, starting with the supplied value and
	 * doubling it each time. Once the buffer is no longer full the back
	 * pressure returns to zero.
	 */
	ON_FULL_EXPONENTIAL {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			AtomicInteger backoffCount = new AtomicInteger(0);
			return q -> {
				if (q.remainingCapacity() == 0) {
					return value << backoffCount.getAndIncrement();
				}
				backoffCount.set(0);
				return 0;
			};

		}
	},
	/**
	 * Returns zero back pressure when the buffer is empty, then it returns a
	 * linearly increasing amount of back pressure based on how full the buffer
	 * is. The maximum value will be returned when the buffer is full.
	 */
	LINEAR {
		@Override
		public <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value) {
			return q -> {
				long remainingCapacity = q.remainingCapacity();
				long used = q.size();
				return (value * used) / (used + remainingCapacity);
			};
		}
	};

	/**
	 * Create a {@link PushbackPolicy} instance configured with a base back
	 * pressure time in nanoseconds
	 * 
	 * The actual backpressure returned will vary based on the selected
	 * implementation, the base value, and the state of the buffer.
	 * 
	 * @param value
	 * @return A {@link PushbackPolicy} to use
	 */
	public abstract <T, U extends BlockingQueue<PushEvent<? extends T>>> PushbackPolicy<T, U> getPolicy(long value);

}
