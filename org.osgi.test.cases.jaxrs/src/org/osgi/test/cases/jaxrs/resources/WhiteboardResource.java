package org.osgi.test.cases.jaxrs.resources;

import static javax.ws.rs.core.Response.Status.CONFLICT;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
