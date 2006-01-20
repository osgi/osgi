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
 * Apr 25, 2005  Andre Assad
 * 26            Implement MEG TCK for the deployment RFC-88
 * ============  ==============================================================
 * Jul 14, 2005  Andre Assad
 * 145           Implement spec review issues
 * ============  ==============================================================
 */

package br.org.cesar.bundles.dmo.rp4;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.deploymentadmin.spi.ResourceProcessorException;
import org.osgi.test.cases.deploymentadmin.mo.tbc.DeploymentmoConstants;


/**
 * @author Luiz Felipe Guimaraes
 * 
 * A testing resource processor to test deployment packages
 * installation (deployment management object). It throws an exception at dropped method.
 */
public class ResourceProcessorImpl implements BundleActivator,ResourceProcessor {

	private ServiceRegistration sr;
 
	
	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentmoConstants.PID_RESOURCE_PROCESSOR4);
		sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		System.out.println("ResourceProcessor started.");

	}

	public void stop(BundleContext bc) throws Exception {
		sr.unregister();

	}

	public void begin(DeploymentSession session) {


	}

	public void process(String arg0, InputStream arg1) throws ResourceProcessorException  {
		if (DeploymentmoConstants.RP4_SIMULATE_EXCEPTION_ON_PROCESS) {
			throw new ResourceProcessorException(ResourceProcessorException.CODE_RESOURCE_SHARING_VIOLATION);
		}
	}

	public void dropped(String arg0)  {

	}

	public void dropAllResources()  {
	}

	public void prepare()  {
		
	}

	public void commit() {
		
	}

	public void rollback() {

	}

	public void cancel() {

	}

}
