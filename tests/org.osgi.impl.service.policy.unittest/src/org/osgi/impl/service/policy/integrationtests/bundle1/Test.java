/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy.integrationtests.bundle1;

import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

public class Test  {
	
	/**
	 * call a method. The sole purpose is that the run() method in the action will
	 * be run with this (eg. Test) class in the call stack. So permission checks will
	 * be done with this class's rights.
	 * @param action
	 * @return
	 */
	public static Object doAction(PrivilegedAction action) {
		return action.run();
	}

	/**
	 * call a method. The sole purpose is that the run() method in the action will
	 * be run with this (eg. Test) class in the call stack. So permission checks will
	 * be done with this class's rights.
	 * @param action
	 * @return
	 * @throws Exception
	 */
	public static Object doAction(PrivilegedExceptionAction action) throws Exception {
		return action.run();
	}

}
