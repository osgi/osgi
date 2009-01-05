package org.osgi.test.cases.device.tbc;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.DriverLocator;
import org.osgi.test.cases.device.tbc.locators.BasicTestLocator;
import org.osgi.test.cases.device.tbc.locators.DefaultSelectionLocator;
import org.osgi.test.cases.device.tbc.locators.DriverLoadingLocator1;
import org.osgi.test.cases.device.tbc.locators.DriverLoadingLocator2;
import org.osgi.test.cases.device.tbc.locators.DriverLoadingLocator3;
import org.osgi.test.cases.device.tbc.locators.EmptyLocator;
import org.osgi.test.cases.device.tbc.locators.RedirectionLocator1;
import org.osgi.test.cases.device.tbc.selectors.DriverLoadingTestSelector1;
import org.osgi.test.cases.device.tbc.selectors.DriverLoadingTestSelector2;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/**
 * The activator of the device access test case
 * 
 * @author ProSyst
 * @version 1.0
 */
public class TestBundleControl extends DefaultTestBundleControl {
	private volatile int		message				= -1;
	/**
	 * Constant for no message in the post box
	 */
	public static final int		MESSAGE_NONE		= -1;
	/**
	 * Constant for OK message in the post box
	 */
	public static final int		MESSAGE_OK			= 0;
	/**
	 * Constant for ERROR message in the post box
	 */
	public static final int		MESSAGE_ERROR		= 1;
	private volatile boolean	noDriverFoundCalled	= false;
	private int					timeout				= 100;
	private ServiceRegistration	tbcReg;

	protected void setUp() {
		tbcReg = getContext().registerService(
				TestBundleControl.class
				.getName(), this, null);
	}
	
	protected void tearDown() {
		tbcReg.unregister();
	}

	public int setMessage(int m) {
		if (this.message != MESSAGE_NONE && m != MESSAGE_NONE) {
			return -1;
		}
		this.message = m;
		return 0;
	}

	private int getMessage() {
		return this.message;
	}
	
	public void setNoDriverFoundCalled(boolean called) {
		this.noDriverFoundCalled = called;
	}

	private boolean noDriverFoundCalled() {
		return this.noDriverFoundCalled;
	}
	
	/*---------------------------------------------------------------------------------------------*/
	/*------------------------- Test methods ------------------------------------------------------*/
	/*---------------------------------------------------------------------------------------------*/
	/**
	 * Tests driver handling when there are no locator services registered
	 */
	public void testStandaloneDriver() {
		Bundle deviceBundle_0 = null;
		Bundle deviceBundle_20 = null;
		Bundle driverBundle_1 = null;
		Bundle driverBundle_7 = null;
		String subtest = "standalone driver test";
		try {
			log(
					subtest,
					"installing and starting device bundle 0 (standart device from the device detection test)");
			deviceBundle_0 = getContext().installBundle(
					getWebServer() + "dev0.jar");
			deviceBundle_0.start();
			log(subtest,
					"installing and starting device bundle 2 (standalone test device)");
			deviceBundle_20 = getContext().installBundle(
					getWebServer() + "dev20.jar");
			deviceBundle_20.start();
			
			
			try {
				Thread.sleep(1000); // Give the RI time to settle (this is
									// BAD!!!!!)
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			
			log(
					subtest,
					"installing driver one - it should attatch to device 2 (standalone test device)");
			driverBundle_7 = getContext().installBundle(
					getWebServer() + "drv7.jar");
			driverBundle_7.start();
			// wait for driver to attach or to be rejected
			waitFor(subtest, "device attachment");
			
			//
			// The next test case sometimes fails because also the
			// previously registered dev20 is matched by drv7 ...
			// Might be a bug in the RI
			//
			log(subtest,
					"installing and starting basic driver bundle - it should attach to device 1");
			driverBundle_1 = getContext().installBundle(
					getWebServer() + "drv1.jar");
			driverBundle_1.start();
			// wait for driver to attach or to be rejected
			waitFor(subtest, "device attachment");
			
			
			log(subtest, "registering a locator service");
			ServiceRegistration reg = getContext()
					.registerService(DriverLocator.class
					.getName(), new EmptyLocator(this), null);
			log(
					subtest,
					"uninstalling driver 7. This should invoke the findDrivers method for the standalone device");
			driverBundle_7.uninstall();
			waitFor(subtest, "findDrivers call");
			log(subtest, "removing locator");
			reg.unregister();
			log(subtest,
					"installing driver 7. It should attach to device 2 again");
			driverBundle_7 = getContext().installBundle(
					getWebServer() + "drv7.jar");
			driverBundle_7.start();
			// wait for driver to attach or to be rejected
			waitFor(subtest, "device attachment");
		}
		catch (BundleException be) {
			be.printStackTrace();
			fail(subtest, be.getMessage());
		}
		finally {
			try {
				if (driverBundle_1 != null)
					driverBundle_1.uninstall();
				if (deviceBundle_0 != null)
					deviceBundle_0.uninstall();
				if (driverBundle_7 != null)
					driverBundle_7.uninstall();
				if (deviceBundle_20 != null)
					deviceBundle_20.uninstall();
			}
			catch (BundleException be) {
				be.printStackTrace();
				fail(subtest, be.getMessage());
			}
		}
		log(subtest, "finished");
	}

	/**
	 * Tests if devices are correctly recognized from the device manager and if
	 * the corresponding drivers are installed (which is the only way to check
	 * if the device manager has detected a device registration))
	 */
	public void testDeviceDetection() {
		Bundle deviceBundle = null;
		String subtest = "basic test";
		log(subtest, "registering services");
		ServiceRegistration locatorSR = getContext().registerService(
				"org.osgi.service.device.DriverLocator", new BasicTestLocator(
						this), null);
		for (int i = 0; i < 5; i++) {
			setNoDriverFoundCalled(false);
			log(subtest, "installing bundle! Test mode = " + i);
			try {
				deviceBundle = getContext().installBundle(
						getWebServer() + "dev" + String.valueOf(i) + ".jar");
				deviceBundle.start();
				// wait for drivers to attach or to be rejected
				// default timeout is 5 seconds. On slower hosts increase the
				// timeout by setting
				// the osgi.test.device.timeout property to a higher value
				// timeout is calculated with the following formula:
				// osgi.test.device.timeout * 100 milliseconds
				// so the default value for osgi.testdevice.timeout is 50.
				// Setting a lower value will decrease the timeout. Setting to
				// zero or negative will have no effect
				waitFor(subtest, "device detection");
				// wait a specific timeout for the noDriverFoundMethod to be
				// called
				if (i == 2) {
					int counter = 0;
					while (!noDriverFoundCalled() && counter++ < 100) {
						try {
							Thread.sleep(timeout);
						}
						catch (InterruptedException ie) {
							ie.printStackTrace();
						}
					}
					if (noDriverFoundCalled())
						log(subtest, "noDriverFound called OK");
					else 
						fail(subtest, "noDriverFound not called");
				}
				else {
					if (noDriverFoundCalled())
						fail(subtest, "noDriverFound called");
				}
			}
			catch (BundleException be) {
				be.printStackTrace();
				fail(subtest, "Error while installing basic device bundle");
			}
			finally {
				if (deviceBundle != null) {
					try {
						deviceBundle.uninstall();
					}
					catch (BundleException be) {
						be.printStackTrace();
						fail(subtest, be.getMessage());
					}
					/*
					 * the device manager is not obliged to remove idle drivers
					 * so I have to remove them manually
					 */
					uninstallDrivers();
				}
			}
		}
		locatorSR.unregister();
	}

	/**
	 * Tests the correct behavior of the device manager against registered
	 * driver locators and driver selectors
	 */
	public void testDriverLoading() {
		String subtest = "driver loading test";
		log(subtest, "registering locators and selector");
		// registering locators
		ServiceRegistration locator1SR = getContext().registerService(
				"org.osgi.service.device.DriverLocator",
				new DriverLoadingLocator1(this), null);
		ServiceRegistration locator2SR = getContext().registerService(
				"org.osgi.service.device.DriverLocator",
				new DriverLoadingLocator2(this), null);
		ServiceRegistration locator3SR = getContext().registerService(
				"org.osgi.service.device.DriverLocator",
				new DriverLoadingLocator3(this), null);
		// registering selector
		ServiceRegistration selector = getContext().registerService(
				"org.osgi.service.device.DriverSelector",
				new DriverLoadingTestSelector1(this), null);
		// registering one selector more - this should be ignored
		ServiceRegistration uselessSelector = getContext().registerService(
				"org.osgi.service.device.DriverSelector",
				new DriverLoadingTestSelector2(this), null);
		// install the bundle representing the device
		Bundle deviceBundle = null;
		try {
			log(subtest, "installing device bundle");
			deviceBundle = getContext().installBundle(
					getWebServer() + "dev100.jar");
			deviceBundle.start();
			waitFor(subtest, "device attachment");
		}
		catch (BundleException be) {
			be.printStackTrace();
			fail(subtest, "Error while installing the device bundle");
		}
		finally {
			if (deviceBundle != null) {
				try {
					deviceBundle.uninstall();
				}
				catch (BundleException be) {
					be.printStackTrace();
					fail(subtest, be.getMessage());
				}
			}
			locator1SR.unregister();
			locator2SR.unregister();
			locator3SR.unregister();
			selector.unregister();
			uselessSelector.unregister();
			/*
			 * the device manager is not obliged to remove idle drivers so I
			 * have to remove them manually
			 */
			uninstallDrivers();
		}
	}

	/**
	 * Tests the default selection algorithm
	 */
	public void testDefaultSelection() {
		String subtest = "default selection test";
		ServiceRegistration locatorSR = getContext().registerService(
				"org.osgi.service.device.DriverLocator",
				new DefaultSelectionLocator(this), null);
		Bundle deviceBundle = null;
		try {
			log(subtest, "installing device bundle");
			deviceBundle = getContext().installBundle(
					getWebServer() + "dev100.jar");
			deviceBundle.start();
			waitFor(subtest, "device attachment");
		}
		catch (BundleException be) {
			be.printStackTrace();
			fail(subtest, "Error while installing the device bundle");
		}
		finally {
			if (deviceBundle != null) {
				try {
					deviceBundle.uninstall();
				}
				catch (BundleException be) {
					be.printStackTrace();
					fail(subtest, be.getMessage());
				}
			}
			locatorSR.unregister();
			/*
			 * the device manager is not obliged to remove idle drivers so I
			 * have to remove them manually
			 */
			uninstallDrivers();
		}
	}

	/**
	 * Tests rediretion
	 */
	public void testRedirection() {
		String subtest = "redirection test";
		ServiceRegistration locator1SR = getContext().registerService(
				"org.osgi.service.device.DriverLocator",
				new RedirectionLocator1(this), null);
		Bundle deviceBundle = null;
		try {
			log(subtest, "installing device bundle");
			deviceBundle = getContext().installBundle(
					getWebServer() + "dev100.jar");
			deviceBundle.start();
			waitFor(subtest, "device attachment");
			/*
			 * Subtest removed - as stated in the spec the device manager must
			 * call all locator until one finds a driver. The calling order is
			 * not specified so I can't be sure that the dummy locator will be
			 * called.
			 */
			// REMOVED
			// // FIX - as locators may be called in a different thread I have
			// to
			//      // set some kind of timeout before checking the flag
			//      for (int i = 0; (i < 1000) && !RedirectionLocator2.called; i++) {
			//      	try {
			//        	Thread.sleep(5); // 5 seconds should be enough for everything
			//      	} catch (InterruptedException ie) {
			//        	ie.printStackTrace(); // nothing much more to do about this
			//      	}
			//      }
			//
			//      if (RedirectionLocator2.called) {
			//      	log("redirection test", "second locator called OK");
			//      } else {
			//      	log("redirection test", "second locator not called! Error!");
			//      }
			// REMOVED
		}
		catch (BundleException be) {
			be.printStackTrace();
			fail(subtest, "Error while installing the device bundle");
		}
		finally {
			if (deviceBundle != null) {
				try {
					deviceBundle.uninstall();
				}
				catch (BundleException be) {
					be.printStackTrace();
					fail(subtest, be.getMessage());
				}
			}
			locator1SR.unregister();
			/*
			 * the device manager is not obliged to remove idle drivers so I
			 * have to remove them manually
			 */
			uninstallDrivers();
		}
	}

	/*---------------------------------------------------------------------------------------------*/
	/*-------------------------------- utility methods --------------------------------------------*/
	/*---------------------------------------------------------------------------------------------*/
	private void uninstallDrivers() {
		Bundle[] bundles = getContext().getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle b = bundles[i];
			try {
				if (b.getSymbolicName().startsWith(
						"org.osgi.test.cases.device.drv")) {
					log("uninstalling " + b);
					b.uninstall();
				}
			}
			catch (BundleException be) {
				be.printStackTrace();
				fail(be.getMessage());
			}
		}
	}

	private void waitFor(String subtest, String message) {
		int counter = 0;
		int m = TestBundleControl.MESSAGE_NONE;
		while (((m = getMessage()) == TestBundleControl.MESSAGE_NONE)
				&& counter++ < 1000) {
			try {
				Thread.sleep(timeout);
			}
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		switch (m) {
			case TestBundleControl.MESSAGE_OK :
				log(subtest, message + " OK");
				break;
			case TestBundleControl.MESSAGE_NONE :
				fail(subtest, message + " timed out!");
				break;
			case TestBundleControl.MESSAGE_ERROR :
				fail(subtest, "error message received do " + message);
				break;
			default :
				fail(subtest, "unkown message received");
		}
		setMessage(TestBundleControl.MESSAGE_NONE);
	}

	public void log(String subtest, String toLog) {
		log("[" + subtest + "] " + toLog);
	}
	
	public void fail(String subtest, String toLog) {
		fail("[" + subtest + "] " + toLog);
	}
}