/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
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
package org.osgi.service.jaxrs;

import javax.ws.rs.client.InvocationCallback;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

/**
 * A PromiseHandler provides a simple way to make asynchronous JAX-RS client
 * calls return a {@link Promise}
 * 
 * @author $Id$
 * @param <T> The type of the response
 */
@ConsumerType
public class PromiseHandler<T> implements InvocationCallback<T> {

	private final Deferred<T> deferred = new Deferred<>();

	@Override
	public void completed(T arg0) {
		deferred.resolve(arg0);
	}

	@Override
	public void failed(Throwable arg0) {
		deferred.fail(arg0);
	}

	/**
	 * @return the Promise that will be resolved or failed by the result of this
	 *         request
	 */
	public Promise<T> getPromise() {
		return deferred.getPromise();
	}

}
