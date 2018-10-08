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

package org.osgi.service.cdi;

import java.util.Arrays;

/**
 * Defines the possible values for maximum cardinality of dependencies.
 *
 * @author $Id$
 */
public enum MaximumCardinality {
	/**
	 * Defines a unary reference.
	 */
	ONE(1),
	/**
	 * Defines a plural reference.
	 */
	MANY(Integer.MAX_VALUE);

	private int value;

	private MaximumCardinality(int value) {
		this.value = value;
	}

	/**
	 * Convert this upper cardinality boundary to an integer
	 *
	 * @return The integer representation of this upper cardinality boundary
	 */
	public int toInt() {
		return value;
	}

	/**
	 * Resolve an integer to an upper cardinality boundary.
	 *
	 * @param value The integer representation of an upper cardinality boundary
	 * @return The enum representation of the upper cardinality boundary
	 *         described by {@code value}
	 */
	public static MaximumCardinality fromInt(int value) {
		return Arrays.stream(values())
				.filter(it -> it.value == value)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Illegal maximum cardinality value: " + value));
	}
}
