/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */

package org.osgi.test.cases.util;

import org.osgi.framework.*;

/**
   Help class used to convert bundle states to Strings.
*/

public class BundleState
{
	/**
	   Returns the name of the state as a String.
	*/
	public static String stateName(int state)
	{
		String tBax;

		switch(state)
		{
		case Bundle.ACTIVE: tBax = "ACTIVE"; break;
		case Bundle.INSTALLED: tBax = "INSTALLED"; break;
		case Bundle.RESOLVED: tBax = "RESOLVED"; break;
		case Bundle.STARTING: tBax = "STARTING"; break;
		case Bundle.STOPPING: tBax = "STOPPING"; break;
		case Bundle.UNINSTALLED: tBax = "UNINSTALLED"; break;
		default: tBax = "UNKNOWN"; break;
		}

		return tBax;
	}
}
