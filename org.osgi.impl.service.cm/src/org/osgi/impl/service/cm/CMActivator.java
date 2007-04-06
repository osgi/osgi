/*
 * $Id$
 *
 * Copyright (c) OSGi Alliance (2000, 2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi (OSGI)
 * Specification may be subject to third party intellectual property rights,
 * including without limitation, patent rights (such a third party may or may
 * not be a member of OSGi). OSGi is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL NOT
 * INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY LOSS OF
 * PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF BUSINESS,
 * OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL, PUNITIVE OR
 * CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS DOCUMENT OR THE
 * INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cm;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.Bundle;

/**
 * BundleActivator class - it is addressed by the framework, when the CM bundle
 * needs to be started or stopped.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class CMActivator implements BundleActivator, ServiceFactory {
	/**
	 * Utility reference to Log class, necessary for communication with Log
	 * Service.
	 */
	protected Log					log;
	/**
	 * Private variables follow.
	 */
	private BundleContext			bc;
	private ConfigurationStorage	storage;
	private ServiceAgent			sa;
	static ServiceRegistration		cmReg;
	private CMEventManager			eventMgr;

	/**
	 * The method is invoked when the bundle should be started.
	 * 
	 * All players in Config Management are created and started, this including:
	 * CM Storage, CMEventManager, ServiceAgent.
	 * 
	 * @param bc context of this bundle, allowing it to access the framework.
	 * @exception BundleException If storage fails to load, or any unexpected
	 *            exception occurs.
	 */
	public void start(BundleContext bc) throws BundleException {
		this.bc = bc;
		log = new Log(bc);
		try {
			storage = new ConfigurationStorage(bc);
			storage.load();
			eventMgr = new CMEventManager(bc);
			eventMgr.start();
			sa = new ServiceAgent(bc, storage);
			cmReg = bc.registerService(ConfigurationAdmin.class.getName(),
					this, null);
		}
		catch (Exception e) {
			String msg = "[CM]Error while trying to start CM Activator!";
			Log.log(1, msg, e);
			throw new BundleException(msg, e);
		}
	}

	/**
	 * Method is called by the framework, when the bundle should be stopped, so
	 * naturally, the resources got by CM, here are released: storage is closed,
	 * listeners removed, event manager informed to stop, CM service
	 * unregistered.
	 * 
	 * @param bc object, relating this bundle with framework.
	 * @exception BundleException In case any exception is thrown
	 */
	public void stop(BundleContext bc) throws BundleException {
		try {
			eventMgr.stopIt();
			cmReg.unregister();
			cmReg = null;
			storage.close();
			sa.close();
			log.close();
		}
		catch (Exception e) {
			String msg = "[CM]Error while trying to stop CM Activator!";
			Log.log(1, msg, e);
			throw new BundleException(msg, e);
		}
	}

	/**
	 * Returns a ConfigurationAdminImpl object, created for each requesting
	 * bundle.
	 * 
	 * @param callerBundle bundle, requested to get ConfigurationAdmin service.
	 * @param sReg ServiceRegistration of the ConfigurationAdmin service
	 * @return ConfigurationAdminImpl object
	 */
	public Object getService(Bundle callerBundle, ServiceRegistration sReg) {
		return new ConfigurationAdminImpl(bc, callerBundle.getLocation());
	}

	/**
	 * Does nothing, as the CM bundle does not keep references to
	 * ConfigurationAdminImpl objects.
	 * 
	 * @param callerBundle bundle requesting to unget ConfigurationAdmin
	 *        service.
	 * @param sReg ServiceRegistration of ConfigurationAdmin.
	 * @param obj Service object, already been got by this bundle.
	 */
	public void ungetService(Bundle callerBundle, ServiceRegistration sReg,
			Object obj) {
	}
}