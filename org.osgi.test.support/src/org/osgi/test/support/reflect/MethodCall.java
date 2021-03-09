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

package org.osgi.test.support.reflect;

import static junit.framework.TestCase.fail;

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
	private final String		methodInterface;
	private final String		methodName;
	private final Class< ? >[]	parameters;

	/**
	 * Constructs a new MethodCall with more than one parameter.
	 * 
	 * @param name the name of the method
	 * @param parameters an array containing the parameter types.
	 */
	public MethodCall(String methodInterface, String methodName,
			Class< ? >... parameters) {
		this.methodInterface = methodInterface;
		this.methodName = methodName;
		this.parameters = parameters;
	}

	/**
	 * Returns the name of the method.
	 */
	public String getName() {
		if (methodInterface == null) {
			return methodName;
		}
		return methodInterface + "." + methodName;
	}

	/**
	 * Invokes the method with the specified arguments.
	 * 
	 * @param o the object to invoke the method on
	 * @param arguments an array containing the arguments
	 * @return whatever the method returns (see
	 *         java.lang.reflect.Method.invoke() for details)
	 * @throws Throwable rethrows anything that was thrown by the reflective
	 *         invoke call. < b>Note!</b> <code>InvocationTargetException</code>
	 *         is unwrapped and the "real" Exception is rethrown!
	 */
	public Object invoke(Object o, Object... arguments) throws Throwable {
		Class< ? > methodSource = o.getClass();
		if (methodInterface != null) {
			interfaceSearch: while (methodSource != null) {
				for (Class< ? > interf : methodSource.getInterfaces()) {
					if (methodInterface.equals(interf.getName())) {
						methodSource = interf;
						break interfaceSearch;
					}
				}
				methodSource = methodSource.getSuperclass();
			}
			if (methodSource == null) {
				fail("Interface " + methodInterface
						+ " not found on target object.");
			}
		}
		Method m = methodSource.getMethod(methodName, parameters);
		try {
			return m.invoke(o, arguments);
		}
		catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				cause = e;
			}
			throw cause;
		}
	}
}
