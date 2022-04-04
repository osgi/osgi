/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.servlet.whiteboard.secure.tb2;

import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED;
import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE;
import static org.osgi.service.servlet.whiteboard.HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpWhiteboardSecurityTestBundle2 implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		setupUploadServlet(context, "/post");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	private void setupUploadServlet(final BundleContext context,
			final String path) throws Exception {
		final Dictionary<String,Object> servletProps = new Hashtable<String,Object>();
		servletProps.put(HTTP_WHITEBOARD_SERVLET_PATTERN, path);
		servletProps.put(HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED,
				Boolean.TRUE);
		servletProps.put(HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE, 1024L);

		final Servlet uploadServlet = new HttpServlet() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void doPost(HttpServletRequest req,
					HttpServletResponse resp)
					throws IOException, ServletException {
				// nothing to do
			}
		};

		context.registerService(Servlet.class.getName(), uploadServlet,
				servletProps);
	}
}
