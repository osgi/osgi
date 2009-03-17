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
 * Provides access to the type conversions (both predefined and user registered)
 * that are defined for the module context
 */
public interface ConversionService {
	
	/**
	 * Convert an object to an instance of the given class, using the built-in and 
	 * user-registered type converters as necessary.
	 * @param fromValue the object to be converted
	 * @param toType the type that the instance is to be converted to
	 * @return an instance of the class 'toType'
	 * @throws Exception if the conversion cannot succeed. This exception is
	 * checked because callers should expect that not all source objects
	 * can be successfully converted.
	 */
	Object convert(Object fromValue, Class toType) throws Exception;
	
}