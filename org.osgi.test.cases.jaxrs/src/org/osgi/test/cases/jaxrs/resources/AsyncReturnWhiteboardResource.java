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
package org.osgi.test.cases.jaxrs.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

@Path("whiteboard/asyncReturn")
public class AsyncReturnWhiteboardResource {

	@GET
	@Path("cs/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public CompletionStage<String> echoCompletionStage(
			@PathParam("name") String value) {

		CompletableFuture<String> cf = new CompletableFuture<>();

		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				cf.completeExceptionally(e);
				return;
			}
			cf.complete(value);
		}).start();

		return cf;
	}

	@GET
	@Path("p/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Promise<String> echoPromise(@PathParam("name") String value) {

		Deferred<String> d = new Deferred<>();

		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (Exception e) {
				d.fail(e);
				return;
			}
			d.resolve(value);
		}).start();

		return d.getPromise();
	}
}
