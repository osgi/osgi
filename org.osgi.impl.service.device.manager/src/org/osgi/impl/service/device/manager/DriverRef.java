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
 * Helper class for communicating with a driver.
 *
 * @author $Author$
 * @version $Revision$
 *
 */

public class DriverRef implements Match {
    final static String driverClass = "org.osgi.service.device.Driver";
    
    String id;
    
    /**
     * Driver bundle. If non-null, the bundle should be
     * stopped and uninstalled at cleanup.
     */
    Bundle bundle;
    
    /**
     * Driver service reference. May be null only if dr is null.
     */
    ServiceReference sr;
    
    
    /**
     * If true matching has already been done.
     */
    
    ServiceReference matchedAgainst;
    
    /**
     * Recorded match value
     */
    int match;

    /**
     * If driver bundle is only transiently installed
     */

    boolean trans;
    
    BundleContext bc;
    
    /**
     * Construct a DriverRef to an already running driver.
     *
     * @param deviceSR Service reference to the device
     * @param id Driver id
     * @param sr Service reference to the driver
     */

    DriverRef(BundleContext bc,
	      String id, 
	      ServiceReference driverService,
	      boolean trans) {
	this.bc=bc;
	this.sr=driverService;
	this.id=id;
	matchedAgainst=null;
	this.trans=trans;
    }

    DriverRef(BundleContext bc, 
	      ServiceReference driverService) {
	this(bc,null,driverService,false);

	id=(String)driverService.getProperty("DRIVER_ID");
	if (id==null) {
	    Log.warn ("Driver "+driverService+" has no valid ID!!");
	    id=driverService.toString();
	}
	
     }
    
    /**
     * Compute the match value of the driver. Subsequent matches
     * return the same value or <code>Device.MATCH_NONE</code>
     * if the driver is disabled.
     *
     * @param deviceSR Service reference to the device
     *
     * @return Match value
     */
    
    synchronized int match(ServiceReference device) {
	if(matchedAgainst == device) return match;
	matchedAgainst = null;
	match = Device.MATCH_NONE;
	Driver dr=(Driver)bc.getService(sr);
	if(dr!=null) {
	    try {
		match=dr.match(device);
		matchedAgainst=device;
	    } catch(Exception e) {
		Log.error(id+": match failed",e);
	    } finally {
		dr=null;
		bc.ungetService(sr);
	    }
	}
	return match;
    }
    
    /**
     * Attach to the device (return null), or refer to another driver. If
     * the attach fails to complete an exception is thrown. Regardless of
     * the outcome, the DriverRef is disabled and will not participate in
     * future matches. This is achieved by setting its match value to
     * <code>MATCH_NONE</code>
     *
     * @return Referred driver ID or null
     * @throws Exception
     */
    
    String attach() throws Exception {
	String ref=null;
	Driver dr=(Driver)bc.getService(sr);
	if(dr!=null) {
	    try {
		ref=dr.attach(matchedAgainst);
		if(ref!=null)
		    match=Device.MATCH_NONE;
	    } catch (Exception e) {
		Log.error ("Exception while attaching",e);
		match=Device.MATCH_NONE;
	    } finally {
		bc.ungetService(sr);
	    }
	}
	return ref;
    }
    
    /*
     * Two Drivers are considered equal, when their ID's are equal
     */

    public boolean equals (DriverRef d) {
	return id.equals(d.id);
    }

    public ServiceReference getDriver() {
	return sr;
    }

    public int getMatchValue() {
	return match;
    }

    public boolean isTransient() {
	return trans;
    }

    public void setTransient (boolean trans) {
	this.trans=trans;
    }

}
