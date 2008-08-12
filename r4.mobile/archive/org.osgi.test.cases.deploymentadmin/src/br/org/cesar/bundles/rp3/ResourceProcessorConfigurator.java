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
 * 09/05/2005    Andre Assad
 * 76            Implement Test Cases for Deployment Configuration
 * ============  ==============================================================
 */

package br.org.cesar.bundles.rp3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentSession;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingResourceProcessorConfigurator;

/**
 * @author Andre Assad
 *
 */

public class ResourceProcessorConfigurator implements BundleActivator, TestingResourceProcessorConfigurator {

	private ServiceRegistration sr;
	private ConfigurationAdmin ca;
	private Dictionary props;
	private Configuration configManagedService;
	private Configuration configManagedServiceFactory;
	private MetaTypeInformation mti;

	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		props.put(Constants.SERVICE_PID, DeploymentTestControl.PID_RESOURCE_PROCESSOR3);
		sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		System.out.println("Resource Processor Configurator started.");
		
		ca = (ConfigurationAdmin)bc.getService(bc.getServiceReference(ConfigurationAdmin.class.getName()));

		MetaTypeService mts = (MetaTypeService)bc.getService(bc.getServiceReference(MetaTypeService.class.getName()));
		mti = mts.getMetaTypeInformation(bc.getBundle());
	}

	public void stop(BundleContext bc) throws Exception {
		sr.unregister();
		
	}

	public void begin(DeploymentSession session) {
		try {
			configManagedService = ca.getConfiguration(DeploymentTestControl.PID_MANAGED_SERVICE);
			configManagedServiceFactory = ca.createFactoryConfiguration(DeploymentTestControl.PID_MANAGED_SERVICE_FACTORY);
		} catch (IOException e) {
			
		}
	}

	/**
	 * Extracts Objects Class Definitions and Attributes from 
	 */
	private Dictionary extractMetaData() {
		Dictionary props = null;
		String [] pids = mti.getPids();
		String [] factoryPids = mti.getFactoryPids();

		for (int i=0; i<pids.length; i++) {
		     ObjectClassDefinition ocd = mti.getObjectClassDefinition(pids[i], null);
		     AttributeDefinition[] ADs = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
		     for (int j=0; j<ADs.length; j++) {
		     	String[] defaultValues = ADs[j].getDefaultValue();
		     	switch (ADs[j].getType()) {
		     	case AttributeDefinition.BOOLEAN: {
		     		Boolean[] attr = new Boolean[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Boolean(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.BYTE: {
		     		Byte[] attr = new Byte[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Byte(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.CHARACTER: {
		     		Character[] attr = new Character[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Character(defaultValues[q].charAt(0));
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.DOUBLE: {
		     		Double[] attr = new Double[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Double(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.FLOAT: {
		     		Float[] attr = new Float[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Float(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.INTEGER: {
		     		Integer[] attr = new Integer[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Integer(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.LONG: {
		     		Long[] attr = new Long[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Long(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.SHORT: {
		     		Short[] attr = new Short[ADs[j].getCardinality()];
		     		for(int q=0; q<attr.length; q++) {
		     			attr[q] = new Short(defaultValues[q]);
		     		}
		     		props.put(ocd.getName(), attr);
		     		break;
		     	}
		     	case AttributeDefinition.STRING: {
		     		props.put(ocd.getName(), defaultValues);
		     		break;
		     	}
		     	}
		     }
		}

		return props;
	}

	public void process(String str, InputStream is) throws DeploymentException {
		props = extractMetaData();
		
	}

	public void dropped(String arg0) throws DeploymentException {
		
	}

	public void dropAllResources() throws DeploymentException {
		
	}

	public void prepare() throws DeploymentException {
		
	}

	public void commit() {
		try {
			configManagedService.update(props);
			configManagedServiceFactory.update(props);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void rollback() {
		
	}

	public void cancel() {
		
	}

	/**
	 * @return Returns the props.
	 */
	public Dictionary getProperties() {
		return props;
	}
}
