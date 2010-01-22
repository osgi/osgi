/*
 * Copyright 2010 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.osgi.impl.service.jndi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.Context;

/**
 * Utility class for reflection calls made by the JNDI implementation
 *
 * 
 * @version $Revision$
 */
class ReflectionUtils {

	/**
	 * This method uses reflection to invoke the given Method
	 * on the passed in Context instance.  This method also
	 * catches the InvocationTargeException, in order to always
	 * re-throw the original exception from the Method invocation. 
	 * 
	 * @param method the Method to invoke
	 * @param contextToInvokeOn the Context to invoke the Method on
	 * @param args the arguments to the Method
	 * @return an Object representing the result of the Method call
	 * @throws Throwable
	 */
	static Object invokeMethodOnContext(Method method, Context contextToInvokeOn, Object[] args) throws Throwable {
		return invokeMethodOnObject(method, contextToInvokeOn, args);
	}

	
	
	/**
	 * This method uses reflection to invoke the given Method
	 * on the passed in Object instance.  This method also
	 * catches the InvocationTargeException, in order to always
	 * re-throw the original exception from the Method invocation. 
	 * 
	 * @param method the Method to invoke
	 * @param objectToInvokeOn the Object to invoke the Method on
	 * @param args the arguments to the Method
	 * @return an Object representing the result of the Method call
	 * @throws Throwable
	 */
	static Object invokeMethodOnObject(Method method, Object objectToInvokeOn, Object[] args) throws IllegalAccessException, Throwable {
		try {
			return method.invoke(objectToInvokeOn, args); 
		} catch (InvocationTargetException invocationException) {
			throw invocationException.getTargetException();
		}
	}
}
