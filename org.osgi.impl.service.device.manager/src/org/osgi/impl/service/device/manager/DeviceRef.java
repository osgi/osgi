/**
 * Copyright (c) 1999, 2001 Gatespace AB. All Rights Reserved.
 * 
 * Gatespace grants Open Services Gateway Initiative (OSGi) an irrevocable,
 * perpetual, non-exclusive, worldwide, paid-up right and license to
 * reproduce, display, perform, prepare and have prepared derivative works
 * based upon and distribute and sublicense this material and derivative
 * works thereof as set out in the OSGi MEMBER AGREEMENT as of January 24
 * 2000, for use in accordance with Section 2.2 of the BY-LAWS of the
 * OSGi MEMBER AGREEMENT.
 */

package org.osgi.impl.service.device.manager;

import org.osgi.framework.*;
import org.osgi.service.device.*;

import java.util.*;

/**
 * Helper class for wrapping a device service.
 *
 * @author $Author$
 * @version $Revision$
 *
 */

public class DeviceRef {
        
    BundleContext bc;

    /**
     * Driver service reference. May be null only if dr is null.
     */
    ServiceReference sr=null;
    
    /**
     * If true matching has already been done.
     */
    
    boolean matchDone;
    
    /**
     * Recorded match value
     */
    int match;
    
    /**
     * Construct a Device Reference
     *
     * @param sr Service reference to the driver
     */

    DeviceRef(BundleContext bc,
	      ServiceReference sr) {
	this.sr=sr;
	this.bc=bc;
    }

    ServiceReference getServiceReference() {
	return sr;
    }

    Dictionary getProperties() {
	String[] keys=sr.getPropertyKeys();
	Dictionary props=new Hashtable(keys.length);
	if(keys!=null) {
	    for(int i=0;i<keys.length;i++) {
		String key=keys[i];
		Object val=sr.getProperty(key);
		if (val != null)
		    props.put(key,val);
	    }
	}
	return props;	
    }

    public String toString() {
	return ("Reference to Device Service: "+sr+"(DeviceID="+sr.getProperty("deviceID")+")");
    }
}
