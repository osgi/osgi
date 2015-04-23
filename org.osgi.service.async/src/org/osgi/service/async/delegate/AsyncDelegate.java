/*
 * Copyright (c) OSGi Alliance (2014, 2015). All Rights Reserved.
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

package org.osgi.service.async.delegate;

import java.lang.reflect.Method;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.promise.Promise;

/**
 * This interface is used by services to allow them to optimize Asynchronous
 * calls where they are capable of executing more efficiently.
 * 
 * <p>
 * This may mean that the service has access to its own thread pool, or that it
 * can delegate work to a remote node, or act in some other way to reduce the
 * load on the Asynchronous Services implementation when making an asynchronous
 * call.
 */
@ConsumerType
public interface AsyncDelegate {
	/**
	 * Invoke the specified method as an asynchronous task with the specified
	 * arguments.
	 * 
	 * <p>
	 * This method can be used by clients, or the Async Service, to optimize
	 * Asynchronous execution of methods.
	 * 
	 * <p>
	 * When called, this method should invoke the supplied method using the
	 * supplied arguments asynchronously, returning a Promise that can be used
	 * to access the result.
	 * 
	 * <p>
	 * If the method cannot be executed asynchronously by this method then
	 * {@code null} must be returned.
	 * 
	 * @param m The method to be asynchronously invoked.
	 * @param args The arguments to be used to invoke the method.
	 * @return A Promise representing the asynchronous result, or {@code null}
	 *         if this method cannot be asynchronously invoked.
	 * @throws Exception An exception should be thrown only if there was a
	 *         serious error that prevented the asynchronous task from starting.
	 *         For example, the specified method does not exist on this object.
	 *         Exceptions must not be thrown to indicate that the call does not
	 *         support asynchronous invocation. Instead this method must return
	 *         {@code null}. Exceptions must also not be thrown to indicate a
	 *         failure from the execution of the underlying method. This must be
	 *         handled by failing the returned Promise.
	 */
	Promise<?> async(Method m, Object[] args) throws Exception;

	/**
	 * Invoke the specified method as a "fire-and-forget" asynchronous task with
	 * the specified arguments.
	 * 
	 * <p>
	 * This method can be used by clients, or the Async Service, to optimize
	 * Asynchronous execution of methods.
	 * 
	 * <p>
	 * When called, this method should invoke the specified method using the
	 * specified arguments asynchronously. This method differs from
	 * {@link #async(Method, Object[])} in that it does not return a Promise.
	 * This method therefore allows the implementation to perform more
	 * aggressive optimizations because the end result of the invocation does
	 * not need to be returned to the caller.
	 * 
	 * <p>
	 * If the method cannot be executed asynchronously by this method then
	 * {@code false} must be returned.
	 * 
	 * @param m The method to be asynchronously invoked.
	 * @param args The arguments to be used to invoke the method.
	 * @return {@code true} if the asynchronous execution request has been
	 *         accepted, or {@code false} if this method cannot be
	 *         asynchronously invoked by the AsyncDelegate.
	 * @throws Exception An exception should be thrown only if there was a
	 *         serious error that prevented the asynchronous task from starting.
	 *         For example, the specified method does not exist on this object.
	 *         Exceptions must not be thrown to indicate that the call does not
	 *         support asynchronous invocation. Instead this method must return
	 *         {@code false}. Exceptions must also not be thrown to indicate a
	 *         failure from the execution of the underlying method.
	 */
	boolean execute(Method m, Object[] args) throws Exception;
}
