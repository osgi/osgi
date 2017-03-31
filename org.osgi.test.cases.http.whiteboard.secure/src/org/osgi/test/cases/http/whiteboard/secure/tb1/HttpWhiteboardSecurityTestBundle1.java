package org.osgi.test.cases.http.whiteboard.secure.tb1;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

public class HttpWhiteboardSecurityTestBundle1 implements BundleActivator {

	private ServiceRegistration<ServletContextHelper>	registration;

	public void start(BundleContext context) throws Exception {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, HttpWhiteboardConstants.HTTP_WHITEBOARD_DEFAULT_CONTEXT_NAME);
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, "/");
		registration = context.registerService(ServletContextHelper.class, new FileServletContextHelper(), properties);
	}

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

}
