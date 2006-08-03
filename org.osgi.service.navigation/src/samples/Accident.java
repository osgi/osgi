/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2006). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package samples;

import org.osgi.service.navigation.*;
import org.osgi.service.navigation.control.NavigationService;

public class Accident {
	NavigationService nav;
	
	/**
	 * There has been an accident, find out if we run into this
	 * accident or not. If we do, plan around all segments that lead 
	 * @param where
	 */
	void accident(Coordinate where) {
		Location loc = nav.getLocation(where);
		Route route = nav.getCurrentRoute();
		if ( route == null )
			return;
		
		// like to use filtering, but I guess that is hard to
		// make it work because we have no id on the location.
		
		RouteSegment rss[] = route.getRouteSegments(-1, "(location.id="+/*loc.getId()+*/")");
		for ( int i=0; i<rss.length; i++ ) {
			// route.exclude(rss[i]);
		}
		
		// ### Iterating over the segments stinks! We need a count or iterator. 
		// ### maybe the nextRoutSegment should return an int?
		int n =0;
		while(true) {
			RouteSegment rs = route.getRouteSegment(n++);
			if ( rs == null) 
				continue;
			
			if ( rs.getLocation().equals(loc) ) {
				// route.exclude(rs);
			}
		}
	}
	
}
