/**
 * Copyright (c) 1999, 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants the OSGi Alliance an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.device.manager;

import java.io.InputStream;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.device.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Device Listener implementation.
 * 
 * This is a listener to Framework device registration events.
 * 
 * Registered devices are put in a queue, where a thread processes one after
 * another and tries to find a matching driver.
 * 
 * @see DeviceRef
 * 
 * @author $Author$
 * @version $Revision$
 *  
 */
public class DeviceListener implements ServiceListener, Runnable {
	BundleContext		bc;
	DeviceManagerImpl	dm;
	boolean				quit			= false;
	Vector				deviceQueue		= new Vector();
	ServiceTracker		driverLocators	= null;
	ServiceTracker		driverSelector	= null;

	/**
	 * Create a device manager and listen for device service events.
	 * 
	 * @param bc The device manager's bundle context
	 * @param dm A reference to the device manager
	 */
	DeviceListener(BundleContext bc, DeviceManagerImpl dm) {
		this.bc = bc;
		this.dm = dm;
		driverLocators = new ServiceTracker(bc, DeviceManagerImpl.locatorClass,
				null);
		driverLocators.open();
		driverSelector = new ServiceTracker(bc,
				DeviceManagerImpl.selectorClass, null);
		driverSelector.open();
	}

	/**
	 * set a flag to indicate the sweeper thread to stop
	 */
	public void stop() {
		quit = true;
		synchronized (deviceQueue) {
			deviceQueue.notify();
		}
	}

	/**
	 * This is the device service listener entry point.
	 * 
	 * Any newly registered Device Service is added to a queue for processing.
	 * Finding, downloading and activation a Driver is done asyncronously to
	 * prevent excessive blocking of the event notification Thread.
	 * 
	 * @param se The service event
	 */
	public void serviceChanged(ServiceEvent se) {
		ServiceReference sr = se.getServiceReference();
		switch (se.getType()) {
			case ServiceEvent.REGISTERED :
				synchronized (deviceQueue) {
					deviceQueue.add(new DeviceRef(bc, sr));
					Log.info(sr + ": device added to queue (deviceID="
							+ sr.getProperty("deviceID") + ")");
					deviceQueue.notify();
				}
				break;
			case ServiceEvent.UNREGISTERING :
				Log.info(sr + ": device uninstalled");
				// Not Implemented in the reference implementation
				// When devices are unregistered drivers may become idle.
				// This should trigger some form garbage collection, to
				// prevent unused drivers from accumulating over time.
				break;
			case ServiceEvent.MODIFIED :
				// Do nothing
				break;
		}
	}

	/**
	 * run
	 * 
	 * This is the main loop which takes devices from the queue and processes
	 * them
	 */
	public void run() {
		while (!quit) {
			DeviceRef dr = null;
			synchronized (deviceQueue) {
				if (deviceQueue.size() > 0)
					dr = (DeviceRef) deviceQueue.remove(0);
				else
					try {
						deviceQueue.wait(5000);
					}
					catch (Exception e) {
						Log.error("waiting on device queue", e);
					}
			}
			if (dr != null) {
				deviceInstalled(dr);
			}
		}
	}

	/**
	 * Query installed driver locators, install driver bundles
	 * 
	 * @param instDrivers currently installed drivers
	 * @param dev the device for which a driver is to be found
	 */
	void doLocators(Hashtable instDrivers, DeviceRef dev) {
		Object[] locators = driverLocators.getServices();
		Dictionary searchProps = dev.getProperties();
		if (locators == null) {
			Log.info("No DriverLocator service installed");
			return;
		}
		for (int i = 0; i < locators.length; i++) {
			DriverLocator locator = (DriverLocator) locators[i];
			String[] drv = null;
			Log.debug("Query driverlocator " + locator);
			try {
				drv = locator.findDrivers(searchProps);
			}
			catch (Exception e) {
				Log.error("Exception from driver locator " + locator, e);
			}
			if (drv != null) {
				for (int j = 0; j < drv.length; j++) {
					String id = drv[j];
					if (instDrivers.get(id) == null) { // not yet known driver
						Log.debug("loading driver " + drv[j] + " from locator "
								+ locator);
						try {
							InputStream driverIS = locator.loadDriver(id);
							if (driverIS != null) {
								Bundle b = bc.installBundle(id, driverIS);
								dm.driverListener.transientDrivers.add(b);
								b.start();
							}
							else
								Log
										.warn("Driver Locator failed to provide InputStream for driver "
												+ id);
						}
						catch (Exception e) {
							Log.error("Couldnt load driver " + id, e);
						}
					}
				}
			}
		}
	}

	void loadReferredDriver(String ref) {
		Object[] locators = driverLocators.getServices();
		if (locators == null) {
			Log.warn("DriverLocator service gone");
			return;
		}
		Log.info("Loading referred driver: " + ref);
		for (int i = 0; i < locators.length; i++) {
			DriverLocator locator = (DriverLocator) locators[i];
			try {
				InputStream driverIS = locator.loadDriver(ref);
				if (driverIS != null) {
					Bundle b = bc.installBundle(ref, driverIS);
					b.start();
					break;
				}
			}
			catch (Exception e) {
				Log.error("Couldn't load driver " + ref, e);
			}
		}
	}

	void removeTransientDrivers(Hashtable allDrivers) {
		Enumeration d = allDrivers.elements();
		Log.info("uninstalling transient drivers");
		while (d.hasMoreElements()) {
			DriverRef dr = (DriverRef) d.nextElement();
			try {
				if (dr.isTransient()) {
					Bundle b = dr.getDriver().getBundle();
					b.uninstall();
					dm.driverListener.transientDrivers.remove(b);
				}
			}
			catch (Exception e) {
				Log.error("Couldn't uninstall driver " + dr.getDriver(), e);
			}
		}
	}

	/**
	 * This method is invoked for every DeviceReference taken from the queue, or
	 * when a device became idle. <br>
	 * Here the main work of finding, matching and attaching Drivers is done.
	 * 
	 * @param deviceRef a reference to the device to process
	 */
	void deviceInstalled(DeviceRef deviceRef) {
		boolean attached = false;
		Log.debug("handling newly discovered device: " + deviceRef);
		synchronized (dm.queueLock) {
			// get all currently installed drivers, mark as non transient
			Hashtable allDrivers = dm.getAllDrivers(null, false);
			doLocators(allDrivers, deviceRef);
			// get all newly installed drivers, mark as transient
			dm.getAllDrivers(allDrivers, true);
			Log.debug("running bidding algorithm");
			String referred = null;
			do {
				DriverRef[] drivers = (DriverRef[]) allDrivers.values()
						.toArray(new DriverRef[0]);
				for (int i = 0; i < drivers.length; i++)
					drivers[i].match(deviceRef.getServiceReference());
				DriverSelector ds = (DriverSelector) driverSelector
						.getService();
				if (ds == null)
					ds = new DefaultDriverSelector();
				int chosen = DriverSelector.SELECT_NONE;
				try {
					chosen = ds
							.select(deviceRef.getServiceReference(), drivers);
				}
				catch (Exception e) {
					Log.error("Selector threw Exception", e);
				}
				ds = null;
				Log.debug("Driver chosen: " + chosen);
				if (chosen != DriverSelector.SELECT_NONE)
					try {
						referred = null;
						referred = drivers[chosen].attach();
						if (referred == null) {
							attached = true;
							drivers[chosen].setTransient(false);
						}
					}
					catch (Exception e) {
						Log.error("Couldnt attach driver", e);
					}
				else
					break;
				if (referred != null) {
					loadReferredDriver(referred);
					// get all newly installed drivers, mark as transient
					dm.getAllDrivers(allDrivers, true);
				}
			} while (referred != null);
			// Call the device's noDriverFound method
			if (!attached) {
				ServiceReference sr = deviceRef.getServiceReference();
				Object obj = bc.getService(sr);
				try {
					// implementing the Device interface is optional
					if (Device.class.isInstance(obj)) {
						Device dev = (Device) obj;
						dev.noDriverFound();
					}
				}
				finally {
					bc.ungetService(sr);
				}
			}
			removeTransientDrivers(allDrivers);
		}
	}
}
