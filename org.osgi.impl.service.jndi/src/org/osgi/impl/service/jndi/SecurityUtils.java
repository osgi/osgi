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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Utility class for security-related operations in the JNDI implementation.  
 *
 * 
 * @version $Revision$
 */
class SecurityUtils {

	private SecurityUtils() {
		// construction of this object is not allowed
	}
	
	
	/**
	 * Invokes the specified action in a doPrivileged() block, and 
	 * returns the result.  
	 * 
	 * @param action the PrivilegedExceptionAction to execute
	 * @return the resulting Object of the operation
	 * @throws Exception the exception thrown (if any) by the action itself
	 */
	static Object invokePrivilegedAction(final PrivilegedExceptionAction action) throws Exception {
		try {
			return AccessController.doPrivileged(action);
		}
		catch (PrivilegedActionException e) {
			throw e.getException();
		}
	}
	
	
	/**
	 * Invokes the specified action, which does not require a return
	 * @param action the PrivilegedExceptionAction to execute
	 * @throws Exception the exception thrown (if any) by the action itself
	 */
	static void invokePrivilegedActionNoReturn(final PrivilegedExceptionAction action) throws Exception {
		try {
			AccessController.doPrivileged(action);
		}
		catch (PrivilegedActionException e) {
			throw e.getException();
		}
	}
}
