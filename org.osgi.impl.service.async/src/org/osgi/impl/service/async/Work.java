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
package org.osgi.impl.service.async;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.osgi.util.promise.Deferred;

public class Work<T> implements Runnable {

	final MethodCall					methodCall;
	
	private final Deferred<T> deferred;

	private final AccessControlContext acc;
	
	public Work(MethodCall methodCall, Deferred<T> deferred) {
		this.methodCall = methodCall;
		this.deferred = deferred;
		this.acc = AccessController.getContext();
	}


	@Override
	public void run() {
		try {
			final Object service = methodCall.getService();
			// This is necessary for non public methods. The original mediator call must
			// have been allowed to happen, so this should always be safe.
			methodCall.method.setAccessible(true);
			
			@SuppressWarnings("unchecked")
			T returnValue = AccessController.doPrivileged(
					(PrivilegedExceptionAction<T>) () -> (T) methodCall.method
							.invoke(service, methodCall.arguments),
					acc);
			
			
			
			deferred.resolve(returnValue);
			
		} catch (PrivilegedActionException pae) {
			Throwable targetException = pae.getCause();
			if(targetException instanceof InvocationTargetException) {
				targetException = ((InvocationTargetException) targetException).getTargetException();
			}
			deferred.fail(targetException);
		} catch (Exception e) {
			deferred.fail(e);
		} finally {
			methodCall.releaseService();
		}
	}
}
