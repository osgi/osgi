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
package org.osgi.test.cases.jakartars.resources;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.osgi.util.promise.Promise;

@Path("echo")
public class EchoResource {

	@GET
	@Path("body")
	public Response echoBody(@Context HttpHeaders headers, String body) {
		MediaType mediaType = headers.getMediaType();
		boolean returnOSGiText = mediaType.getType().equals("osgi")
				&& mediaType.getSubtype().equals("text");
		return Response.ok(body)
				.type(returnOSGiText ? mediaType : TEXT_PLAIN_TYPE)
				.build();
	}

	@GET
	@Path("header")
	@Produces(TEXT_PLAIN)
	public Response echoHeader(@HeaderParam("echo") String echo) {
		return Response.ok(echo)
				.header("echo", echo)
				.type(TEXT_PLAIN)
				.build();
	}

	@GET
	@Path("promise")
	@Produces(TEXT_PLAIN)
	public void echoHeader(@Suspended AsyncResponse async,
			@HeaderParam("echo") Promise<String> echo) {

		echo.onSuccess(s -> async.resume(Response.ok(echo.getValue())
				.type(TEXT_PLAIN)
				.build()));
	}

}
