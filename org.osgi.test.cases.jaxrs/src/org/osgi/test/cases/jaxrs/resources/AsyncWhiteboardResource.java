package org.osgi.test.cases.jaxrs.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("whiteboard/async")
public class AsyncWhiteboardResource {

	private final Runnable	preResume;
	private final Runnable postResume;

	public AsyncWhiteboardResource(Runnable preResume, Runnable postResume) {
		this.preResume = preResume;
		this.postResume = postResume;
	}

	@GET
	@Path("{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public void echo(@Suspended AsyncResponse async,
			@PathParam("name") String value) {

		new Thread(() -> {
			try {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					preResume.run();
					async.resume(e);
					return;
				}
				preResume.run();
				async.resume(value);
			} finally {
				postResume.run();
			}
		});
	}
}
