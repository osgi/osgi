package org.osgi.test.cases.jaxrs.extensions;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

public class HeaderStringReplacer extends StringReplacer
		implements ContainerRequestFilter, ContainerResponseFilter {

	public HeaderStringReplacer(String toReplace, String replaceWith) {
		super(toReplace, replaceWith);
	}

	@Override
	public void filter(ContainerRequestContext arg0,
			ContainerResponseContext ctx) throws IOException {
		String header = ctx.getHeaderString("echo");
		ctx.getHeaders().remove("echo");
		ctx.getHeaders().putSingle("echo",
				header.replace(getToReplace(), getReplaceWith()));
	}

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		String header = ctx.getHeaderString("echo");
		ctx.getHeaders().remove("echo");
		ctx.getHeaders().putSingle("echo",
				header.replace(getToReplace(), getReplaceWith()));
	}
}
