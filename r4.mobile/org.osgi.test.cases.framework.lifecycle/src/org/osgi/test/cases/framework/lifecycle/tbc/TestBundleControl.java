/**
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.test.cases.framework.lifecycle.tbc;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.security.AllPermission;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.permissionadmin.*;
import org.osgi.test.cases.framework.lifecycle.servicereferencegetter.*;
import org.osgi.test.cases.logproxy.*;
import org.osgi.test.cases.util.*;
import org.osgi.test.service.*;

/**
 * This is the bundle initially installed and started by the TestCase when
 * started. It performs the various tests and reports back to the TestCase.
 * 
 * 
 * The testPermissions test is currently removed because of unknown state. It
 * uses tb10 but this seems to be removed from the upper makefile.
 * 
 * @author Ericsson Radio Systems AB
 */
public class TestBundleControl extends Thread implements LogProxy,
		BundleListener, ServiceListener {
	PermissionAdmin				permissionAdmin	= null;
	private ServiceReference	serviceRef;
	ServiceRegistration			srLogProxy;
	BundleContext				_context;
	BundleContext				_otherContext;
	ServiceRegistration			_sr;
	ServiceReference			_linkRef;
	TestCaseLink				_link;
	String						_tcHome;
	boolean						_continue		= true;
	boolean						_called;
	Vector						_eventQueue		= new Vector(16);
	static String[]				methods			= new String[] {
			"testNormalInstallStartStopUninstall",
			"testStartStopWithException", "testUnavailableURL",
			"testBrokenStream", "testEvents", "testServiceRegistrations",
			"testNormalUpdate", "testRollbackUpdate",
			"testStoppedBundleContext",
			//  			"testHangInStop", // Not used, behaviour is not specified in OSGi
			// 1.0.
			"testGetLocation", "testPermissions"};

	/**
	 * Constructor. Gets a reference to the TestCaseLink to communicate with the
	 * TestCase.
	 */
	TestBundleControl(BundleContext context) {
		_context = context;
		_linkRef = _context.getServiceReference(TestCaseLink.class.getName());
		_link = (TestCaseLink) _context.getService(_linkRef);
	}

	/**
	 * This function performs the tests.
	 */
	public void run() {
		int progress = 0;
		_sr = _context.registerService(TestBundleControl.class.getName(), this,
				null);
		srLogProxy = _context.registerService(LogProxy.class.getName(), this,
				null);
		try {
			_link.log("Test bundle control started Ok.");
			_tcHome = (String) _link.receive(10000);
			setPermissions();
			for (int i = 0; _continue && i < methods.length; i++) {
				Method method = getClass().getDeclaredMethod(methods[i],
						new Class[0]);
				method.invoke(this, new Object[0]);
				_link.send("" + 100 * (i + 1) / methods.length);
			}
			_link.send("ready");
		}
		catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			if ( targetException != null ) {
				Throwable nested = null;
				if (targetException instanceof BundleException)
					nested = ((BundleException) targetException)
							.getNestedException();
				if ( nested != null )
					nested.printStackTrace();
				else
					targetException.printStackTrace();
					
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Releases the reference to the TestCaseLink.
	 */
	void quit() {
		resetPermissions();
		if (_continue) {
			_context.ungetService(_linkRef);
			_linkRef = null;
			_continue = false;
		}
	}

	public void log(String test, String result) {
		try {
			_link.log(test + " " + result);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests normal Install/Uninstall/start/stop of a bundle.
	 */
	void testNormalInstallStartStopUninstall() throws Exception {
		Bundle tb;
		String res;
		tb = _context.installBundle(_tcHome + "tb1.jar");
		if (tb.getState() == Bundle.INSTALLED
				|| tb.getState() == Bundle.RESOLVED)
			res = "TestBundle 1 installed, state Ok.";
		else
			res = "TestBundle 1 installed but incorrect state " + tb.getState();
		log("Testing normal install:", res);
		tb.start();
		if (tb.getState() == Bundle.ACTIVE)
			res = "TestBundle 1 started, state ACTIVE.";
		else
			res = "TestBundle 1 started but incorrect state " + tb.getState();
		log("Testing normal start:", res);
		tb.stop();
		if (tb.getState() == Bundle.RESOLVED)
			res = "TestBundle 1 stopped, state RESOLVED.";
		else
			res = "TestBundle 1 stopped but incorrect state " + tb.getState();
		log("Testing normal stop:", res);
		tb.uninstall();
		if (tb.getState() == Bundle.UNINSTALLED)
			res = "TestBundle 1 uninstalled, state UNINSTALLED.";
		else
			res = "TestBundle 1 uninstalled but incorrect state "
					+ tb.getState();
		log("Testing normal uninstall:", res);
	}

	/**
	 * Tests Start/Stop with exception.
	 */
	void testStartStopWithException() throws Exception {
		Bundle tb2a, tb2b;
		tb2a = _context.installBundle(_tcHome + "tb2a.jar");
		log("Testing start with exception:", "Installed Ok.");
		try {
			tb2a.start();
			log("Testing start with exception:", "No exception thrown, Error!");
		}
		catch (BundleException be) {
			if (tb2a.getState() == Bundle.RESOLVED)
				log("Testing start with exception:",
						"Exception thrown, state is RESOLVED.");
			else
				log("Testing start with exception:",
						"Exception thrown, incorrect state, " + tb2a.getState());
		}
		tb2a.uninstall();
		tb2a = null;
		tb2b = _context.installBundle(_tcHome + "tb2b.jar");
		log("Testing stop with exception:", "Installed Ok.");
		tb2b.start();
		log("Testing stop with exception:", "Started Ok.");
		try {
			tb2b.stop();
			log("Testing stop with exception:", "No exception thrown, Error!");
		}
		catch (BundleException be) {
			if (tb2b.getState() == Bundle.RESOLVED)
				log("Testing stop with exception:",
						"Exception thrown, state is RESOLVED.");
			else
				log("Testing stop with exception:",
						"Exception thrown, incorrect state, " + tb2b.getState());
		}
		tb2b.uninstall();
		log("Testing stop with exception:", "Uninstalled Ok.");
		tb2b = null;
	}

	/**
	 * Tests unavailable URL.
	 */
	void testUnavailableURL() throws Exception {
		Bundle tb3;
		String res;
		try {
			tb3 = _context.installBundle(_tcHome + "NoSuchBundle.jar");
			res = "No exception thrown, Error!";
		}
		catch (BundleException be) {
			res = "Exception thrown, Ok.";
		}
		log("Testing unavailable URL:", res);
	}

	/**
	 * Tests halfway broken inputstream during install.
	 */
	void testBrokenStream() throws Exception {
		Bundle tb4;
		String res = null;
		byte b[] = new byte[500];
		try {
			URL url = new URL(_tcHome + "tb4.jar");
			InputStream in = url.openStream();
			// Read 500 bytes of the file
			for (int i = 0; i < 500; i += in.read(b, i, 500 - i));
			// Now, make an InputStream from those first 500 bytes.
			ByteArrayInputStream bin = new ByteArrayInputStream(b);
			try {
				tb4 = _context.installBundle(_tcHome + "tb4.jar", bin);
				res = "No exception thrown, Error!";
			}
			catch (BundleException be) {
				res = "Exception thrown, Ok.";
			}
		}
		catch (IOException ioe) {
			System.err.println("Error reading test bundle 4 jar file.");
			ioe.printStackTrace();
		}
		log("Testing broken inputstream:", res);
	}

	/**
	 * Used by the Event test below to receive bundle events.
	 */
	public synchronized void bundleChanged(BundleEvent be) {
		// TODO should really test for RESOLVED/UNRESOLVED.  Filtering for now until ...
		if ((be.getType() & (BundleEvent.RESOLVED | BundleEvent.UNRESOLVED)) != 0)
			return;
		System.out.println("In " + _eventQueue.size() + " event : " + event(be.getType()));
		_eventQueue.add(be);
		notify();
	}

	/**
	 * Used by the Event test below to receive service events.
	 */
	public void serviceChanged(ServiceEvent se) {
		String service;
		String event;
		service = (String) se.getServiceReference().getProperty("Service-Name");
		switch (se.getType()) {
			case ServiceEvent.REGISTERED :
				event = "REGISTERED";
				break;
			case ServiceEvent.MODIFIED :
				event = "MODIFIED";
				break;
			case ServiceEvent.UNREGISTERING :
				event = "UNREGISTERING";
				break;
			default :
				event = "UNKNOWN EVENT";
				break;
		}
		log("Testing ServiceEvents:", "Service " + service + " is " + event
				+ ".");
	}

	/**
	 * Tests various events.
	 */
	void testEvents() throws Exception {
		Bundle tb;
		String res;
		_context.addBundleListener(this);
		_context.addServiceListener(this);
		tb = _context.installBundle(_tcHome + "tb5.jar");
		syncBundle("Get install event", BundleEvent.INSTALLED);
		tb.start();
		syncBundle("Get started event", BundleEvent.STARTED);
		tb.stop();
		syncBundle("Get stopped event", BundleEvent.STOPPED);
		tb.uninstall();
		syncBundle("Get uninstalled event", BundleEvent.UNINSTALLED);
		_context.removeBundleListener(this);
		_context.removeServiceListener(this);
		ServiceListener sl = new ServiceListener() {
			public void serviceChanged(ServiceEvent se) {
				_called = true;
			}
		};
		_called = false;
		_context.addServiceListener(sl,
				"(objectClass=org.osgi.test.cases.framework.lifecycle.tb5.EventTest)");
		tb = _context.installBundle(_tcHome + "tb5.jar");
		tb.start();
		tb.stop();
		tb.uninstall();
		_context.removeServiceListener(sl);
		if (_called)
			res = "ServiceListener called, Ok.";
		else
			res = "ServiceListener not called, Error!";
		log("Checking addServiceListener() with a matching filter:", res);
		_called = false;
		_context.addServiceListener(sl, "(objectClass=not)");
		tb = _context.installBundle(_tcHome + "tb5.jar");
		tb.start();
		tb.stop();
		tb.uninstall();
		_context.removeServiceListener(sl);
		if (_called)
			res = "ServiceListener called, Error!";
		else
			res = "ServiceListener not called, Ok.";
		log("Checking addServiceListener() with a non matching filter:", res);
	}

	/**
	 * Tests service registrations.
	 */
	void testServiceRegistrations() throws Exception {
		String res;
		String clazzes[];
		ServiceRegistration sr;
		ServiceReference refs[];
		Hashtable props;
		boolean found;
		clazzes = new String[] {TestBundleControl.class.getName(),
				Thread.class.getName(), ServiceListener.class.getName()};
		props = new Hashtable();
		props.put("PropertyX", "TBC");
		sr = _context.registerService(clazzes, this, props);
		refs = _context.getServiceReferences(Thread.class.getName(), null);
		found = false;
		for (int i = 0; i < refs.length; i++)
			if (refs[i].getProperty("PropertyX").equals("TBC"))
				found = true;
		if (found)
			res = "Service registered, Ok.";
		else
			res = "ServiceListener not registered ok, Error!";
		log("Testing multiple name service registration:", res);
		sr.unregister();
		clazzes = new String[] {BundleActivator.class.getName()};
		try {
			sr = _context.registerService(clazzes, this, props);
			res = "No exception thrown, Error!";
		}
		catch (IllegalArgumentException iae) {
			res = "Exception thrown, Ok.";
		}
		log("Testing to register a service not instanceof all classes:", res);
	}

	/**
	 * Tests normal update.
	 */
	void testNormalUpdate() throws Exception {
		Bundle tb;
		String res;
		long Id;
		_context.addBundleListener(this);
		tb = _context.installBundle(_tcHome + "tb6a.jar");
		syncBundle("Test update, get installed ", BundleEvent.INSTALLED);
		tb.start();
		syncBundle("Test update, get started ", BundleEvent.STARTED);
		Id = tb.getBundleId();
		URL url = new URL(_tcHome + "tb6b.jar");
		InputStream in = url.openStream();
		tb.update(in);
		syncBundle("Test update expect stopped before update",
				BundleEvent.STOPPED);
		syncBundle("Test update, now get updated ", BundleEvent.UPDATED);
		syncBundle("Test update, start again", BundleEvent.STARTED);
		if (Id == tb.getBundleId())
			res = "BundleId same, Ok.";
		else
			res = "BundleId differs, Error!";
		log("Checking BundleId after update:", res);
		tb.stop();
		syncBundle("Test update get stopped", BundleEvent.STOPPED);
		tb.uninstall();
		syncBundle("Test update, get uninstalled ", BundleEvent.UNINSTALLED);
		_context.removeBundleListener(this);
	}

	/**
	 * Tests failed update with rollback.
	 */
	void testRollbackUpdate() throws Exception {
		Bundle tb;
		String res;
		tb = _context.installBundle(_tcHome + "tb7.jar");
		tb.start();
		try {
			tb.update();
			res = "No exception thrown, Error!";
		}
		catch (BundleException be) {
			res = "Exception thrown, Ok.";
		}
		log("Testing failed rollback:", res);
		if (tb.getState() == Bundle.ACTIVE)
			res = "State is ACTIVE, Ok.";
		else
			res = "State is " + BundleState.stateName(tb.getState())
					+ ", Error!";
		log("Checking state after rollback:", res);
		tb.uninstall();
	}

	/**
	 * Used by the StoppedBC test below.
	 */
	public void setBundleContext(BundleContext bc) {
		_otherContext = bc;
	}

	/**
	 * Tries to use a stopped BundleContext.
	 */
	void testStoppedBundleContext() throws Exception {
		Bundle tb;
		ServiceReference ref;
		String res;
		tb = _context.installBundle(_tcHome + "tb8.jar");
		tb.start();
		tb.stop();
		// By now, this bc should be illegal to use.
		BundleContext otherContext = _otherContext;
		try {
			ref = otherContext
					.getServiceReference(TestCaseLink.class.getName());
			if (ref.equals(_linkRef))
				res = "BundleContext is still usable, ERROR!";
			else
				res = "BundleContext is almost usable, ERROR!";
		}
		catch (IllegalStateException ise) {
			tb.start();
			// The context should not become reusable after we restart the
			// bundle!
			try {
				ref = otherContext.getServiceReference(TestCaseLink.class
						.getName());
				if (ref.equals(_linkRef))
					res = "BundleContext became reusable, ERROR!";
				else
					res = "BundleContext became almost reusable, ERROR!";
			}
			catch (IllegalStateException ise2) {
				res = "BundleContext was not usable, Ok.";
			}
			tb.stop();
		}
		log("Testing to use a stopped BundleContext:", res);
		tb.uninstall();
	}

	/**
	 * Tests to hang in stop.
	 */
	void testHangInStop() throws Exception {
		Bundle tb;
		_link.log("Testing to hang in stop.");
		tb = _context.installBundle(_tcHome + "tb9.jar");
		tb.start();
		_link.log("Trying to stop. ");
		tb.stop();
		_link.log("Stopped. ");
		tb.uninstall();
	}

	/**
	 * Tests that getLocation returns the correct location.
	 */
	void testGetLocation() throws Exception {
		Bundle tb;
		String res;
		// Use the same bundle as the "hang in stop" test, we won't start (or
		// stop) it anyway.
		String loc = _tcHome + "tb9.jar";
		tb = _context.installBundle(loc);
		if (tb.getLocation().equals(loc))
			res = "Location is the same, Ok.";
		else
			res = "Location differs, Error!";
		log("Testing getLocation:", res);
		tb.uninstall();
	}

	/**
	 * Test the permissions in the framework
	 */
	synchronized void testPermissions() throws Exception {
		Bundle tb;
		Bundle tbPerm;
		tb = _context.installBundle(_tcHome + "tb5.jar");
		tbPerm = _context.installBundle(_tcHome + "tb10.jar");
		tb.start();
		tbPerm.start();
		serviceRef = _context.getServiceReference(ServiceReferenceGetter.class
				.getName());
		if (serviceRef != null) {
			ServiceReferenceGetter serviceReferenceGetter = (ServiceReferenceGetter) _context
					.getService(serviceRef);
			serviceReferenceGetter.setServiceReference(_context
					.getServiceReference(PermissionAdmin.class.getName()));
		}
		wait(5000);
		tbPerm.uninstall();
		if ((tb.getState() & Bundle.UNINSTALLED) == 0) {
			tb.uninstall();
		}
	}

	synchronized void syncBundle(String test, int event) throws IOException {
		try {
			if (_eventQueue.size() == 0) {
				System.out.println("Waiting for event");
				wait(10000);
			}
			if (_eventQueue.size() == 0)
				log(test, "Time out on event receive " + event(event));
			else {
				BundleEvent be = (BundleEvent) _eventQueue.remove(0);
				if (be.getType() == event)
					log(test, " correct event " + event(event) + " Ok");
				else
					log(test, " invalid event " + event(be.getType())
							+ ", expected " + event(event));
			}
		}
		catch (InterruptedException x) {/* cannot happen */
		}
	}

	String event(int event) {
		switch (event) {
			case 0 :
				return "(NONE)";
			case BundleEvent.INSTALLED :
				return "INSTALLED(" + BundleEvent.INSTALLED + ")";
			case BundleEvent.STARTED :
				return "STARTED(" + BundleEvent.STARTED + ")";
			case BundleEvent.UPDATED :
				return "UPDATED(" + BundleEvent.UPDATED + ")";
			case BundleEvent.STOPPED :
				return "STOPPED(" + BundleEvent.STOPPED + ")";
			case BundleEvent.UNINSTALLED :
				return "UNINSTALLED(" + BundleEvent.UNINSTALLED + ")";
			case BundleEvent.RESOLVED:
				return "RESOLVED(" + BundleEvent.RESOLVED + ")";
			case BundleEvent.UNRESOLVED:
				return "UNRESOLVED(" + BundleEvent.UNRESOLVED + ")";
			case BundleEvent.STARTING:
				return "STARTING(" + BundleEvent.STARTING + ")";
			case BundleEvent.STOPPING:
				return "STOPPING(" + BundleEvent.STOPPING + ")";
			default :
				return "UNKNOWN(" + event + ")";
		}
	}

	void setPermissions() {
		//get the permission admin service
		serviceRef = _context.getServiceReference(PermissionAdmin.class
				.getName());
		if (serviceRef != null)
			permissionAdmin = (PermissionAdmin) _context.getService(serviceRef);
		if (permissionAdmin != null) {
			//set the required permissions for the testcontrol and target
			// bundle
//			PermissionInfo[] permInfoArrayLocation = {
//					new PermissionInfo(AdminPermission.class.getName(), "*",
//							"*"),
//					new PermissionInfo(ServicePermission.class.getName(), "*",
//							"get,register"),
//					new PermissionInfo(PackagePermission.class.getName(), "*",
//							"EXPORT,IMPORT"),
//					new PermissionInfo(SocketPermission.class.getName(), "*",
//							"accept,connect,listen,resolve")};
			PermissionInfo[] permInfoArrayLocation = {
					new PermissionInfo(AllPermission.class.getName(), null, null)
			};
			permissionAdmin
					.setPermissions((_context.getBundle()).getLocation(),
							permInfoArrayLocation);
			permissionAdmin.setPermissions(_tcHome + "tb10.jar",
					permInfoArrayLocation);
			//set permissions for all bundle 5&8
			PermissionInfo[] permInfoArrayService = {
					new PermissionInfo(ServicePermission.class.getName(),
							"org.*", "get,register"),
					new PermissionInfo(PackagePermission.class.getName(), "*",
							"EXPORT,IMPORT")};
			permissionAdmin.setPermissions(_tcHome + "tb5.jar",
					permInfoArrayService);
			permissionAdmin.setPermissions(_tcHome + "tb8.jar",
					permInfoArrayService);
			//set permissions for tb10
			PermissionInfo[] permInfoArrayTB10 = {
					new PermissionInfo(ServicePermission.class.getName(),
							"org.osgi.test.*", "get"),
					new PermissionInfo(ServicePermission.class.getName(),
							"org.osgi.framework.*", "get,register"),
					new PermissionInfo(
							ServicePermission.class.getName(),
							"org.osgi.test.cases.framework.lifecycle.servicereferencegetter.*",
							"get,register"),
					new PermissionInfo(PackagePermission.class.getName(), "*",
							"EXPORT,IMPORT")};
			permissionAdmin.setPermissions(_tcHome + "tb10.jar",
					permInfoArrayTB10);
			/*
			 * //set defaultpermissions for all bundles PermissionInfo[]
			 * permInfoArrayDefault ={ new
			 * PermissionInfo(PackagePermission.class.getName(),"*","EXPORT,IMPORT")};
			 * permissionAdmin.setDefaultPermissions(permInfoArrayDefault);
			 */
		}
	}

	void resetPermissions() {
		if (permissionAdmin != null) {
			/* permissionAdmin.setDefaultPermissions(null); */
			permissionAdmin.setPermissions(_tcHome + "tb5.jar", null);
			permissionAdmin.setPermissions(_tcHome + "tb8.jar", null);
			permissionAdmin.setPermissions(_tcHome + "tb10.jar", null);
			permissionAdmin.setPermissions(
					(_context.getBundle()).getLocation(), null);
		}
	}
}
