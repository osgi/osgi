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
package org.osgi.service.blueprint.convert;

/**
 * Implemented by type converters that extend the type conversion
 * capabilties of a module context container.
 */
public interface Converter {
	
	/**
	 * The type that this converter converts String values into.
	 * @return Class object for the class that this converter converts to
	 */
	Class getTargetClass();

	/**
	 * Convert an object to an instance of the target class.
	 * @param source the object to be converted
	 * @return an instance of the class returned by getTargetClass
	 * @throws Exception if the conversion cannot succeed. This exception is
	 * checked because callers should expect that not all source objects
	 * can be successfully converted.
	 */
	Object convert(Object source) throws Exception;
}
