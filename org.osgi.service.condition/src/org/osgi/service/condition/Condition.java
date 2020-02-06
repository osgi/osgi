/*
 * Copyright (c) OSGi Alliance (2020). All Rights Reserved.
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

package org.osgi.service.condition;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * A condition service.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface Condition {
	/**
	 * Service property identifying a condition's unique identifier. The type of
	 * this service property is {@code String+}
	 */
	public static final String		CONDITION_ID									= "osgi.condition.id";

	/**
	 * The unique identifier for the TRUE Condition. The TRUE condition is
	 * registered by the framework and therefore can always be relied upon.
	 * 
	 * @see Condition#CONDITION_ID
	 */
	public static final String		CONDITION_ID_TRUE								= "true";

	/**
	 * A singleton condition instance that can be used to register condition
	 * services.
	 */
	public static final Condition	INSTANCE										= new Condition() {/* Nothing to do */};

}
