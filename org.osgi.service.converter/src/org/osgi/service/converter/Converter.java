/*
 * Copyright (c) OSGi Alliance (2008). All Rights Reserved.
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
package org.osgi.service.converter;

/**
 * A Converter service can convert an object to another <em>type</em>. For
 * example the command shell can use this service to coerce the source type (in
 * this case often a string) to a destination type, for example a parameter in a
 * method. However, converters are general converters, they must be able to
 * handle any source type though it is not required that they can actually
 * perform the conversion.
 * 
 * A Converter service must register with the {@link #OSGI_CONVERTER_TYPE}
 * service property. This property specifies the names of the class and
 * interfaces this Converter service can convert to one or more target types.
 * Multiple Converter services can provide conversion of the same
 * class/interface. An {@link AggregateConverter} must use these services in
 * service ranking order, the first Converter service that can convert the
 * source object to the target type will be chosen.
 * 
 * Due to different class space the target type can be a recognized class name
 * but still fail because the converter belongs to another class space.
 * Converter services must therefore be careful to check if they are in the same
 * class space as the target type.
 * 
 * 
 * @ConsumerInterface
 * @version $Id$
 */
public interface Converter {
	/**
	 * Names of classes/interfaces that this Converter service has conversions
	 * for. It is a service property that is a String+</code>. That is, a
	 * string, or an array/collection of strings.
	 */
	String	OSGI_CONVERTER_TYPE	= "osgi.converter.type";

	/**
	 * Return if this converter is able to convert the specified object to the
	 * specified target type.
	 * 
	 * @param <T> The raw type
	 * 
	 * @param sourceObject The source object <code>s</code> to convert.
	 * @param targetType The target type <code>T</code>.
	 * 
	 * @return <code>true</code> if the conversion is possible,
	 *         <code>false</code> otherwise.
	 */
	<T> boolean canConvert(Object sourceObject, ReifiedType<T> targetType);

	/**
	 * Convert an object to the desired type.
	 * 
	 * Return null if the conversion can not be done. Otherwise return an object
	 * that extends the desired type or implements it.
	 * 
	 * @param <T> The raw type of the desired result object
	 * @param targetType The type that the returned object can be assigned to
	 * @param sourceObject The object that must be converted
	 * @return An object that can be assigned to the desired type or
	 *         <code>null</code> if it could not be converted.
	 */
	<T> T convert(Object sourceObject, ReifiedType<T> targetType);
}
