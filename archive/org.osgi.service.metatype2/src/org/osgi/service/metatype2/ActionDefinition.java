/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.service.metatype2;

/**
 * An interface to describe an action.
 *
 * <p>An <tt>ActionDefinition</tt> object defines a description of an action.
 * An action is very similar to an attribute, it only adds the definition of the
 * input arguments if any.
 *
 * @version $Revision$
 */
public interface ActionDefinition extends ExtendedAttributeDefinition {
	
	/**
	 * The <code>VOID(13)</code> type. 
	 * 
	 * The action may have VOID type meaning that it does not 
	 * have output results.
	 */
	public static final int VOID = 13;
	
  /**
	 * Returns the definitions of the input arguments of this action
	 * 
	 * @return The definitions of the input arguments or null if there is no input arguments.
	 */
    ExtendedAttributeDefinition[] getInputArgumentDefinitions();
}


