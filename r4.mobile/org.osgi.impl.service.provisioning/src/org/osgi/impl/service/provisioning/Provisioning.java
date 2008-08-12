package org.osgi.impl.service.provisioning;

import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.provisioning.ProvisioningService;

/**
 * The BundleActivator for the Provisioning bundle.
 * <p>
 * This bundle registers the ProvisioningService. It also watches for changes in
 * the provisioning
 * 
 * @author breed
 */
public class Provisioning extends Thread implements
		org.osgi.framework.BundleActivator {
	ProvisioningServiceImpl	svc;
	ServiceRegistration		reg;
	SimpleWebInterface		swi;
	ProvisioningLog			log	;
	Hashtable				regDict	= new Hashtable();

	/**
	 * @see org.osgi.framework.BundleActivator#start(BundleContext)
	 */
	public void start(BundleContext bc) throws Exception {
		log = new ProvisioningLog(bc);
		ProvisioningDictionary dict = null;
		svc = new ProvisioningServiceImpl(bc, log, regDict);
		dict = (ProvisioningDictionary) svc.getInformation();
		regDict.put(ProvisioningService.PROVISIONING_UPDATE_COUNT, dict
				.get(ProvisioningService.PROVISIONING_UPDATE_COUNT));
		reg = bc
				.registerService(ProvisioningService.class.getName(), svc, null);
		svc.setServiceRegistration(reg);
		swi = new SimpleWebInterface(6111, svc, log);
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(BundleContext)
	 */
	public void stop(BundleContext bc) throws Exception {
		swi.shutdown();
	}
}
