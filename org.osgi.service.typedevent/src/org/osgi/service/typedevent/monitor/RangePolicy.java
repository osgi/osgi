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
package org.osgi.service.typedevent.monitor;

import static java.lang.Integer.MAX_VALUE;

import org.osgi.annotation.versioning.ProviderType;

/**
 * A range policy determining how much history should be stored. The minimum
 * defines a hard limit indicating the minimum number of historical events that
 * must be kept. The maximum defines an additional limit capping the maximum
 * number of events that should be kept by the implementation. Events between
 * the minimum and maximum storage limits may be discarded at any time by the
 * implementation.
 * 
 * @ThreadSafe
 * @author $Id$
 * @since 1.1
 */
@ProviderType
public final class RangePolicy {

	private static final RangePolicy	NONE		= new RangePolicy(0, 0);
	private static final RangePolicy	UNLIMITED	= new RangePolicy(0,
			MAX_VALUE);

	private final int					min;
	private final int					max;

	private RangePolicy(int min, int max) {
		if (min < 0 || max < 0 || min > max) {
			throw new IllegalArgumentException(String.format(
					"The minimum %d and maximum %d must both be greater than zero, and the minimum less than or equal to the maximum",
					Integer.valueOf(min), Integer.valueOf(max)));
		}
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Get the minimum storage requirement
	 * <p>
	 * This defines the minimum number of events that must be stored by the
	 * implementation. If at least <code>min</code> events have been sent to a
	 * topic using this policy then there must be at least <code>min</code>
	 * events returned by the history.
	 * 
	 * @return the minimum number of events that must be retained
	 */
	public int getMinimum() {
		return min;
	}

	/**
	 * Get the maximum storage requirement
	 * <p>
	 * This defines the maximum number of events that should be stored by the
	 * implementation. The implementation must return at most <code>max</code>
	 * events for a topic using this policy, and may return fewer subject to the
	 * minimum constraint defined by {@link #getMinimum()}.
	 * 
	 * @return the maximum number of events that should be retained
	 */
	public int getMaximum() {
		return max;
	}

	/**
	 * Create a range policy for the defined range
	 * 
	 * @param min the minimum number of events that must be kept in history
	 * @param max the maximum number of events that may be kept in history
	 * @return A configured {@link RangePolicy}
	 */
	public static RangePolicy range(int min, int max) {
		return new RangePolicy(min, max);
	}

	/**
	 * Create a range policy for an exact range. Equivalent to
	 * <code>RangePolicy.range(count, count)</code>
	 * 
	 * @param count the number of events that must be kept in history
	 * @return A configured {@link RangePolicy}
	 */
	public static RangePolicy exact(int count) {
		return new RangePolicy(count, count);
	}

	/**
	 * Create a range policy for a minimum history requirement. Equivalent to
	 * <code>RangePolicy.range(min, Integer.MAX_VALUE)</code>
	 * 
	 * @param min the minimum number of events that must be kept in history
	 * @return A configured {@link RangePolicy}
	 */
	public static RangePolicy atLeast(int min) {
		return new RangePolicy(min, MAX_VALUE);
	}

	/**
	 * Create a range policy for a maximum history requirement. Equivalent to
	 * <code>RangePolicy.range(0, max)</code>
	 * 
	 * @param max the maximum number of events that may be kept in history
	 * @return A configured {@link RangePolicy}
	 */
	public static RangePolicy atMost(int max) {
		return new RangePolicy(0, max);
	}

	/**
	 * Get a range policy which stores no events. Equivalent to
	 * <code>RangePolicy.range(0, 0)</code>
	 * 
	 * @return A configured {@link RangePolicy}
	 */
	public static RangePolicy none() {
		return NONE;
	}

	/**
	 * Get a range policy which may store unlimited events. Equivalent to
	 * <code>RangePolicy.range(0, Integer.MAX_VALUE)</code>
	 * 
	 * @return A configured {@link RangePolicy}
	 */
	public static RangePolicy unlimited() {
		return UNLIMITED;
	}
}
