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
import javax.enterprise.context.Dependent;

/**
 * Configuration Policy for the {@link Component} annotation.
 *
 * <p>
 * Controls whether component configurations must be satisfied depending on the
 * presence of a corresponding Configuration object in the OSGi Configuration
 * Admin service. A corresponding configuration is a Configuration object where
 * the PID is the name of the component.
 *
 * @author $Id$
 * @since 1.1
 */
public enum ConfigurationPolicy {
	/**
	 * The value indicating that no choice was made in which case the calculated
	 * value or default behavior should take effect.
	 * <p>
	 * The default behavior is {@link ConfigurationPolicy#OPTIONAL OPTIONAL}.
	 */
	DEFAULT("default"),

	/**
	 * Use the corresponding Configuration object if present but allow the component
	 * to be satisfied even if the corresponding Configuration object is not
	 * present.
	 */
	OPTIONAL("optional"),

	/**
	 * There must be a corresponding Configuration object for the component
	 * configuration to become satisfied.
	 */
	REQUIRE("require"),

	/**
	 * The pids defined in {@link Configuration#value()} are treated as "factory
	 * pids" and component instances are created and managed by a managed service
	 * factory. The component is therefore referred to as a component factory bean.
	 * <p>
	 * A component factory bean having no configuration factory instances should not
	 * prevent the instantiation of other component beans. As such it behaves like
	 * {@link ConfigurationPolicy#OPTIONAL OPTIONAL} in addition to the multiplicity
	 * provided by the managed service factory.
	 * <p>
	 * A component factory bean must use the {@link Dependent} scope. Use of any
	 * other scope will be treated as a definition error.
	 * <p>
	 * Attempting to inject a component factory bean into a regular component bean
	 * in a non-dynamic way will result in a definition error.
	 */
	FACTORY("factory"),

	/**
	 * Always allow the component configuration to be satisfied and do not use the
	 * corresponding Configuration object even if it is present.
	 */
	IGNORE("ignore");

	private final String value;

	/**
	 * Get a {@link ConfigurationPolicy ConfigurationPolicy} instance by value
	 * rather than by name.
	 *
	 * @param input a non-null value
	 * @return the {@link ConfigurationPolicy ConfigurationPolicy} matching the
	 *         value
	 * @throws NullPointerException if the input is null
	 * @throws IllegalArgumentException on invalid input
	 */
	public static ConfigurationPolicy get(String input) {
		Objects.requireNonNull(input, "input cannot be null");

		for (ConfigurationPolicy policy : values()) {
			if (input.equals(policy.toString()))
				return policy;
		}

		throw new IllegalArgumentException(input);
	}

	ConfigurationPolicy(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
