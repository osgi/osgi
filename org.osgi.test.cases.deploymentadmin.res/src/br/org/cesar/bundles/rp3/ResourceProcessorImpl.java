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
 * ===========   ==============================================================
 * Jun 10, 2005  Andre Assad
 * 26            Implement MEGTCK for the deployment RFC-88 
 * ===========   ==============================================================
 */
package br.org.cesar.bundles.rp3;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentSession;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.DeploymentConstants;
import org.osgi.test.cases.deploymentadmin.tc2.tbc.util.TestingSessionResourceProcessor;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andre Assad
 *
 */
public class ResourceProcessorImpl implements BundleActivator, TestingSessionResourceProcessor {

	private ServiceRegistration sr;
	private ServiceTracker tracker;
	private DeploymentSession session;

	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentConstants.PID_RESOURCE_PROCESSOR3);
		sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		
		tracker = new ServiceTracker(bc, sr.getReference(), null);
		tracker.open();
		System.out.println("Resource Processor started.");
		
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		
	}

	/**
	 * @return Returns private data area of the bundle.
	 */
	public File getDataFile(Bundle bundle) {
		return (bundle!=null)?session.getDataFile(bundle):null;
	}

	/**
	 * @return Returns the session target deployment package.
	 */
	public DeploymentPackage getTargetDeploymentPackage() {
		return session.getTargetDeploymentPackage();
	}

	/**
	 * @return Returns the session source deployment package.
	 */
	public DeploymentPackage getSourceDeploymentPackage() {
		return session.getSourceDeploymentPackage();
	}
	
	/**
	 * @return Returns the tracker.
	 */
	public ServiceTracker getTracker() {
		return tracker;
	}

	public void begin(DeploymentSession session) {
		this.session = session;
		
	}

	public void process(String name, InputStream stream) throws DeploymentException {
		
	}

	public void dropped(String resource) throws DeploymentException {
		
	}

	public void dropAllResources() throws DeploymentException {
		
	}

	public void prepare() throws DeploymentException {
		
	}

	public void commit() {
		
	}

	public void rollback() {
		
	}

	public void cancel() {
		
	}
}
