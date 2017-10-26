package org.osgi.test.cases.jaxrs.resources;

import static javax.ws.rs.core.MediaType.*;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
