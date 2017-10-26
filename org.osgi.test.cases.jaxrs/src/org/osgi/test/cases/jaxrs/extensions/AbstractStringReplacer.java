package org.osgi.test.cases.jaxrs.extensions;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

public abstract class AbstractStringReplacer
		implements ReaderInterceptor, WriterInterceptor, ContainerRequestFilter,
		ContainerResponseFilter {

	@Override
	public void aroundWriteTo(WriterInterceptorContext ctx)
			throws IOException, WebApplicationException {
		Object entity = ctx.getEntity();
		if (entity != null) {
			ctx.setEntity(entity.toString().replace(getToReplace(),
					getReplaceWith()));
		}
		ctx.proceed();
	}

	@Override
	public Object aroundReadFrom(ReaderInterceptorContext ctx)
			throws IOException, WebApplicationException {

		if (String.class == ctx.getType()) {
			try (BufferedReader r = new BufferedReader(
					new InputStreamReader(ctx.getInputStream(), UTF_8))) {
				String line = r.readLine();

				return line.replace(getToReplace(), getReplaceWith());
			}
		}
		return ctx.proceed();
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

	public abstract String getToReplace();

	public abstract String getReplaceWith();
}
