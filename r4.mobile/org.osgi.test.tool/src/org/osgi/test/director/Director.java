/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.director;

import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;
import org.osgi.framework.*;
import org.osgi.test.shared.*;

/**
 * 
 * Bundle activator for the director bundle.
 * 
 * This bundle is installed in a framework and is responsible for provding a
 * test harness for testcases. The framework registry is used to find target
 * frameworks (the once that need to be tested) and testcases. Testcases are
 * installed as other bundles wich register a testcase service.
 * <p>
 * When the bundle is started, a UI will pop up.
 * <p>
 * The osgi-target.jar should be installed in the target framework. It will
 * broadcast its net address which will be picked up by the Discovery class
 * here. This will register a service that the handlers are listening to.
 */
public class Director implements BundleActivator {
	BundleContext			context;		// Framework context
	boolean					cont	= true; // To keep us alive
	Discovery				discovery;		// Simplified JINI ;-)
	BundleActivator			activator;		// Activator of applet start
	// code
	public static Handler	handler;		// The handler for the
	// applet/script
	IApplet					applet;		// Results.
	TestCommands			commands;		// CommandProvider

	/**
	 * BundleActivator entrance, called by the framework to start us.
	 * 
	 * Initialize and start the correct configuration. Properties are used to
	 * start a script or a UI or both.
	 */
	public void start(BundleContext context) throws Exception {
		initialize(context);
		String ui = getProperty("org.osgi.test.ui", null);
		String script = getProperty("org.osgi.test.batch.script", null);
		if (script != null) {
			if (ui == null)
				ui = "false";
			Thread thread = new Thread() {
				public void run() {
					doScript();
				}
			};
			thread.start();
		}
		if (ui == null || ui.equals("true"))
			try {
				//
				// do not link directly to the UI so that we can ship without
				// any UI code.
				//
				Class act = Class.forName("org.osgi.test.applet.Activator");
				activator = (BundleActivator) act.newInstance();
				activator.start(context);
			}
			catch (Throwable t) {
				System.out.println("No applet in JAR file");
				t.printStackTrace();
			}
	}

	public Handler initialize(BundleContext context) throws Exception {
		this.context = context;
		handler = new Handler(context); // Controller
		discovery = new Discovery(context); // Our mini JINI
		commands = new TestCommands(handler, context); // Interface for
		// script/console
		return handler;
	}

	/**
	 * Execute a script specified by org.osgi.test.batch.script
	 */
	public void doScript() {
		int port = 3191;
		String script = getProperty("org.osgi.test.batch.script", null);
		String target = getProperty("org.osgi.test.batch.target", "localhost");
		String out = getProperty("org.osgi.test.batch.out", null);
		String quit = getProperty("org.osgi.test.batch.quittarget", null);
		int status = 1;
		StringTokenizer st = new StringTokenizer(target, ":");
		String host = st.nextToken();
		if (st.hasMoreTokens())
			port = Integer.parseInt(st.nextToken());
		try {
			System.out.println("Running script on " + host + ":" + port + " "
					+ new URL(script));
			handler.startRun(host, port, IHandler.OPTION_FOREVER);
			ScriptEditor editor = new ScriptEditor("batch", new URL(script));
			handler.getRun().doTestCase(editor);
		}
		catch (java.net.ConnectException e) {
			System.out.println("Could not connect to target: " + target);
		}
		catch (Exception e) {
			System.out.println("Failure in test run: " + script);
			e.printStackTrace();
		}
		//
		// Now process the results. We can deliver XML or HTML result
		//
		String result = handler.stopRun();
		try {
			if (out == null)
				out = result;
			if (out.endsWith(".html")) {
				String url = handler.getBaseURL() + "?source=" + result
						+ "&style=testresult.xsl";
				System.out.println("Reading from " + url);
				handler.send(new URL(url), out);
			}
			else
				handler.send(handler.getResult(result).toURL(), out);
			status = 0;
		}
		catch (IOException e) {
			System.out.println("Could not save result: " + e + " in " + out);
			e.printStackTrace();
			status = 1;
		}
		try {
			if (quit != null && quit.trim().equalsIgnoreCase("true")) {
				Thread.sleep(2000);
				handler.startRun(host, port, 0);
				handler.getRun().rebootTarget(-1);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(status);
	}

	/**
	 * Called by the framework to stop our bundle.
	 */
	public void stop(BundleContext bc) throws Exception {
		if (activator != null)
			activator.stop(context);
		discovery.close();
		commands.close();
	}

	public void setApplet(IApplet applet) {
		this.applet = applet;
	}

	public String getProperty(String name, String def) {
		String result = System.getProperty(name);
		if (result == null) {
			result = context.getProperty(name);
		}
		return (result == null) ? def : result;
	}
}
