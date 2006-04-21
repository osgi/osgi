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

import java.util.List;

import org.osgi.service.navigation.Location;
import org.osgi.service.navigation.control.*;

public class Visitor {
	NavigationService nav;
	NavigationSession session;
	
	List		visits;
	
	public void patientVisitor(Patient patients[]){
		if ( session != null ) 
			session.destroy();
		
		Location locations[] = new Location[patients.length];
		for ( int i=0; i<locations.length; i++ ) {
			locations[i] = calcAddress(patients[i]);
		}
		RoutePlan	plan = new RoutePlan(RoutePlan.SHORTEST, patients);
		session = nav.navigate(plan);
		session.resume();
	}
	
	
	private Location calcAddress(Patient patient) {
		// TODO Auto-generated method stub
		return null;
	}


	public void setNavigationService(NavigationService ns) {
		nav = ns;
	}
	
}

interface Patient extends Location {
	
}