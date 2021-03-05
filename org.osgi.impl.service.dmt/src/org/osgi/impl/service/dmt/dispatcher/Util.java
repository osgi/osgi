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
package org.osgi.impl.service.dmt.dispatcher;

import java.util.Arrays;
import java.util.Collection;

/**
 * simple utility class with some static methods 
 * 
 * @author steffen
 *
 */
public class Util {

	/**
	 * Takes an Object and returns a Collection of Strings if:
	 * - Object is of type String[] or
	 * - Object is of type String
	 * @param property ... given object
	 * @return ... Collection of Strings or null, if types don't fit
	 */
	@SuppressWarnings("unchecked")
	public static Collection<String> toCollection(Object property) {
		if (property instanceof Collection<?>)
			return (Collection<String>) property;

		if (property instanceof String)
			return Arrays.asList((String) property);

		if (property instanceof String[])
			return Arrays.asList((String[]) property);

		return null;
	}
}
