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
 * May 05, 2005  Andre Assad
 * 76            Implement Test Cases for Deployment Configuration
 * ============  ==============================================================
 */
package org.osgi.test.cases.deploymentadmin.tb2;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingManagedServiceFactory;

/**
 * @author Andre Assad
 * 
 * Activates a ManagedSericeFactory
 *
 */
public class ManagedServiceFactoryImpl implements BundleActivator, TestingManagedServiceFactory {
	
	private ServiceRegistration sr;
	private boolean updated;
	private Dictionary props;
	private String pid;
	private BundleContext bc;

	public void start(BundleContext bc) throws Exception {
		this.bc = bc;
		Dictionary props = new Hashtable();
		props.put(Constants.SERVICE_PID, DeploymentTestControl.PID_MANAGED_SERVICE_FACTORY);
		props.put(ATTRIBUTE_A, ATTRIBUTE_A_VALUE);
		props.put(ATTRIBUTE_B, ATTRIBUTE_B_VALUE);
		props.put(ATTRIBUTE_C, ATTRIBUTE_C_VALUE);
		
		sr = bc.registerService(ManagedServiceFactory.class.getName(), this, props);
		System.out.println("TestingManagedService started");
	}

	public void stop(BundleContext bc) throws Exception {
		sr.unregister();
	}

	public void updated(String pid, Dictionary props) throws ConfigurationException {
		this.pid = pid;
		this.props = props;
		updated = true;
	}

	public void deleted(String arg0) {
		
	}
	
	/**
	 * @return Returns the pid.
	 */
	public String getPid() {
		return pid;
	}
	/**
	 * @return Returns the props.
	 */
	public Dictionary getProperties() {
		return props;
	}
	/**
	 * @return Returns the updated.
	 */
	public boolean isUpdated() {
		return updated;
	}

	public String getName() {
		return this.getClass().getName();
	}
}
