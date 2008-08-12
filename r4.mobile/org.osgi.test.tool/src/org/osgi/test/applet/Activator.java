package org.osgi.test.applet;

/*
 * Author: pkriens
 * Created: Wednesday, September 05, 2001 9:23:01 AM
 * Modified: Wednesday, September 05, 2001 9:23:01 AM
 */
import netscape.application.*;

import org.osgi.framework.*;

public class Activator extends Thread implements BundleActivator, WindowOwner {
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
		applet = new TestApplet(org.osgi.test.director.Director.handler,
				context);
		Size size;
		mainWindow = new ExternalWindow();
		mainWindow.setOwner(this);
		size = mainWindow.windowSizeForContentSize(600, 572);
		mainWindow.sizeTo(size.width, size.height);
		mainWindow.show();
		applet.setMainRootView(mainWindow.rootView());
		applet.run();
		System.out.println("Stopping applet in thread");
	}

	public boolean windowWillShow(Window arg0) {
		return true;
	}

	public void windowDidShow(Window arg0) {
	}

	public boolean windowWillHide(Window arg0) {
		return true;
	}

	public void windowDidHide(Window arg0) {
		System.exit(0);
	}

	public void windowDidBecomeMain(Window arg0) {
	}

	public void windowDidResignMain(Window arg0) {
	}

	public void windowWillSizeBy(Window arg0, Size arg1) {
	}
}
