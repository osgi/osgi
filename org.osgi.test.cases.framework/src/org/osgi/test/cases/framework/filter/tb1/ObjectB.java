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

package org.osgi.test.cases.framework.filter.tb1;

/**
 *
 * Test class used as a property object of a service. This class
 * does not implement comparable. It implements a constructor
 * that receives a single <code>java.lang.String</code> argument. It
 * also overrides the <code>equals</code> method.  
 * 
 * @author $Id$
 */
public class ObjectB {
	String _value;
	
	/**
	 * Creates the object with the specified string value. 
	 * @param value
	 */
	public ObjectB(String value) {
		_value = value;
	}
	
	/**
	 * Overriden implementation of the method in <code>java.lang.Object</code>.
	 * 
	 * @param object An instance of this class
	 * @return Boolean value that is the result of calling the equals method of
	 *         the <code>java.lang.String</code> value of <code>object</code>
	 *         receiving as argument the value of <code>this</code> object.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		return ((ObjectB) object)._value.equals(_value);
	}
}
