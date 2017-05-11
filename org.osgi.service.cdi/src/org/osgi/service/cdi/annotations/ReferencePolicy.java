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
 * Policy for the {@link Reference} annotation.
 *
 * @author $Id$
 */
public enum ReferencePolicy {
	/**
	 * The value indicating that no choice was made in which case the calculated
	 * value or default behavior should take effect.
	 * <p>
	 * The default behavior is {@link ReferencePolicy#STATIC STATIC}.
	 */
	DEFAULT("default"),

	/**
	 * The static policy is the most simple policy and is the default policy. A
	 * CDI component instance never sees any of the dynamics. CDI Containers are
	 * deactivated before any bound service for a reference having a static
	 * policy becomes unavailable. If a target service is available to replace
	 * the bound service which became unavailable, the CDI Container must be
	 * reactivated and bound to the replacement service.
	 */
	STATIC("static"),

	/**
	 * The dynamic policy is slightly more complex since the component
	 * implementation must properly handle changes in the set of bound services.
	 * With the dynamic policy, the CDI Extender can change the set of bound
	 * services without deactivating a CDI Container. If the component uses the
	 * event strategy to access services, then the component instance will be
	 * notified of changes in the set of bound services by calls to the observer
	 * methods.
	 */
	DYNAMIC("dynamic");

	private final String value;

	/**
	 * Get a {@link ReferencePolicy ReferencePolicy} instance by value rather than
	 * by name.
	 *
	 * @param input a non-null value
	 * @return the {@link ReferencePolicy ReferencePolicy} matching the value
	 * @throws NullPointerException if the input is null
	 * @throws IllegalArgumentException on invalid input
	 */
	public static ReferencePolicy get(String input) {
		Objects.requireNonNull(input, "input cannot be null");

		for (ReferencePolicy policy : values()) {
			if (input.equals(policy.toString()))
				return policy;
		}

		throw new IllegalArgumentException(input);
	}

	ReferencePolicy(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
