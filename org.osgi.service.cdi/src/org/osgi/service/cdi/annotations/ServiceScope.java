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
 * Service scope for the {@link Reference} annotation.
 *
 * @author $Id$
 */
public enum ServiceScope {
	/**
	 * The value indicating that no choice was made in which case the calculated
	 * value or default behavior should take effect.
	 * <p>
	 * The default behavior is {@link ServiceScope#SINGLETON}.
	 */
	NOT_SPECIFIED("not_specified"),

	/**
	 * Indicates the component should be registered as an OSGi service with
	 * {@code osgi.scope=bundle}. It must be registered as a bundle scope service
	 * and an instance of the component must be created for each bundle using the
	 * service.
	 */
	BUNDLE("bundle"),

	/**
	 * Indicates the component should be registered as an OSGi service with
	 * {@code osgi.scope=prototype}. It must be registered as a prototype scope
	 * service and an instance of the component must be created for each distinct
	 * request for the service.
	 */
	PROTOTYPE("prototype"),

	/**
	 * Indicates the component must be registered as an OSGi service with
	 * {@code osgi.scope=singleton}. It must be registered as a bundle scope service
	 * but only a single instance of the component must be used for all bundles
	 * using the service.
	 */
	SINGLETON("singleton");

	private final String value;

	/**
	 * Get a {@link ServiceScope ServiceScope} instance by value rather than by
	 * name.
	 *
	 * @param input a non-null value
	 * @return the {@link ServiceScope ServiceScope} matching the value
	 * @throws NullPointerException if the input is null
	 * @throws IllegalArgumentException on invalid input
	 */
	public static ServiceScope get(String input) {
		Objects.requireNonNull(input, "input cannot be null");

		for (ServiceScope policy : values()) {
			if (input.equals(policy.toString()))
				return policy;
		}

		throw new IllegalArgumentException(input);
	}

	ServiceScope(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
