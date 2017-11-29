/*
 * Copyright (c) OSGi Alliance (2017). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.jaxrs.extensions;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

public abstract class AbstractStringReplacer
		implements ReaderInterceptor, WriterInterceptor {

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

	public abstract String getToReplace();

	public abstract String getReplaceWith();
}
