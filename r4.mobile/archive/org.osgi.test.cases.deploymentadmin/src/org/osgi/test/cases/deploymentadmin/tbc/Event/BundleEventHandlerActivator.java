/*
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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
 * 
 */

/*
 * REVISION HISTORY:
 *
 * Date          Author(s)
 * CR            Headline
 * ============  ==============================================================
 * 28/04/2005    Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tbc.Event;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;

/**
 * @author Andre Assad
 * 
 * Activates the BundleEventHandler
 */
public class BundleEventHandlerActivator implements BundleActivator {
	
	private ServiceRegistration servReg;
	private BundleEventHandlerImpl beh;
	
	public BundleEventHandlerActivator(BundleEventHandlerImpl beh) {
		this.beh = beh;
	}

	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		String[] topics = {BundleEventHandlerImpl.TOPIC_INSTALL, BundleEventHandlerImpl.TOPIC_UNINSTALL, BundleEventHandlerImpl.TOPIC_COMPLETE};
		props.put(org.osgi.service.event.EventConstants.EVENT_TOPIC, topics);
		
		servReg = bc.registerService(EventHandler.class.getName(), beh, props);
		System.out.println("BundleEventHandler started...");

	}

	public void stop(BundleContext arg0) throws Exception {
		servReg.unregister();

	}
}
