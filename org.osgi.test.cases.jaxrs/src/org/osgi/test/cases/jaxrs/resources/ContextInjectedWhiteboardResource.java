package org.osgi.test.cases.jaxrs.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("whiteboard/context")
public class ContextInjectedWhiteboardResource {

	public static final String	CUSTOM_HEADER	= "customHeader";

	@Context
	private HttpHeaders headers;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String headerReplay() {
		return headers.getHeaderString(CUSTOM_HEADER);
	}

}
