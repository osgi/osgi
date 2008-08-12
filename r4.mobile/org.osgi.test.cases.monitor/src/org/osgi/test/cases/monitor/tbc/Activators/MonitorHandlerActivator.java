/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

/*
 * REVISION HISTORY:
 *
 * Date         Author(s)
 * CR           Headline
 * ===========  ==============================================================
 * 08/03/2005   Alexandre Santos
 * 14           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.monitor.tbc.Activators;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.monitor.tbc.MonitorTestControl;

/**
 * @author Alexandre Santos
 *
 */
public class MonitorHandlerActivator implements BundleActivator {

	private MonitorTestControl tbc;
	
	public MonitorHandlerActivator(MonitorTestControl tbc) {
		this.tbc = tbc;
	}	
	
	private ServiceRegistration servReg;
	
	private MonitorHandlerImpl testMonitorHandlerImpl;

	public void start(BundleContext bc) throws Exception {
		// creating the service
		testMonitorHandlerImpl = new MonitorHandlerImpl(tbc);
			
		String[] topics = new String[] {"org/osgi/service/monitor/MonitorEvent"};
		Hashtable ht = new Hashtable();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, topics);
		//ht.put(org.osgi.service.event.EventConstants.EVENT_FILTER, "("+MonitorTestControl.CONST_LISTENER_ID+"="+MonitorTestControl.INITIATOR+")");
		servReg = bc.registerService(EventHandler.class.getName(), testMonitorHandlerImpl, ht);
	}

	public void stop(BundleContext arg0) throws Exception {
		// unregistering the service
		servReg.unregister();
	}

}
