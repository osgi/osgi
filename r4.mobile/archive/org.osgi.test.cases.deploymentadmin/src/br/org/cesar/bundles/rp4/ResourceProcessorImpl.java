/*
 * Created on 10/06/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.org.cesar.bundles.rp4;

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
import org.osgi.test.cases.deploymentadmin.tbc.DeploymentTestControl;
import org.osgi.test.cases.deploymentadmin.tbc.TestingSessionResourceProcessor;

/**
 * @author aea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResourceProcessorImpl implements BundleActivator, TestingSessionResourceProcessor {

	private ServiceRegistration sr;
	private DeploymentSession session;

	public void start(BundleContext bc) throws Exception {
		Dictionary props = new Hashtable();
		props.put("service.pid", DeploymentTestControl.PID_RESOURCE_PROCESSOR4);
		sr = bc.registerService(ResourceProcessor.class.getName(), this, props);
		System.out.println("Resource Processor started.");
		
	}

	public void stop(BundleContext context) throws Exception {
		sr.unregister();
		
	}

	public File getDataFile(Bundle bundle) {
		return (bundle!=null)?session.getDataFile(bundle):null;
	}

	public DeploymentPackage getTargetDeploymentPackage() {
		return session.getTargetDeploymentPackage();
	}

	public DeploymentPackage getSourceDeploymentPackage() {
		return session.getSourceDeploymentPackage();
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
