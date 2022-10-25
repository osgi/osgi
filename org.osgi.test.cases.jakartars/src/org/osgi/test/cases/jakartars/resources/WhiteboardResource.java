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

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("whiteboard/resource")
public class WhiteboardResource {

	private final Set<String> values = new TreeSet<>(
			Arrays.asList("fizz", "buzz", "fizzbuzz"));

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getValues() {
		return values.toString();
	}

	@PUT
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addValue(@PathParam("name") String value) {
		if (values.add(value)) {
			return Response.ok().build();
		} else {
			return Response.status(CONFLICT).build();
		}
	}

	@POST
	@Path("{oldName}/{newName}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response addValue(@PathParam("oldName") String oldName,
			@PathParam("newName") String newName) {
		if (values.remove(oldName)) {
			if (values.add(newName)) {
				return Response.ok().build();
			} else {
				values.add(oldName);
				return Response.status(CONFLICT).entity(newName).build();
			}
		} else {
			return Response.status(CONFLICT).entity(oldName).build();
		}
	}

	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response containsValue(@PathParam("name") String value) {
		return Response
				.status(values.contains(value) ? Status.OK : Status.NOT_FOUND)
				.entity(value)
				.build();
	}

	@DELETE
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteValue(@PathParam("name") String value) {
		if (values.remove(value)) {
			return Response.ok().build();
		} else {
			return Response.status(CONFLICT).build();
		}
	}

}
