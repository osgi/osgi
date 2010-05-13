/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
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

package org.osgi.test.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Useful to mock Framework interfaces which may change in future releases.
 * 
 * The mock will delegate method calls on the mocked interface to the delegate
 * object. The delegate object does NOT need to implement the interface and does
 * NOT need to implement all the methods on the interface.
 * 
 * @version $Id$
 */
public class MockFactory {
	private MockFactory() {
		/* non-instantiable */
	}
	
	/**
	 * Return a mocked object for the specified interface. The mocked object
	 * will delegate method calls on the specified interface to the specified
	 * delegate object. The specified delegate object does NOT need to implement
	 * the specified interface and does NOT need to implement all the methods on
	 * the specified interface.
	 * 
	 * <p>
	 * The mocked object will throw UnsupportedOperationException if the
	 * specified delegate is <code>null</code> or if the specified delegate does
	 * not implement the method called on the mocked object.
	 * 
	 * @param interfce The interface to mock.
	 * @param delegate The object to which method calls on the mocked object are
	 *        delegated.
	 * @return A mocked which implements the specified interface and delegates
	 *         method calls to the specified delegate object.
	 */
	public static Object newMock(final Class interfce, final Object delegate) {
		ClassLoader proxyLoader = ((delegate == null) ? MockFactory.class
				: delegate.getClass()).getClassLoader();
		return Proxy.newProxyInstance(proxyLoader, new Class[] {interfce},
				new InvocationHandler() {
					final Class	delegateClass	= (delegate == null) ? null
														: delegate.getClass();

					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						if (delegate == null) {
							throw new UnsupportedOperationException();
						}
						Method delegateMethod;
						try {
							delegateMethod = delegateClass.getMethod(method
									.getName(), method.getParameterTypes());
						}
						catch (NoSuchMethodException e) {
							throw new UnsupportedOperationException();
						}
						delegateMethod.setAccessible(true);
						try {
							return delegateMethod.invoke(delegate, args);
						}
						catch (IllegalAccessException e) {
							throw e;
						}
						catch (InvocationTargetException e) {
							Throwable cause = e.getCause();
							if (cause == null) {
								cause = e;
							}
							throw cause;
						}
					}
				});
	}
}
