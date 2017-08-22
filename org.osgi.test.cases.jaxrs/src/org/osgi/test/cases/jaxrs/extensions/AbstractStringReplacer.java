package org.osgi.test.cases.jaxrs.extensions;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

public abstract class AbstractStringReplacer implements WriterInterceptor {

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

	public abstract String getToReplace();

	public abstract String getReplaceWith();
}
