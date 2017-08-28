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

package org.osgi.service.cdi.annotations;

import java.util.Objects;

/**
 * Service policy for the {@link Reference} annotation.
 *
 * @author $Id$
 */
public enum ServicePolicy {
	/**
	 * The value indicating that no choice was made in which case the calculated
	 * value or default behavior should take effect.
	 * <p>
	 * The default behavior is {@link ServicePolicy#NONE NONE}.
	 */
	DEFAULT("default"),

	/**
	 * The derived policy means that the component should be published as a service.
	 * The types under which the component will be published is derived by using the
	 * following algorithm:
	 * <ul>
	 * <li>If {@code @Component.service} is set - Use the types listed</li>
	 * <li>If {@code @Component.service} is not set - Use the directly implemented
	 * interfaces. If no directly implemented interfaces are found, use concrete
	 * type.</li>
	 * </ul>
	 */
	DERIVED("derived"),

	/**
	 * The {@link ServicePolicy#NONE NONE} policy means that the component is not to
	 * be published as a service. If {@code @Component.service} is set, it is
	 * ignored. This is the default policy.
	 */
	NONE("none");

	private final String value;

	/**
	 * Get a {@link ServicePolicy ServicePolicy} instance by value rather than by
	 * name.
	 *
	 * @param input a non-null value
	 * @return the {@link ServicePolicy ServicePolicy} matching the value
	 * @throws NullPointerException if the input is null
	 * @throws IllegalArgumentException on invalid input
	 */
	public static ServicePolicy get(String input) {
		Objects.requireNonNull(input, "input cannot be null");

		for (ServicePolicy policy : values()) {
			if (input.equals(policy.toString()))
				return policy;
		}

		throw new IllegalArgumentException(input);
	}

	ServicePolicy(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
