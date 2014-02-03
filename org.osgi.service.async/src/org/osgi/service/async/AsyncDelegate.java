/*
 * Copyright (c) OSGi Alliance (2014). All Rights Reserved.
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

package org.osgi.service.async;

import java.lang.reflect.Method;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.promise.Promise;

/**
 * This interface is used by services to allow them to optimize Asynchronous calls where they are capable
 * of executing more efficiently.
 * 
 * This may mean that the service has access to its own thread pool, or that it can delegate work to a remote 
 * node, or act in some other way to reduce the load on the Asynchronous Services implementation when making
 * an asynchronous call.
 */
@ConsumerType
public interface AsyncDelegate {
	/**
	 * This method can be used by the Async service to optimize Asynchronous execution.
	 * 
	 * When called, the {@link AsyncDelegate} should execute the supplied method using
	 * the supplied arguments asynchronously, returning a promise that can be used
	 * to access the result. 
	 * 
	 * If the method cannot be executed asynchronously by the delegate then it should
	 * return <code>null</code>.
	 * 
	 * @param m the method that should be asynchronously executed
	 * @param args the arguments that should be used to invoke the method
	 *   
	 * @return A promise representing the asynchronous result, or <code>null</code> if
	 * this method cannot be asynchronously invoked.
	 */
	Promise<?> async( Method m, Object[] args) throws Exception;
}
