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

package org.osgi.service.async;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.ServiceReference;
import org.osgi.util.promise.Promise;

/**
 * The Asynchronous Execution Service. This can be used to make asynchronous
 * invocations on OSGi services and objects through the use of a mediator
 * object.
 * 
 * <p>
 * Typical usage:
 * 
 * <pre>
 *   Async async = ctx.getService(asyncRef);
 *   
 *   ServiceReference&lt;MyService&gt; ref = ctx.getServiceReference(MyService.class);
 *   
 *   MyService mediator = async.mediate(ref, MyService.class);
 *   
 *   Promise&lt;BigInteger&gt; result = async.call(mediator.getSumOverAllValues());
 * </pre>
 * 
 * <p>
 * The {@link Promise} API allows callbacks to be made when asynchronous tasks
 * complete, and can be used to chain Promises.
 * 
 * <p>
 * Multiple asynchronous tasks can be started concurrently, and will run in
 * parallel if the Async Service has threads available.
 */
@ProviderType
public interface Async {

	/**
	 * Create a mediator for the specified object. The mediator is a generated
	 * object that registers the method calls made against it. The registered
	 * method calls can then be run asynchronously using either the
	 * {@link #call(Object)}, {@link #call()}, or {@link #execute()} method.
	 * 
	 * <p>
	 * The values returned by method calls made on a mediated object are opaque
	 * and should not be interpreted.
	 * 
	 * <p>
	 * Normal usage:
	 * 
	 * <pre>
	 * I s = ...;
	 * I i = async.mediate(s, I.class);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * @param target The service object to mediate.
	 * @param iface The type that the mediated object should provide.
	 * @return A mediator for the service object.
	 * @throws IllegalArgumentException If the type represented by iface cannot
	 *         be mediated.
	 */
	<T> T mediate(T target, Class<T> iface);

	/**
	 * Create a mediator for the specified service. The mediator is a generated
	 * object that registers the method calls made against it. The registered
	 * method calls can then be run asynchronously using either the
	 * {@link #call(Object)}, {@link #call()}, or {@link #execute()} method.
	 * 
	 * <p>
	 * The values returned by method calls made on a mediated object are opaque
	 * and should not be interpreted.
	 * 
	 * <p>
	 * This method differs from {@link #mediate(Object, Class)} in that it can
	 * track the availability of the specified service. This is recommended as
	 * the preferred option for mediating OSGi services as asynchronous tasks
	 * may not start executing until some time after they are requested.
	 * Tracking the validity of the {@link ServiceReference} for the service
	 * ensures that these tasks do not proceed with an invalid object.
	 * 
	 * <p>
	 * Normal usage:
	 * 
	 * <pre>
	 * ServiceReference&lt;I&gt; s = ...;
	 * I i = async.mediate(s, I.class);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * @param target The service reference to mediate.
	 * @param iface The type that the mediated object should provide.
	 * @return A mediator for the service object.
	 * @throws IllegalArgumentException If the type represented by iface cannot
	 *         be mediated.
	 */
	<T> T mediate(ServiceReference<? extends T> target, Class<T> iface);

	/**
	 * Invoke the last method call registered by a mediated object as an
	 * asynchronous task. The result of the task can be obtained using the
	 * returned Promise.
	 * 
	 * <p>
	 * Typically the parameter for this method will be supplied inline like
	 * this:
	 * 
	 * <pre>
	 * ServiceReference&lt;I&gt; s = ...;
	 * I i = async.mediate(s, I.class);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * @param r The return value of the mediated call, used for type
	 *        information.
	 * @return A Promise which can be used to retrieve the result of the
	 *         asynchronous task.
	 */
	<R> Promise<R> call(R r);

	/**
	 * Invoke the last method call registered by a mediated object as an
	 * asynchronous task. The result of the task can be obtained using the
	 * returned Promise.
	 * 
	 * <p>
	 * Generally it is preferable to use {@link #call(Object)} like this:
	 * 
	 * <pre>
	 * ServiceReference&lt;I&gt; s = ...;
	 * I i = async.mediate(s, I.class);
	 * Promise&lt;String&gt; p = async.call(i.foo());
	 * </pre>
	 * 
	 * <p>
	 * However this pattern does not work for void methods. Void methods can
	 * therefore be handled like this:
	 * 
	 * <pre>
	 * ServiceReference&lt;I&gt; s = ...;
	 * I i = async.mediate(s, I.class);
	 * i.voidMethod()
	 * Promise&lt;?&gt; p = async.call();
	 * </pre>
	 * 
	 * @return A Promise which can be used to retrieve the result of the
	 *         asynchronous task.
	 */
	Promise<?> call();

	/**
	 * Invoke the last method call registered by a mediated object as a
	 * "fire-and-forget" asynchronous task. This method should be used by
	 * clients in preference to {@link #call()} and {@link #call(Object)} when
	 * no callbacks, or other features of {@link Promise}, are needed.
	 * 
	 * <p>
	 * The advantage of this method is that it allows for greater optimization
	 * of the underlying asynchronous task. Clients are therefore likely to see
	 * better performance when using this method compared to using
	 * {@link #call(Object)} or {@link #call()} and ignoring the returned
	 * Promise. The {@link Promise} returned by this method is different from
	 * the Promise returned by {@link #call(Object)} or {@link #call()}, in that
	 * the returned Promise will resolve when the fire-and-forget task is
	 * successfully started, or fail if the task cannot be started. Note that
	 * there is no <i>happens-before</i> relationship and the returned Promise
	 * may resolve before or after the fire-and-forget task starts, or
	 * completes.
	 * 
	 * <p>
	 * Typically this method is used like {@link #call()}:
	 * 
	 * <pre>
	 * ServiceReference&lt;I&gt; s = ...;
	 * I i = async.mediate(s, I.class);
	 * i.someMethod()
	 * Promise&lt;Void&gt; p = async.execute();
	 * </pre>
	 * 
	 * @return A Promise representing whether the fire-and-forget task was able
	 *         to start.
	 */
	Promise<Void> execute();
}
