package org.osgi.test.cases.jaxrs.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("whiteboard/string")
public class StringResource {

	private final String message;

	public StringResource(String message) {
		this.message = message;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getValues() {
		return message;
	}

}
