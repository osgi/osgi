/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Telecom AB. 2001.
 * This source code is owned by Ericsson Telecom AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.syncbundlelistener.tbc;

import java.lang.reflect.*;
import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;
import org.osgi.test.cases.logproxy.*;
import org.osgi.test.service.*;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * @author Ericsson Telecom AB
 */
public class Activator extends Thread implements LogProxy,
		SynchronousBundleListener, BundleActivator {
	private PermissionAdmin		permissionAdmin;
	private ServiceReference	serviceRef;
	BundleContext				_context;
	ServiceReference			_linkRef;
	ServiceRegistration			_sr;
	TestCaseLink				_link;
	String						_tcHome;
	boolean						_continue		= true;
	Bundle						tb;
	int							previousState	= 0;
	static final int			TESTS			= 5;
	static String[]				methods			= new String[] {
			"testPermissions", "testCatchOwnEvent", "testSyncBundleListener",
			"testListenerOrder"					};

	/**
	 * start. Gets a reference to the TestCaseLink to communicate with the
	 * TestCase.
	 */
	public void start(BundleContext context) {
		_context = context;
		_linkRef = _context.getServiceReference(TestCaseLink.class.getName());
		_link = (TestCaseLink) _context.getService(_linkRef);
		_sr = _context.registerService(LogProxy.class.getName(), this, null);
		start();
	}

	public void stop(BundleContext context) {
		quit();
	}

	/**
	 * This function performs the tests.
	 */
	public void run() {
		int progress = 0;
		try {
			_link.log("Test bundle control started Ok.");
			_tcHome = (String) _link.receive(10000);
			for (int i = 0; _continue && i < methods.length; i++) {
				Method method = getClass().getDeclaredMethod(methods[i],
						new Class[0]);
				method.invoke(this, new Object[0]);
				_link.send("" + 100 * (i + 1) / methods.length);
			}
			log("done", "");
			_link.send("ready");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("done");
	}

	/**
	 * Releases the reference to the TestCaseLink.
	 */
	void quit() {
		if (_continue) {
			_context.ungetService(_linkRef);
			_linkRef = null;
			_continue = false;
		}
	}

	public void log(String test, String result) {
		try {
			if (result == null)
				result = "";
			_link.log(test + " " + result);
		}
		catch (Exception e) {
			System.out.println("Error in link log function");
			e.printStackTrace();
		}
	}

	/**
	 * control that a security exception is thrown if we try to add a
	 * synchronous bundle listener without adminPermission
	 */
	void testPermissions() {
		try {
			Bundle permissionBundle = _context.installBundle(_tcHome
					+ "tb1.jar");
			permissionBundle.start();
			permissionBundle.uninstall();
		}
		catch (Exception e) {
			log("Exception in testPermissions", "FAIL");
			e.printStackTrace();
		}
	}

	/**
	 * Tests if BundleListener can catch their own event and that we can
	 * register bundle multiple times
	 */
	synchronized void testCatchOwnEvent() {
		try {
			//install a bunlde that adds a bundle listener
			//tb2 also adds an extra listener to test
			Bundle listenerBundle = _context.installBundle(_tcHome + "tb2.jar");
			listenerBundle.start();
			//take a bundle through its different states
			bundleLifeCycleNoPrint();
			log(".", "");
			listenerBundle.uninstall();
		}
		catch (Exception e) {
			log("Exception in testCatchOwnEvent", "");
			e.printStackTrace();
		}
	}

	/**
	 * Tests to add a BundleListener.
	 */
	synchronized void testSyncBundleListener() throws Exception {
		try {
			_context.addBundleListener(this);
			bundleLifeCycle();
			_context.removeBundleListener(this);
		}
		catch (Exception e) {
			log("Caught excption in testSyncBundleListener", "FAIL");
			e.printStackTrace();
		}
	}

	/**
	 * Tests that synchronous bundles are called before normalBundleListener. We
	 * can't test this in testSyncBundleListener because different framework
	 * behavior generate different outputs.
	 */
	synchronized void testListenerOrder() throws Exception {
		try {
			// Install another bundle with an asynchronous listener
			Bundle aListenerBundle = _context
					.installBundle(_tcHome + "tb4.jar");
			aListenerBundle.start();
			_context.addBundleListener(this);
			try {
				bundleLifeCycleNoPrint();
			}
			catch (Exception e) {
				log("Exception in testLIstenerOrder", "FAIL");
				e.printStackTrace();
			}
			_context.removeBundleListener(this);
			aListenerBundle.uninstall();
		}
		catch (Exception e) {
			log("Caught excption in testSyncBundleListener", "FAIL");
			e.printStackTrace();
		}
	}

	/**
	 * The BundleEvent callback, used by the BundleListener test.
	 */
	public synchronized void bundleChanged(BundleEvent fe) {
		try {
			if (fe.getBundle() != _context.getBundle()) {
				switch (fe.getType()) {
					case BundleEvent.INSTALLED :
						log("Bundle is being installed.",
								"waiting 3 seconds in SyncBundleListener.");
						wait(3000);
						_link
								.log("SyncBundleListener: INSTALLED has waited 3 seconds.");
						break;
					case BundleEvent.STARTED :
						log("Bundle is being started.",
								"waiting 3 seconds in SyncBundleListener.");
						wait(3000);
						_link
								.log("SyncBundleListener: STARTED has waited 3 seconds.");
						break;
					case BundleEvent.STOPPED :
						log("Bundle is being stopped.",
								"waiting 3 seconds in SyncBundleListener.");
						wait(3000);
						_link
								.log("SyncBundleLiastener: STOPPED has waited 3 seconds.");
						break;
					case BundleEvent.UNINSTALLED :
						log("Bundle is being uninstalled.",
								"waiting 3 seconds in SyncBundleListener.");
						wait(3000);
						_link
								.log("SyncBundleListener: UNINSTALL has waited 3 seconds");
						break;
					case BundleEvent.UPDATED :
						log("Bundle is being updated.",
								"waiting 3 seconds in SyncBundleListener.");
						wait(3000);
						_link
								.log("SyncBundleListener: UPDATED has waited 3 seconds.");
						break;
				}
			}
			else {
				log("The syncBundleListener caught its own event!", "FAIL");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		notify();
	}

	void bundleLifeCycle() {
		try {
			tb = _context.installBundle(_tcHome + "tb3.jar");
			log("returned from Bundle.install()", "");
			wait(2000);
			tb.start();
			log("returned from Bundle.start()", "");
			wait(2000);
			tb.stop();
			log("returned from Bundle.stop()", "");
			wait(2000);
			tb.update();
			log("returned from Bundle.update()", "");
			wait(2000);
			tb.uninstall();
			log("returned from Bundle.uninstall()", "");
			wait(2000);
		}
		catch (Exception e) {
			log("Exception in bundleLifeCycle", "FAIL");
			e.printStackTrace();
		}
	}

	void bundleLifeCycleNoPrint() {
		try {
			tb = _context.installBundle(_tcHome + "tb3.jar");
			wait(2000);
			tb.start();
			wait(2000);
			tb.stop();
			wait(2000);
			tb.update();
			wait(2000);
			tb.uninstall();
			wait(2000);
		}
		catch (Exception e) {
			log("Exception in bunldeLifeCycleNoPrint", "FAIL");
			e.printStackTrace();
		}
	}
}
