/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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

import java.util.Hashtable;

import org.osgi.framework.*;
import org.osgi.service.cu.*;
import org.osgi.service.cu.admin.*;
import org.osgi.test.service.TestLogger;

/**
 * Bundle that registers a HierarchyListener with a filter and a HierarchyListener without
 * filter
 *
 * @version $Revision$
 */
public class Activator implements BundleActivator {
	private ServiceRegistration regList1;
	private ServiceRegistration regList2;
	private ServiceReference logRef;
	private static TestLogger logger;

	public void start(BundleContext context) throws Exception {
        logRef = context.getServiceReference(TestLogger.class.getName());
        if (logRef != null)
            logger = (TestLogger) context.getService(logRef);
        
        // Register a HierarchyListener without filter
		Hashtable p = new Hashtable();
		p.put(ControlUnitConstants.EVENT_SYNC, "");
		regList1 = context.registerService(HierarchyListener.class.getName(), new NoFilterListener(), p);

		// Register a HierarchyListener with a filter containing all possible properties
		p.clear();
		p.put(ControlUnitConstants.EVENT_SYNC, "");
		p.put(ControlUnitConstants.EVENT_FILTER, "(&(org.osgi.control.event.type=1)(org.osgi.control.id=hip.position.id)(org.osgi.control.type=hip.position)(org.osgi.control.parent.type=hip)(org.osgi.control.parent.id=hip.id))");
		regList2 = context.registerService(HierarchyListener.class.getName(), new EventFilterListener(), p);
	}
	
	public void stop(BundleContext context) throws Exception {
		if (regList1 != null)
			regList1.unregister();
		if (regList2 != null)
			regList2.unregister();
		if (logRef != null)
			context.ungetService(logRef);
	}
	
	public static void log(String msg) {
		if (logger != null)
			logger.log(msg);
	}
}
