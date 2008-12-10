/*
 * Copyright (c) OSGi Alliance (2000, 2008). All Rights Reserved.
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
 * Metadata describing a parameter of a method or constructor and the
 * value that is to be passed during injection.
 * 
 */
public interface ParameterSpecification {
	
	/**
	 * The value to inject into the parameter.
	 * 
	 * @return the parameter value
	 */
	Value getValue();
	
	/**
	 * The type to convert the value into when invoking the constructor or
	 * factory method. If no explicit type was specified on the component 
	 * definition then this method returns null.
	 * 
	 * @return the explicitly specified type to convert the value into, or 
	 * null if no type was specified in the component definition.
	 */
	String getTypeName();
	
	/**
	 * The (zero-based) index into the parameter list of the method or 
	 * constructor to be invoked for this parameter. This is determined
	 * either by explicitly specifying the index attribute in the component
	 * declaration, or by declaration order of constructor-arg elements if the
	 * index was not explicitly set.
	 * 
	 * @return the zero-based parameter index
	 */
	int getIndex();
}
