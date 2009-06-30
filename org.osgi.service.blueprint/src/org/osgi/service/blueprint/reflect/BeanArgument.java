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
package org.osgi.service.blueprint.reflect;

/**
 * Metadata used in a Bean Manager to inject arguments in a method or
 * constructor. This Metadata class describes the <code>argument</element>
 */
public interface BeanArgument {

	/**
	 * The Metadata for the value to inject into the argument.
	 *
	 * This is the <code>value</code> attribute.
	 *
	 * @return the Metadata for the value
	 */
	Metadata getValue();

	/**
	 * The type to convert the value into when invoking the constructor or
	 * factory method. If no explicit type was specified on the
	 * definition then this method returns <code>null</code>.
	 *
	 * This is the <code>type</code> attribute.
	 *
	 * @return the explicitly specified type to convert the value into, or <code>null</code>
	 *         if no type was specified in the definition.
	 */
	String getValueType();

	/**
	 * The (zero-based) index into the parameter list of the method or
	 * constructor to be invoked for this argument. This is determined either
	 * by explicitly specifying the <code>index</code> attribute in the component
	 * declaration.  If not explicitly set, this will return -1.
	 *
	 * This is the <code>index</code> attribute.
	 *
	 * @return the zero-based parameter index, or -1 if the argument position was not set.
	 */
	int getIndex();
}
