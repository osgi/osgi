/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.test.cases.cu.tb5;

import org.osgi.service.cu.admin.HierarchyListener;

/**
 * Listener of Control Unit hierarchy changes registered with a filter that includes
 * all possible properties.
 * This listener will recieve events that match the defined filter.
 * 
 * @version $Revision$
 */
public class EventFilterListener implements HierarchyListener {

	/**
	 * @param eventType
	 * @param cuType
	 * @param cuID
	 * @param parentType
	 * @param parentID
	 * @see org.osgi.service.cu.admin.HierarchyListener#hierarchyChanged(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void hierarchyChanged(int eventType, String cuType, String cuID, String parentType, String parentID) {
		Activator.log("<EventFilterListener> EventType=" + eventType + ", CUType=" + cuType + ", CUId=" + cuID + ", parentType=" + parentType + ", parentId=" + parentID);
	}	
}
