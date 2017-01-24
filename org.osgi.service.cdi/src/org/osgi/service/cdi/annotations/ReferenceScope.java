/*
 * Copyright (c) OSGi Alliance (2013, 2014, 2017). All Rights Reserved.
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

/**
 * Reference scope for the {@link Reference} annotation.
 */
public enum ReferenceScope {
	/**
	 * A single service object is used for all references to the service in this
	 * bundle.
	 */
	BUNDLE("bundle"),

	/**
	 * Bound services must have prototype service scope. Each instance of the
	 * bean with this reference can receive a unique instance of the service.
	 */
	PROTOTYPE("prototype"),

	/**
	 * Bound services must have singleton service scope. Each instance of the
	 * bean with this reference will receive the same instance of the service.
	 */
	SINGLETON("singleton");

	private final String	value;

	ReferenceScope(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
