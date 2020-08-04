package org.osgi.test.cases.http.whiteboard.secure.tb2;

import static org.osgi.service.http.whiteboard.HttpWhiteboardConstants.*;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

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
