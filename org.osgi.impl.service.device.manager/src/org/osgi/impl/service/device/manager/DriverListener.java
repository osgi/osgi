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

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.device.*;

/**
 * Driver Listener implementation.
 *
 * This is a listener to Framework device registration events.
 *
 * Registered drivers are put in a queue, where a thread
 * processes one after another and runns a matching
 * with currently idle devices.
 *
 * @see DriverRef
 *
 * @author $Author$
 * @version $Revision$
 *
 */

public class DriverListener implements ServiceListener, Runnable {
    BundleContext bc;
    DeviceManagerImpl dm;
    boolean quit = false;
    Vector driverQueue = new Vector();
    Vector transientDrivers = new Vector();  // contains transient drivers caused by
                                             // temporary driver installs by the DriverLocator
    
   
    /**
     * Create a device manager and listen for device service events.
     *
     * @param bc The device manager's bundle context
     */
    
    DriverListener(BundleContext bc, DeviceManagerImpl dm) {
	this.bc = bc;
	this.dm = dm;
    }
    
    public void stop() {
	quit=true;
	synchronized (driverQueue) {
	    driverQueue.notify();
	}
    }

    /**
     * This is the device service listener entry point.
     *
     * Registered drivers are put in a queue, where a thread
     * processes one after another and tries to find idle devices
     * to attach to them.
     * </P>
     * transient drivers which are installed during a device manager
     * matching ans attaching phase and which are subject to removal
     * (so called "transient" drivers) are ignore and not further
     * processed.
     *
     * @param se The service event
     */
    
    public void serviceChanged(ServiceEvent se) {
	ServiceReference sr=se.getServiceReference();
	switch(se.getType()) {
	case ServiceEvent.REGISTERED:
	    synchronized (driverQueue) {
		if (!transientDrivers.contains(sr.getBundle())) {
		    driverQueue.add(new DriverRef(bc,sr));
		    Log.info(sr+": driver added to queue");
		    driverQueue.notify();
		} else {
		    Log.info(sr+": transient driver - ignored");
		}   
	    }
	    break;
	case ServiceEvent.UNREGISTERING:
	    Log.info(sr+": driver uninstalled");
	    synchronized (driverQueue) {
		DriverRef dr=new DriverRef(bc,sr);
		if (driverQueue.contains(dr))
		    if (driverQueue.remove(dr))
			Log.debug ("Removed "+dr+" from queue");
	    }
	    if (!transientDrivers.contains(sr.getBundle())) {
		dm.handleIdleDevices();   // according to spec 5.2.5 take care of ALL idle devices here.
	    } else {
		Log.info(sr+": transient driver - ignored!");
	    }   
	    break;
	case ServiceEvent.MODIFIED:
	    // Do nothing
	    break;
	}
    }
    
    /**
     * run
     *
     * This is the main
     * loop which takes drivers from the queue and processes them
     */

    public void run() {
	while (!quit) {
	    DriverRef dr = null;

	    synchronized (dm.queueLock) {  // prevent running this algorithm while the 
		                           // DriverLocator installs / uninstalls transient drivers
		synchronized (driverQueue) {
		    if (driverQueue.size() > 0)
			dr=(DriverRef)driverQueue.remove(0);
		    else
			try {
			    driverQueue.wait (1000);		
			} catch (Exception e) {
			    Log.error ("waiting on driver queue",e);
			}
		    }
	    }
	    
	    if (dr!=null) {
		driverInstalled (dr);
	    }
	    
	}
    }
    

    Hashtable getAllDrivers(Hashtable oldDrivers, 
			    boolean trans) {
	ServiceReference[] sra = null;
	Hashtable drivers=new Hashtable ();
	if (oldDrivers != null) 
	    drivers=oldDrivers;
	try {
	    sra=bc.getServiceReferences(dm.driverClass,null);
	} catch(InvalidSyntaxException e) {}
	
	if (sra==null)
	    return drivers;

	for (int i=0; i<sra.length; i++) {
	    String id=(String)sra[i].getProperty
		(org.osgi.service.device.Constants.DRIVER_ID);
	    if (id!=null) {
		if (!drivers.containsKey(id)) 
		    drivers.put(id, new DriverRef(bc,id,sra[i],trans));
	    } else {
		Log.warn("No DRIVER_ID set for driver service "+sra[i]+" from bundle: "+sra[i].getBundle().getLocation()+" --> IGNORING");
	    }
	}

	return drivers;
    }

    /**
     * This method is invoked for every DriverReference taken from the 
     * queue.
     *
     * Here the main work of finding idle devices matching this Driver is done
     *
     * @param driver reference to the driver to process
     */

    void driverInstalled(DriverRef driver) {
	Log.debug ("Driver taken from queue: "+driver);

	synchronized (dm.queueLock) {
	    if (driver.getDriver().getBundle() == null) {
		Log.debug ("Driver already removed");
		return; // driver has already been removed
	    }
	    ServiceReference[] idleDevices = dm.findIdleDevices();
	    if (idleDevices == null)
		return;

	    for (int i=0; i<idleDevices.length; i++) {
		try {
		    if (driver.match(idleDevices[i]) != Device.MATCH_NONE)
			driver.attach();
		} catch (Exception e) {
		    Log.error ("Exception while attaching",e);
		}	
	    }
	}
    }
}

