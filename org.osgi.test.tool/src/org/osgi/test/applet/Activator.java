package org.osgi.test.applet;

/*
 * Author: pkriens
 * Created: Wednesday, September 05, 2001 9:23:01 AM
 * Modified: Wednesday, September 05, 2001 9:23:01 AM
 */
import netscape.application.*;
import org.osgi.framework.*;

public class Activator extends Thread implements BundleActivator {
	TestApplet		applet;
	ExternalWindow	mainWindow;
	BundleContext	context;

	public void start(BundleContext context) {
		this.context = context;
		setPriority(MAX_PRIORITY);
		start();
	}

	public void stop(BundleContext context) {
		applet.stopRunning();
	}

	public void run() {
		System.out.println("Starting applet in thread");
		applet = new TestApplet(org.osgi.test.director.Director.handler,
				context);
		Size size;
		mainWindow = new ExternalWindow();
		size = mainWindow.windowSizeForContentSize(600, 572);
		mainWindow.sizeTo(size.width, size.height);
		mainWindow.show();
		applet.setMainRootView(mainWindow.rootView());
		applet.run();
		System.out.println("Stopping applet in thread");
	}
}
