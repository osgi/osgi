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
 * 05/05/2005   Leonardo Barros
 * 38           Implement MEG TCK
 * ===========  ==============================================================
 */
package org.osgi.test.cases.application.tbc.Event;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;
import org.osgi.test.cases.application.tbc.ApplicationTestControl;

public class EventHandlerActivator implements BundleActivator {
	private ApplicationTestControl tbc;

	public EventHandlerActivator(ApplicationTestControl tbc) {
		this.tbc = tbc;
	}

	private ServiceRegistration servReg;

	private EventHandlerImpl testActivatorImpl;
	
	private Hashtable ht;

	public void start(BundleContext bc) throws Exception {
		// creating the service
		testActivatorImpl = new EventHandlerImpl(tbc);
		
		String[] topics = new String[] {
				"org/osgi/framework/ServiceEvent/REGISTERED",
				"org/osgi/framework/ServiceEvent/MODIFIED",
				"org/osgi/framework/ServiceEvent/UNREGISTERING" };	

		ht = new Hashtable();
		ht.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, topics);
		servReg = bc.registerService(EventHandler.class.getName(),
				testActivatorImpl, ht);
	}
	
	public void setProperties(Hashtable props) {
		servReg.setProperties(props);
	}
	
	public void resetProperties() {
		servReg.setProperties(ht);
	}	
	
	public void stop(BundleContext arg0) throws Exception {
		// unregistering the service
		servReg.unregister();
	}

}
