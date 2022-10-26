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
package org.osgi.util.pushstream;

import java.util.concurrent.BlockingQueue;

/**
 * Provides a configurable {@link PushbackPolicy} implementation that returns
 * zero back pressure until the buffer fills beyond the supplied threshold. Once
 * the threshold is reached back pressure is returned based on the supplied
 * parameters.
 * <p>
 * The starting level of the back pressure once the threshold is exceeded is
 * defined using the <code>initial</code> parameter. Additional back pressure is
 * applied for each queued item over the threshold. The amount of this
 * additional back pressure is defined by the <code>increment</code> parameter.
 * <p>
 * The following common use cases are supported:
 * <ul>
 * <li>The increment size can be zero, returning the initial value as a fixed
 * back pressure after the threshold is exceeded.</li>
 * <li>The initial value can be zero, returning a linearly increasing back
 * pressure from zero once the threshold is exceeded</li>
 * </ul>
 * 
 * @param <T> The type of objects in the {@link PushEvent}
 * @param <U> The type of the Queue used in the user specified buffer
 * @since 1.1
 */
public final class ThresholdPushbackPolicy<T, U extends BlockingQueue<PushEvent< ? extends T>>>
		implements PushbackPolicy<T,U> {

	private final int	threshold;
	private final long	initial;
	private final long	increment;

	/**
	 * A simple configuration with an initial back pressure of one and and
	 * increase increment size of one.
	 * 
	 * @see #createThresholdPushbackPolicy(int threshold, long initial, long
	 *      increment)
	 * @param threshold the queue size limit after which back pressure will be
	 *            applied
	 * @throws IllegalArgumentException if any of the given values is lower then
	 *             zero
	 * @return a new {@link ThresholdPushbackPolicy}
	 */
	public static <T, U extends BlockingQueue<PushEvent< ? extends T>>> ThresholdPushbackPolicy<T,U> createSimpleThresholdPushbackPolicy(
			int threshold) {
		return new ThresholdPushbackPolicy<>(threshold, 1L, 1L);
	}

	/**
	 * A simple configuration, where the increment size is used as the initial
	 * back pressure.
	 * 
	 * @see #createThresholdPushbackPolicy(int threshold, long initial, long
	 *      increment)
	 * @param increment the increments in which the pressure increases
	 * @param threshold the queue size limit after which back pressure will be
	 *            applied
	 * @throws IllegalArgumentException if any of the given values is lower then
	 *             zero
	 * @return a new {@link ThresholdPushbackPolicy}
	 */
	public static <T, U extends BlockingQueue<PushEvent< ? extends T>>> ThresholdPushbackPolicy<T,U> createIncrementalThresholdPushbackPolicy(
			int threshold, long increment) {
		return new ThresholdPushbackPolicy<>(threshold, increment, increment);
	}

	/**
	 * Provides a {@link ThresholdPushbackPolicy} with an individual
	 * configuration for all possible parameters.
	 * 
	 * @param increment the increments in which the pressure increases.
	 * @param initial the initial back pressure which defines the floor after
	 *            the threshold is exceeded
	 * @param threshold the queue size limit after which back pressure will be
	 *            applied
	 * @throws IllegalArgumentException if any of the given values is lower then
	 *             zero
	 * @return a new {@link ThresholdPushbackPolicy}
	 */
	public static <T, U extends BlockingQueue<PushEvent< ? extends T>>> ThresholdPushbackPolicy<T,U> createThresholdPushbackPolicy(
			int threshold, long initial, long increment) {
		return new ThresholdPushbackPolicy<>(threshold, initial, increment);
	}
	
	/**
	 * @param increment the increments in which the pressure increases.
	 * @param initial the initial back pressure which defines the floor after
	 *            the threshold is exceeded
	 * @param threshold the queue size limit after which back pressure will be
	 *            applied
	 * @throws IllegalArgumentException if any of the given values is lower then
	 *             zero
	 */
	private ThresholdPushbackPolicy(int threshold, long initial,
			long increment) {
		if (threshold < 0 || initial < 0L || increment < 0L) {
			throw new IllegalArgumentException(String.format(
					"threshold, initial and increment must each be zero or higher. Current values are threshold [%s], initial [%s], increment [%s]",
					Integer.valueOf(threshold), Long.valueOf(initial),
					Long.valueOf(increment)));
		}
		this.threshold = threshold;
		this.initial = initial;
		this.increment = increment;
	}

	@Override
	public long pushback(U queue) throws Exception {
		int factor = queue.size() - threshold - 1;
		if (factor < 0) {
			return 0L;
		}
		return initial + (factor * increment);
	}

}
