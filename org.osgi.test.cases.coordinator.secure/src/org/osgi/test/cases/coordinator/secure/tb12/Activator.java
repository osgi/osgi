/*
 * Copyright (c) OSGi Alliance (2011). All Rights Reserved.
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

package org.osgi.test.cases.coordinator.secure.tb12;

import java.util.Collection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.osgi.test.cases.coordinator.secure.TestClassResult;
import org.osgi.test.cases.coordinator.secure.TestClassResultImpl;

/**
 * Gets coordinations with permission only for coordinations whose name starts
 * with com.ibm.*.
 */
public class Activator implements BundleActivator {
	private ServiceRegistration<TestClassResult>	resultsRegistration;
	
	public void start(BundleContext bc) throws Exception {
		ServiceReference<Coordinator> sr = bc
				.getServiceReference(Coordinator.class);
		Coordinator c = bc.getService(sr);
		boolean result = false;
		Collection<ServiceReference<Coordination>> srs = bc
				.getServiceReferences(Coordination.class, null);
		if (srs.size() == 2) {
			for (ServiceReference<Coordination> csr : srs) {
				Coordination co = bc.getService(csr);
				bc.ungetService(csr);
				if (co.getName().startsWith("com.ibm")) {
					result = c.getCoordination(co.getId()) != null;
				}
				else if (co.getName().startsWith("com.acme")) {
					result = c.getCoordination(co.getId()) == null;
				}
				else {
					result = false;
					break;
				}
			}
		}
		resultsRegistration = bc.registerService(TestClassResult.class,
				new TestClassResultImpl(result), null);
		bc.ungetService(sr);
	}

	public void stop(BundleContext bc) throws Exception {
		resultsRegistration.unregister();
	}
}
