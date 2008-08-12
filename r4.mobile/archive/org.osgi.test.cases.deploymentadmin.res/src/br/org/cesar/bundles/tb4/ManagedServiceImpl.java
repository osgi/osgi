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
package br.org.cesar.bundles.tb4;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingManagedService;


/**
 * @author Andre Assad
 * 
 * Activates a ManagedSericeFactory
 *
 */
public class ManagedServiceImpl implements BundleActivator, TestingManagedService {
	
	private ServiceRegistration sr;
	private Dictionary props;
	private boolean updated; 

	public synchronized void start(BundleContext bc) throws Exception {
		props = new Hashtable();
		props.put(Constants.SERVICE_PID, DeploymentConstants.PID_MANAGED_SERVICE);
		
		sr = bc.registerService(ManagedService.class.getName(), this, props);
		System.out.println("TestingManagedService started");
	}

	public void stop(BundleContext bc) throws Exception {
		sr.unregister();
	}

	public synchronized void updated(Dictionary props) throws ConfigurationException {
		this.props = props;
		updated = true;
	}

	public Dictionary getProperties() {
		return props;
	}

	public boolean isUpdated() {
		return updated;
	}

}
