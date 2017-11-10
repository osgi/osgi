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

package org.osgi.service.cdi.dto.model;

import java.util.Arrays;
import org.osgi.dto.DTO;

/**
 * Abstract base to represent any type of dependency.
 *
 * @NotThreadSafe
 * @author $Id$
 */
public abstract class DependencyModelDTO extends DTO {
	/**
	 * Defines the possible values for {@link #maximumCardinality}
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
		 * @return The enum representation of the upper cardinality boundary descrubed
		 *         by <code>value</code>
		 */
		public static MaximumCardinality fromInt(int value) {
			return Arrays.stream(values())
					.filter(it -> it.value == value)
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Illegal maximum cardinality value: " + value));
		}
	}

	/**
	 * The maximum cardinality of the reference.
	 */
	public MaximumCardinality	maximumCardinality;

	/**
	 * The minimum cardinality of the reference.
	 * <p>
	 * Contains the minimum cardinality statically resolved from the CDI bundle
	 * metadata. The minimum cardinality can be replaced by configuration at
	 * runtime.
	 * <p>
	 * <ul>
	 * <li>If {@link #maximumCardinality} is {@link MaximumCardinality#ONE ONE} the
	 * value must be either 0 or 1.</li>
	 * <li>If {@link #maximumCardinality} is {@link MaximumCardinality#MANY MANY}
	 * the value must be from 0 to {@link Integer#MAX_VALUE}.
	 * </ul>
	 */
	public int					minimumCardinality;

	/**
	 * Indicates if the reference is greedy or reluctant in nature.
	 */
	public boolean				greedy;

	/**
	 * Indicates if the reference is dynamic or static in nature.
	 */
	public boolean				dynamic;
}
