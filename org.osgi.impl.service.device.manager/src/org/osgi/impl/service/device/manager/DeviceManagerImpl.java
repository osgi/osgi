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

import org.osgi.framework.*;
import org.osgi.util.tracker.*;
import org.osgi.service.device.*;

import java.io.*;
import java.util.*;

/**
 * Device Manager reference implementation.
 * <p>
 * The Device Manager does not register itself any services with the
 * framework. Instead it reacts to Device and Driver services being registered.
 * </p><p>
 * This particular implementation of a Device Manager has main parts, the
 * <code>DeviceListener</code> and the <code>DriverListener</code>, acting
 * on Device Services and Driver Services accordingly
 *
 * @author $Author$
 * @version $Revision$
 *
 * @see org.osgi.service.device.Device
 * @see org.osgi.service.device.Driver
 * @see DeviceListener
 * @see DriverListener
 */

public class DeviceManagerImpl implements BundleActivator {
    final static String deviceClass       = Device.class.getName();
    final static String driverClass       = Driver.class.getName();
    final static String locatorClass      = DriverLocator.class.getName();
    final static String selectorClass     = DriverSelector.class.getName();
    final static String deviceCategoryKey = org.osgi.service.device.Constants.DEVICE_CATEGORY;
    
    BundleContext bc;
    ServiceTracker driverLocators = null;
    
    Thread deviceSweeper = null;
    Thread driverSweeper = null;
    DeviceListener deviceListener;
    DriverListener driverListener;

    Object queueLock = new Object();

    /**
     * Create a device manager and register listeners for device service events.
     *
     * Start threads to monitor device and driver queues.
     *
     * @param bc The device manager's bundle context
     */
    
    public void start(BundleContext bc) {
	this.bc = bc;
	
	Log.start(bc);
	
	deviceListener = new DeviceListener(bc, this);
	try {
	    bc.addServiceListener(deviceListener,
				  "(|(objectClass="+deviceClass+")"+
				  "("+deviceCategoryKey+"=*))");
	} catch(InvalidSyntaxException e) {
	    Log.error("Failed to register device listener",e);
	}

	deviceSweeper=new Thread (deviceListener);
	deviceSweeper.start();

	driverListener = new DriverListener(bc,this);	    
	driverSweeper=new Thread (driverListener);
	driverSweeper.start();

	try {
	    bc.addServiceListener(driverListener,
				  "(objectClass="+driverClass+")");
	} catch(InvalidSyntaxException e) {
	    Log.error("Failed to register driver listener",e);
	}

	handleIdleDevices();
		
	Log.info("Started");
    }


    /**
     * Create a device manager and register listeners for device service events.
     *
     * Start threads to monitor device and driver queues.
     *
     * @param bc The device manager's bundle context
     */
    public void stop(BundleContext bc) {
	Log.close();
	deviceListener.stop();
	driverListener.stop();
    }

    /**
     * Get a Hashtable with a list of all Drivers currently installed.
     *
     * It will take a list of old drivers, and only add newly found drivers,
     * not present in the old list.</BR>
     * The newly added drivers will be flagged as "transient" as specified
     * by the given flag. - This information is later consulted as to whether 
     * these drivers are candidates for removing.
     *
     * @param oldDrivers The list of known Drivers
     * @param trans whether to flag the newly found drivers as "transient"
     */

    Hashtable getAllDrivers(Hashtable oldDrivers, 
			    boolean trans) {
	return driverListener.getAllDrivers(oldDrivers,trans);
    }


     /**
     * Create a list of currently idle device bundles
     *
     * A "idle" device bundle is a bundle registering a device service,
     * which no other bundle registering a driver service has a dependency on.
     */

    synchronized ServiceReference[] findIdleDevices() {
	ServiceReference[] allDevices = null;
	ServiceReference[] allDrivers = null;
	Vector idleDevices = new Vector();
	Hashtable attachedDevices = new Hashtable();

	// get all Device Services known to the Framework
	try {
	    allDevices = bc.getServiceReferences 
		(null, 
		 "(|(objectClass="+deviceClass+")("+deviceCategoryKey+"=*))");
	} catch (InvalidSyntaxException e) {
	    Log.error ("Filter Exception", e);
	}
	if (allDevices == null) {
	    return null;  // no Devices => noone CAN be idle :)
	}

	// get all Driver Services known to the Framework
	try {
	    allDrivers = bc.getServiceReferences (driverClass,null); 
	} catch (InvalidSyntaxException e) {
	    Log.error ("Filter Exception", e);
	}
	if (allDrivers == null) {
	    return allDevices;  // no Drivers => everyone is idle
	}

	// determine the closure of Services which all Driver Bundles depend on
       	for (int i=0; i<allDrivers.length; i++) {
	    ServiceReference sr[] = allDrivers[i].getBundle().getServicesInUse();
	    if (sr != null) { 
		for (int j=0; j<sr.length; j++) 
		    attachedDevices.put(sr[j],sr[j]);
	    }
	}
	
	// for each Device Bundle check if it is in the closure of Services
	// which any Driver depends upon
 	for (int i=0; i<allDevices.length; i++) {
	    if (!attachedDevices.containsKey(allDevices[i])) {
		Log.debug ("found idle device "+allDevices[i]);
 		idleDevices.add(allDevices [i]);
 	    }
 	}
	
	if (idleDevices.size() == 0)
	    return null;

	return ((ServiceReference[]) (idleDevices.toArray(new ServiceReference[1])));
    }

    /**
     * Take care of idle devices.
     *
     * simulate as if any idle device would have just been detected as a new device
     */

    synchronized void handleIdleDevices() {
	ServiceReference[] idleDevices = findIdleDevices();

	if (idleDevices == null) {
	    Log.debug ("No idle devices");
	    return;
	}

	for (int i=0; i<idleDevices.length; i++) {
	    Log.debug("trying to find driver for idle device:"+idleDevices[i]);
	    deviceListener.deviceInstalled (new DeviceRef (bc,idleDevices[i]));
	}

    }

    
}

