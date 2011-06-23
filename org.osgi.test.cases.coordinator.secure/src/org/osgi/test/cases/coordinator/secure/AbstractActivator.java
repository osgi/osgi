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
package org.osgi.test.cases.coordinator.secure;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;

/**
 * Performs common operations required by tests.
 */
public abstract class AbstractActivator implements BundleActivator {
	/**
	 * The coordination, if any, registered by the test.
	 */
	protected Coordination coordination;
	/**
	 * The Coordinator service.
	 */
	protected Coordinator coordinator;
	
	private ServiceReference<Coordination>			coordinationRef;
	private ServiceReference<Coordinator>			coordinatorRef;
	private ServiceRegistration<TestClassResult>	resultsRegistration;
	
	public void start(BundleContext bc) throws Exception {
		coordinationRef = bc.getServiceReference(Coordination.class);
		if (coordinationRef != null)
			coordination = bc.getService(coordinationRef);
		coordinatorRef = bc.getServiceReference(Coordinator.class);
		coordinator = bc.getService(coordinatorRef);
		SecurityException result = null;
		try {
			doStart();
		}
		catch (SecurityException e) {
			result = e;
		}
		boolean succeeded = hasPermission() ? result == null : result != null;
		resultsRegistration = bc.registerService(TestClassResult.class,
				new TestClassResultImpl(succeeded), null);
	}

	public void stop(BundleContext bc) throws Exception {
		resultsRegistration.unregister();
		bc.ungetService(coordinatorRef);
		if (coordinationRef != null)
			bc.ungetService(coordinationRef);
	}
	
	/**
	 * Subclasses use this to execute the single test that may or may not result
	 * in a SecurityException depending on permissions.
	 * @throws Exception
	 */
	protected abstract void doStart() throws Exception;
	
	/**
	 * Indicates whether or not the test should have permission and a SecurityException should not have occurred.
	 * @return true if the test should have permission and a SecurityException should not have occurred, false otherwise.
	 */
	protected abstract boolean hasPermission();
}
