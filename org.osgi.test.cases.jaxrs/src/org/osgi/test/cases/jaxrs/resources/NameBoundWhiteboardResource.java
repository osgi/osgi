package org.osgi.test.cases.jaxrs.resources;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.test.cases.jaxrs.extensions.BoundStringReplacer.NameBound;

@Path("whiteboard/name")
public class NameBoundWhiteboardResource {

	private final Set<String> values = new TreeSet<>(
			Arrays.asList("fizz", "buzz", "fizzbuzz"));

	@GET
	@Path("bound")
	@Produces(MediaType.TEXT_PLAIN)
	@NameBound
	public String getTransformedValues() {
		return values.toString();
	}

	@GET
	@Path("unbound")
	@Produces(MediaType.TEXT_PLAIN)
	public String getRawValues() {
		return values.toString();
	}

}
