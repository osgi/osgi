/*
 * Copyright (c) OSGi Alliance (2010, 2017). All Rights Reserved.
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

package org.osgi.service.jaxrs.client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.util.promise.Promise;

/**
 * A specialization of the {@link RxInvoker} which creates {@link Promise}
 * instances.
 * <p>
 * Bundles may obtain an instance of a {@link PromiseRxInvoker} using a
 * {@link ClientBuilder} obtained from the service registry and calling the
 * {@link javax.ws.rs.client.Invocation.Builder#rx(Class)} method.
 * 
 * @author $Id$
 */
@SuppressWarnings("rawtypes")
@ProviderType
public interface PromiseRxInvoker extends RxInvoker<Promise> {

	@Override
	Promise<Response> delete();

	@Override
	<R> Promise<R> delete(Class<R> arg0);

	@Override
	<R> Promise<R> delete(GenericType<R> arg0);

	@Override
	Promise<Response> get();

	@Override
	<R> Promise<R> get(Class<R> arg0);

	@Override
	<R> Promise<R> get(GenericType<R> arg0);

	@Override
	Promise<Response> head();

	@Override
	<R> Promise<R> method(String arg0, Class<R> arg1);

	@Override
	<R> Promise<R> method(String arg0, Entity< ? > arg1, Class<R> arg2);

	@Override
	<R> Promise<R> method(String arg0, Entity< ? > arg1, GenericType<R> arg2);

	@Override
	Promise<Response> method(String arg0, Entity< ? > arg1);

	@Override
	<R> Promise<R> method(String arg0, GenericType<R> arg1);

	@Override
	Promise<Response> method(String arg0);

	@Override
	Promise<Response> options();

	@Override
	<R> Promise<R> options(Class<R> arg0);

	@Override
	<R> Promise<R> options(GenericType<R> arg0);

	@Override
	<R> Promise<R> post(Entity< ? > arg0, Class<R> arg1);

	@Override
	<R> Promise<R> post(Entity< ? > arg0, GenericType<R> arg1);

	@Override
	Promise<Response> post(Entity< ? > arg0);

	@Override
	<R> Promise<R> put(Entity< ? > arg0, Class<R> arg1);

	@Override
	<R> Promise<R> put(Entity< ? > arg0, GenericType<R> arg1);

	@Override
	Promise<Response> put(Entity< ? > arg0);

	@Override
	Promise<Response> trace();

	@Override
	<R> Promise<R> trace(Class<R> arg0);

	@Override
	<R> Promise<R> trace(GenericType<R> arg0);
}
