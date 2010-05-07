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

import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;

/**
 * This class represents a privileged action that involves 
 * invoking a method reflectively.  
 * 
 * This class does not make the reflective call.  It provides
 * common exception-handling mechanisms for sub-classes to rely upon.  
 *
 * @version $Revision$
 */
abstract class ReflectiveInvokeAction implements PrivilegedExceptionAction {

	private final Method m_method;
	private final Object[] m_args;
	
	ReflectiveInvokeAction(Method method, Object[] args) {
		m_method = method;
		m_args = args;
	}
	
	public Object run() throws Exception {
		try {
			return invokeMethod(m_method, m_args);
		}
		catch (Exception exception) {
			// re-throw exception
			throw exception;
		}
		catch (Throwable throwable) {
			// the method that was invoked reflectively must have thrown a Throwable
			// in this case, wrap the Throwable in an exception
			throw new Exception("Exception occurred during method invocation", throwable);
		}
	}
	
	public abstract Object invokeMethod(Method method, Object[] args) throws Throwable;
}
