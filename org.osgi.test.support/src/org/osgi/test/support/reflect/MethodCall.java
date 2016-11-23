/*
 * Copyright (c) OSGi Alliance (2000, 2011). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

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
