/*
 * Copyright (c) OSGi Alliance (2011, 2017). All Rights Reserved.
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

package org.osgi.service.cdi.annotations;

import java.util.Objects;

/**
 * Cardinality for the {@link Reference} annotation.
 *
 * <p>
 * Specifies if the reference is optional and if the component implementation
 * support a single bound service or multiple bound services.
 *
 * @author $Id$
 */
public enum ReferenceCardinality {
	/**
	 * The value indicating that no choice was made in which case the calculated
	 * value or default behavior should take effect.
	 * <p>
	 * The default behavior is {@link ReferenceCardinality#MANDATORY MANDATORY}.
	 */
	NOT_SPECIFIED("not_specified"),

	/**
	 * The reference is optional and unary. That is, the reference has a
	 * cardinality of {@code 0..1}.
	 */
	OPTIONAL("0..1"),

	/**
	 * The reference is mandatory and unary. That is, the reference has a
	 * cardinality of {@code 1..1}.
	 */
	MANDATORY("1..1"),

	/**
	 * The reference is optional and multiple. That is, the reference has a
	 * cardinality of {@code 0..n}.
	 */
	MULTIPLE("0..n"),

	/**
	 * The reference is mandatory and multiple. That is, the reference has a
	 * cardinality of {@code 1..n}.
	 */
	AT_LEAST_ONE("1..n");

	private final String	value;

	/**
	 * Get a {@link ReferenceCardinality ReferenceCardinality} instance by value
	 * rather than by name.
	 *
	 * @param input a non-null value
	 * @return the {@link ReferenceCardinality ReferenceCardinality} matching the
	 *         value
	 * @throws NullPointerException if the input is null
	 * @throws IllegalArgumentException on invalid input
	 */
	public static ReferenceCardinality get(String input) {
		Objects.requireNonNull(input, "input cannot be null");

		for (ReferenceCardinality cardinality : values()) {
			if (input.equals(cardinality.toString()))
				return cardinality;
		}

		throw new IllegalArgumentException(input);
	}

	ReferenceCardinality(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
