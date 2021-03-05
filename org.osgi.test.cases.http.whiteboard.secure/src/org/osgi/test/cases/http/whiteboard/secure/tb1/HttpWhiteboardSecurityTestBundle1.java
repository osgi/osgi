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
package org.osgi.test.cases.http.whiteboard.secure.tb1;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

public class HttpWhiteboardSecurityTestBundle1 implements BundleActivator {

	private static final String							JAVA_SERVLET_TEMP_DIR_PROP	= "javax.servlet.content.tempdir";

	private ServiceRegistration<ServletContextHelper>	registration;

	@Override
	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/");
		registration = context.registerService(ServletContextHelper.class, new FileServletContextHelper(), properties);

		registerSetupServlet(context);

		setupUploadServlet(context, "/postdefault", null);
		setupUploadServlet(context, "/postfailedwrite", "/some/wired/path");
		setupUploadServlet(context, "/postlocation", "/upload/test/location");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
	}

	class FileServletContextHelper extends ServletContextHelper {

		@Override
		public URL getResource(String name) {
			File file = new File(name);

			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private void registerSetupServlet(final BundleContext context)
			throws Exception {
		final Dictionary<String,Object> servletProps = new Hashtable<String,Object>();
		servletProps.put(HTTP_WHITEBOARD_SERVLET_PATTERN, "/setup");

		final Servlet initServlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void init(ServletConfig config) throws ServletException {
				super.init(config);
				config.getServletContext().setAttribute(
						JAVA_SERVLET_TEMP_DIR_PROP, "/upload/test/default");
			}
		};

		context.registerService(Servlet.class.getName(), initServlet,
				servletProps);
	}

	private void setupUploadServlet(final BundleContext context,
			final String path, final String location) throws Exception {
		final Dictionary<String,Object> servletProps = new Hashtable<String,Object>();
		servletProps.put(HTTP_WHITEBOARD_SERVLET_PATTERN, path);
		servletProps.put(HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED,
				Boolean.TRUE);
		if (location != null) {
			servletProps.put(HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION,
					location);
		}
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
