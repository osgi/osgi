package org.osgi.test.cases.jaxrs.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("whiteboard/session")
public class SessionManipulator {

	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getValue(@Context HttpServletRequest req,
			@PathParam("name") String name) {
		return String.valueOf(req.getSession().getAttribute(name));
	}

	@PUT
	@Path("{name}")
	public Response getValue(@Context HttpServletRequest req,
			@PathParam("name") String name, String body) {
		req.getSession().setAttribute(name, body);
		return Response.ok().build();
	}

}
