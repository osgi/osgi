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

import java.io.IOException;
import java.security.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.cm.*;

/**
 * The class acts as an agent between CM and framework registry. It listens for
 * service events from ManagedServices, ManagedServiceFactories. When they
 * appear/disappear in the registry, Configuration objects are simultaneously
 * informed.
 * 
 * @author OSGi Alliance
 * @version $Revision$
 */
public class ServiceAgent implements ServiceListener, BundleListener {
	/* Reference to storage of Configuration data */
	protected static ConfigurationStorage	storage;
	protected static BundleContext			bc;

	/**
	 * Inits variables, checks for already available ManagedServices, and
	 * ManagedServiceFactories. Those which have corresponding Configuration
	 * objects are associated to them.
	 * 
	 * @param bc BundleContext of this bundle.
	 * @param storage containing Configuration data.
	 */
	public ServiceAgent(BundleContext bc, ConfigurationStorage storage) {
		ServiceAgent.bc = bc;
		ServiceAgent.storage = storage;
		try {
			informManagedServices(true);
			informManagedServices(false);
			bc.addServiceListener(this, "(|(" + Constants.OBJECTCLASS + "="
					+ ManagedService.class.getName() + ")("
					+ Constants.OBJECTCLASS + "="
					+ ManagedServiceFactory.class.getName() + "))");
		}
		catch (InvalidSyntaxException ise) {
			/* Do nothing - syntax is valid */
		}
		bc.addBundleListener(this);
	}

	/**
	 * When a bundle is uninstalled, the configurations that are bound to must
	 * be either unbound or deleted.
	 */
	public void bundleChanged(BundleEvent bEv) {
		if (bEv.getType() == BundleEvent.UNINSTALLED) {
			final Bundle b = bEv.getBundle();
			String location = (String) AccessController
					.doPrivileged(new PrivilegedAction() {
						public Object run() {
							return b.getLocation();
						}
					});
			synchronized (ServiceAgent.storage) {
				Enumeration e = ConfigurationStorage.configs.elements();
				ConfigurationImpl c;
				while (e.hasMoreElements()) {
					c = (ConfigurationImpl) e.nextElement();
					if (location.equals(c.location)) {
						try {
							if (c.stronglyBound) {
								ServiceAgent.storage.deleteConfig(c);
							}
							else {
								c.setLocation(null, true);
							}
						}
						catch (IOException ioe) {
							Log.log(1,
									"[CM]Error while deleting or unbinding a configuration with pid "
											+ c.pid + ".", ioe);
						}
					}
				}
			}
		}
	}

	/**
	 * Invoked by the framework, when a service state changes. When a
	 * ManagedService or ManagedServiceFactory registers, available
	 * Configuration objects are inspected for pid match - if the corresponding
	 * Configuration is found - it is assigned its ManagedService or
	 * ManagedServiceFactory. When a ManagedService(Factory) unregisters - the
	 * corresponding Configuration is informed.
	 * 
	 * @param sEv holds ServiceEvent data.
	 */
	public void serviceChanged(ServiceEvent sEv) {
		if (sEv.getType() == ServiceEvent.REGISTERED) {
			ServiceReference sRef = sEv.getServiceReference();
			String[] oClasses = (String[]) sRef
					.getProperty(Constants.OBJECTCLASS);
			for (int i = 0; i < oClasses.length; i++) {
				if (oClasses[i].equals(ManagedService.class.getName())) {
					processService(sRef, true);
				}
				else
					if (oClasses[i].equals(ManagedServiceFactory.class
							.getName())) {
						processService(sRef, false);
					}
			}
		}
	}

	/**
	 * Closes the ServiceAgent by removing ServiceListener.
	 */
	protected void close() {
		bc.removeServiceListener(this);
	}

	/**
	 * Searches for a ManagedService(Factory), which has the same service.pid as
	 * the configuration's pid(factory pid). If the configuration already has
	 * been bound to a bundle location, then the ManagedService(Factory) must be
	 * registered by a bundle with the same location. Otherwise - if the
	 * configuration has not been bound to a location, yet, then it is given the
	 * ManagedService(Factory)'s bundle's location.
	 * 
	 * @param config a Configuration
	 * @param toStore if the configuration is assigned a bundle location, should
	 *        it be stored
	 * @return target ManagedService(Factory)'s service reference.
	 */
	protected static ServiceReference searchForService(
			ConfigurationImpl config, boolean toStore) {
		ServiceReference[] sRefs = null;
		try {
			if (config.fPid == null) {
				sRefs = bc.getServiceReferences(ManagedService.class.getName(),
						"(" + Constants.SERVICE_PID + "=" + config.pid + ")");
			}
			else {
				sRefs = bc.getServiceReferences(ManagedServiceFactory.class
						.getName(), "(" + Constants.SERVICE_PID + "="
						+ config.fPid + ")");
			}
		}
		catch (InvalidSyntaxException ise) {
			/* syntax is valid */
		}
		if (sRefs == null)
			return null;
		for (int i = 0; i < sRefs.length; i++) {
			if (checkLocation(config, sRefs[i].getBundle(), toStore)) {
				return sRefs[i];
			}
			else {
				return null;
			}
		}
		return null;
	}

	private static boolean checkLocation(ConfigurationImpl config,
			Bundle bundle, boolean storeLocation) {
		final Bundle b = bundle;
		String locationToCheck = (String) AccessController
				.doPrivileged(new PrivilegedAction() {
					public Object run() {
						return b.getLocation();
					}
				});
		if (config.location == null) {
			config.setLocation(locationToCheck, storeLocation);
			return true;
		}
		else
			if (locationToCheck.equals(config.location)) {
				return true;
			}
			else {
				if (config.fPid != null) {
					Log.log(1, "[CM]Bundle with location: " + locationToCheck
							+ " is trying to forge pid: " + config.fPid);
				}
				else {
					Log.log(1, "[CM]Bundle with location: " + locationToCheck
							+ " is trying to forge pid: " + config.pid);
				}
				return false;
			}
	}

	private void informManagedServices(boolean isMS)
			throws InvalidSyntaxException {
		ServiceReference[] sRefs = null;
		if (isMS) {
			sRefs = bc.getServiceReferences(ManagedService.class.getName(),
					null);
		}
		else {
			sRefs = bc.getServiceReferences(ManagedServiceFactory.class
					.getName(), null);
		}
		if (sRefs != null) {
			for (int i = 0; i < sRefs.length; i++) {
				processService(sRefs[i], isMS);
			}
		}
	}

	private void processService(ServiceReference sRef, boolean isMS) {
		String pid = (String) sRef.getProperty(Constants.SERVICE_PID);
		if (pid == null)
			return;
		if (isMS) {
			ConfigurationImpl config = (ConfigurationImpl) ConfigurationStorage.configs
					.get(pid);
			if (config != null && checkLocation(config, sRef.getBundle(), true)) {
				try {
					config.callBackManagedService(sRef, null);
				}
				catch (IOException ioe) {
					Log.log(1, "[CM]Error while binding bundle location!", ioe);
				}
			}
			else
				if (config == null) {
					CMEventManager.addEvent(new CMEvent(null, null,
							CMEvent.UPDATED, sRef));
				}
		}
		else {
			synchronized (storage) {
				Vector factoryConfigs = (Vector) ConfigurationStorage.configsF.get(pid);
				if (factoryConfigs != null) {
					Enumeration e = factoryConfigs.elements();
					ConfigurationImpl config;
					while (e.hasMoreElements()) {
						config = (ConfigurationImpl) e.nextElement();
						if (checkLocation(config, sRef.getBundle(), true)) {
							try {
								config.callBackManagedService(sRef, null);
							}
							catch (IOException ioe) {
								Log
										.log(
												1,
												"[CM]Error while binding bundle location!",
												ioe);
							}
						}
					}
				}
			}
		}
	}
}