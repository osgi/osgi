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
 * Provides access to a concrete type and its optional generic type argument.
 * 
 * A Collapsed Type converts a type declaration to a generalized form that is
 * directly usable for conversion.It provides access to the concrete Class of
 * the type as well of the optional type arguments.
 * 
 * In Java 1.4, this class only works on non-generic types. In those cases, a
 * Collapsed Type provides to the class and has no type arguments. Blueprint
 * extender implementations can subclass this class and provide access to the
 * generics type graph if used in a declaration. Such a subclass must
 * <em>collapse<em> the different <code>Type</code> instances into the 
 * collapsed form. That is, a form where the raw Class is available with its optional type arguments.
 *
 *@Immutable
 */

public class CollapsedType {
	final static CollapsedType ALL = new CollapsedType(Object.class);

	private final Class clazz;

	/**
	 * Create a Collapsed Type for a normal Java class without any generics
	 * information.
	 * 
	 * @param clazz
	 *            The class that is the collapsed type.
	 */
	public CollapsedType(Class clazz) {
		this.clazz = clazz;
	}

	/**
	 * Access to the raw class.
	 * 
	 * The raw class represents the concrete class that is associated with a
	 * type declaration. This class could have been deduced from the generics
	 * type graph of the declaration. For example, in the following example:
	 * 
	 * <pre>
	 * Map&lt;String, Object&gt; map;
	 * </pre>
	 * 
	 * The raw class is the Map class.
	 * 
	 * @return the collapsed raw class that represents this type.
	 */
	public Class getRawClass() {
		return clazz;
	}

	/**
	 * Access to a type argument.
	 * 
	 * The type argument refers to a argument in a generic type declaration
	 * given by index <code>i</code>. This method returns a Collapsed Type
	 * that has Object as class when no generic type information is available.
	 * Any object is assignable to Object and therefore no conversion is then
	 * necessary, this is compatible with older Javas.
	 * 
	 * This method should be overridden by a subclass that provides access to
	 * the generic information.
	 * 
	 * For example, in the following example:
	 * 
	 * <pre>
	 * Map&lt;String, Object&gt; map;
	 * </pre>
	 * 
	 * The type argument 0 is <code>String</code>, and type argument 1 is
	 * <code>Object</code>.
	 * 
	 * @param i
	 *            The index of the type argument
	 * @return A Collapsed Type that represents a type argument. If
	 */
	public CollapsedType getActualTypeArgument(int i) {
		return ALL;
	}
}
