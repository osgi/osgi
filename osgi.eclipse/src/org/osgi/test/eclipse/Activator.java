package org.osgi.test.eclipse;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.impl.service.http.HttpServiceController;
import org.osgi.test.director.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class Activator extends AbstractUIPlugin {
	static Handler					handler;
	static Director					director	= new Director();
	static BundleContext			context;
	static HttpServiceController	http		= new HttpServiceController();
	static ILog						log;
	
	/**
	 * The constructor.
	 */
	public Activator() {
		super();
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		try {
			String port = System.getProperty("org.osgi.service.http.port", "8000");
			System.setProperty("org.osgi.service.http.port", port);
			
			Activator.context = context;
			log = getLog();
			super.start(context);
			handler = director.initialize(context);
			http.start(context);			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		director.stop(context);
		http.stop(context);
		handler = null;
	}


	/**
	 * @return
	 */
	public static BundleContext getContext() {
		return context;
	}

}
