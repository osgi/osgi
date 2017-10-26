package org.osgi.test.cases.jaxrs.extensions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class NPEMapper implements ExceptionMapper<NullPointerException> {

	@Override
	public Response toResponse(NullPointerException arg0) {
		return Response.status(Status.PAYMENT_REQUIRED)
				.entity("NPE")
				.type(MediaType.TEXT_PLAIN)
				.build();
	}

}
