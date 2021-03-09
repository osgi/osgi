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
package org.osgi.test.cases.cm.junit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class represents a method call.
 * 
 * <p>
 * A method call is represented by the name of the method, the type of the
 * parameters and the actual parameters.
 * <p>
 * When <code>invoke</code> is called, all "reflection magic" is taken care of
 * inside this class.
 */
public class MethodCall {
	private String					name;
	private Class< ? >					clazz;
	private Class< ? >[]				paramClasses;
	private Object[]				paramObjects;
	private static final Class< ? >[]	NO_CLASSES	= new Class[0];
	private static final Object[]	NO_OBJECTS	= new Object[0];

	/**
	 * Construct a new MethodCall with no parameters
	 * 
	 * @param name the name of the method
	 */
	public MethodCall(Class< ? > clazz, String name) {
		this.name = name;
		this.clazz = clazz;
		this.paramClasses = NO_CLASSES;
		this.paramObjects = NO_OBJECTS;
	}

	/**
	 * Constructs a new MethodCall with one parameter.
	 * 
	 * @param name the name of the method
	 * @param paramClass the type of the paramater
	 * @param paramObject the parameter
	 */
	public MethodCall(Class< ? > clazz, String name, Class< ? > paramClass,
			Object paramObject) {
		this(clazz, name);
		this.paramClasses = new Class[] {paramClass};
		this.paramObjects = new Object[] {paramObject};
	}

	/**
	 * Constructs a new MethodCall with more than one parameter.
	 * 
	 * @param name the name of the method
	 * @param paramClasses an array containing the paramatertypes.
	 * @param paramObjects an array containing the parameters
	 */
	public MethodCall(Class< ? > clazz, String name, Class< ? >[] paramClasses,
			Object[] paramObjects) {
		this(clazz, name);
		this.paramClasses = paramClasses;
		this.paramObjects = paramObjects;
	}

	/**
	 * Returns the name of the method.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Invokes the method with the specified parameters.
	 * 
	 * @param o the object to invoke the method on
	 * @return whatever the method returns (see
	 *         java.lang.reflect.Method.invoke() for details)
	 * @throws Throwable rethrows anything that was thrown by the reflective
	 *         invoke call. < b>Note! </b>
	 *         <code>InvocationTargetException</code> is unwrapped and the
	 *         "real" Exception is rethrown!
	 */
	public Object invoke(Object o) throws Throwable {
		Object returnObject = null;
		try {
			Class< ? > useClass = clazz;
			if (useClass == null)
				useClass = o.getClass();
			Method m = useClass.getDeclaredMethod(name, paramClasses);
			returnObject = m.invoke(o, paramObjects);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		return returnObject;
	}
}
