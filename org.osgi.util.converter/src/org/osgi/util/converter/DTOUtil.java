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

package org.osgi.util.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author $Id$
 */
class DTOUtil {
	private static final Method[] OBJECT_CLASS_METHODS = Object.class
			.getMethods();

	private DTOUtil() {
		// Do not instantiate. This is a utility class.
	}

	static boolean isDTOType(Class< ? > cls, boolean ignorePublicNoArgsCtor) {
		if (!ignorePublicNoArgsCtor) {
			if (Arrays.stream(cls.getConstructors())
					.noneMatch(ctor -> ctor.getParameterCount() == 0)) {
				// No public zero-arg constructor, not a DTO
				return false;
			}
		}

		for (Method m : cls.getMethods()) {
			if (Arrays.stream(OBJECT_CLASS_METHODS)
					.noneMatch(om -> om.getName().equals(m.getName())
							&& Arrays.equals(om.getParameterTypes(),
									m.getParameterTypes()))) {
				// Not a method defined by Object.class (or override of such
				// method)
				return false;
			}
		}

		boolean foundField = false;
		for (Field f : cls.getFields()) {
			int modifiers = f.getModifiers();
			if (Modifier.isStatic(modifiers)) {
				// ignore static fields
				continue;
			}

			if (!Modifier.isPublic(modifiers)) {
				return false;
			}
			foundField = true;
		}
		return foundField;
	}
}
