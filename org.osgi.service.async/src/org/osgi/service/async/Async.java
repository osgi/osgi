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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;
import org.osgi.util.promise.Promise;

/**
 * <p>The Asynchronous Execution Service. This can be used to make asynchronous
 * invocations on OSGi services and objects through the use of a mediator object.</p>
 * 
 * <p>Typical usage:</p>
 * 
 * <pre>
 *   Async async = ctx.getService(asyncRef);
 *   
 *   ServiceReference&lt;MyService&gt; ref = ctx.getServiceReference(MyService.class);
 *   
 *   MyService asyncMediator = async.mediate(ref);
 *   
 *   Promise&lt;BigInteger&gt; result = async.call(asyncMediator.getSumOverAllValues());
 * </pre>
 * 
 * <p>The {@link Promise} API allows callbacks to be made when asynchronous tasks complete,
 * and can be used to chain Promises.</p>
 * 
 * Multiple asynchronous tasks can be started concurrently, and will run in parallel if
 * the Async service has threads available.
 */
@ProviderType
public interface Async {

	/**
	 * <p>
	 * Create a mediator for the given object. The mediator is a generated
	 * object that registers the method calls made against it. The registered
	 * method calls can then be run asynchronously using either the
	 * {@link #call(Object)} or {@link #call()} method.
	 * </p>
	 * 
	 * <p>
	 * The values returned by method calls made on a mediated object should be
	 * ignored.
	 * </p>
	 * 
	 * <p>
	 * Normal usage:
	 * </p>
	 * 
	 * <pre>
	 * I i = async.mediate(s);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * @param target The service object
	 * @return A mediator for the service object
	 */
	<T> T mediate(T target);

	/**
	 * <p>
	 * Create a mediator for the given service. The mediator is a generated
	 * object that registers the method calls made against it. The registered
	 * method calls can then be run asynchronously using either the
	 * {@link #call(Object)} or {@link #call()} method.
	 * </p>
	 * 
	 * <p>
	 * The values returned by method calls made on a mediated object should be
	 * ignored.
	 * </p>
	 * 
	 * <p>
	 * This method differs from {@link #mediate(Object)} in that it can track
	 * the availability of the backing service. This is recommended as the
	 * preferred option for mediating OSGi services as asynchronous tasks may
	 * not start executing until some time after they are requested. Tracking
	 * the validity of the {@link ServiceReference} for the service ensures that
	 * these tasks do not proceed with an invalid object.
	 * </p>
	 * 
	 * <p>
	 * Normal usage:
	 * </p>
	 * 
	 * <pre>
	 * I i = async.mediate(s);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * @param target The service object
	 * @return A mediator for the service object
	 */
	<T> T mediate(ServiceReference<T> target);

	/**
	 * <p>This method launches the last method call registered by a mediated
	 * object as an asynchronous task. The result of the task can be obtained
	 * using the returned promise</p>
	 * 
	 * <p> Typically the parameter for this method will be supplied inline like this:</p>
	 * 
	 * <pre>
	 * I i = async.mediate(s);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * @param r the return value of the mediated call, used for type information
	 * @return a Promise which can be used to retrieve the result of the 
	 * 		asynchronous execution
	 */
	<R> Promise<R> call(R r);

	/**
	 * <p>This method launches the last method call registered by a mediated
	 * object as an asynchronous task. The result of the task can be obtained
	 * using the returned promise</p>
	 * 
	 * <p>Generally it is preferrable to use {@link #call(Object)} like this:</p>
	 * 
	 * <pre>
	 * I i = async.mediate(s);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 *  
	 * <p>However this pattern does not work for void methods. Void methods can
	 * therefore be handled like this:</p>
	 * 
	 * <pre>
	 * I i = async.mediate(s);
	 * i.voidMethod()
	 * Promise&lt;?&gt; p = async.call();
	 * </pre>
	 * 
	 * @return a Promise which can be used to retrieve the result of the 
	 * 		asynchronous execution
	 */
	 Promise<?> call();

	/**
	 * <p>
	 * This method launches the last method call registered by a mediated object
	 * as an asynchronous task. The task runs as a "fire and forget" process,
	 * and there will be no notification of its success or failure.
	 * </p>
	 */
	void execute();

}
