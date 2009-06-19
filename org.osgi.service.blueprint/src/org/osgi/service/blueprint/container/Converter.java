/*
 * Copyright (c) OSGi Alliance (2008, 2009). All Rights Reserved.
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
package org.osgi.service.blueprint.container;

/**
 * Provides access to the type conversions (both predefined and user registered)
 * that are defined for the Blueprint Container.
 */
public interface Converter {

	/**
	 * Check if the converter is able to convert the given value to the
	 * specified type.
	 * 
	 * @param s
	 *            The source object to convert from
	 * @param T
	 *            The target type
	 * 
	 * @return <code>true</code> if the conversion is possible,
	 *         <code>false</code> otherwise.
	 */
	boolean canConvert(Object s, CollapsedType T);

	/**
	 * Convert an object to an instance of the given class, using the built-in
	 * and user-registered type converters as necessary.
	 * 
	 * @param s
	 *            The source object to convert from
	 * @param T
	 *            The target type
	 * @return an instance of the class 'toType'
	 * @throws Exception
	 *             if the conversion cannot succeed. This exception is checked
	 *             because callers should expect that not all source objects can
	 *             be successfully converted.
	 */
	Object convert(Object s, CollapsedType T) throws Exception;

}