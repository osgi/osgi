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
 * Default Driver Selector implementation
 *
 * This implements the default algorithm specified. It is used when
 * no other DriverSelector service is found in
 * the framework registry.
 * <p>
 * This object is not intended to be ever registered in the framework
 * registry.
 *
 * @author $Author$
 * @version $Revision$
 *
 */


class DefaultDriverSelector implements DriverSelector {
    
    public int select(ServiceReference device,
		      Match[] matches) {
	int maxmatch=Device.MATCH_NONE;
	int best = DriverSelector.SELECT_NONE;
	long bsid = 0;
	long brank = 0;
	
	for (int i=0; i<matches.length; i++) {
	    int cm   = matches[i].getMatchValue();
	    ServiceReference cd= matches[i].getDriver();
	    
	    long csid = 0;
	    int crank = 0;
	    Object osid = cd.getProperty (org.osgi.framework.Constants.SERVICE_ID);
	    Object osrank = cd.getProperty (org.osgi.framework.Constants.SERVICE_RANKING);
	    
	    // most defensive approach......

	    if (osid==null) 
		Log.warn("Driver "+cd+" does not specify a Service ID!");
	    else
		try {
		    csid = (new Long (osid.toString())).longValue();
		} catch (NumberFormatException e) {
		    Log.error ("Illegal number format in service ID of "+cd,e);
		}
	    
	    if (osrank != null) {
		try {
		    crank = (new Integer (osrank.toString())).intValue();
		} catch (NumberFormatException e) {
		    Log.error ("Illegal number format in service ranking of "+cd,e);
		}
	    }

	    if (cm > maxmatch ||
		(cm == maxmatch &&
		 (crank > brank ||
		  (crank == brank &&
		   csid < bsid)))) {
		maxmatch=cm;
		best = i;
		bsid = csid;
		brank = crank;
	    }
	}

	if (maxmatch != Device.MATCH_NONE)
	    return (best);
	else
	    return (DriverSelector.SELECT_NONE);
	
    }
}
