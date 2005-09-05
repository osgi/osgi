/*
 * $Header$
 * 
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component.instance;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.resolver.Reference;
import org.eclipse.osgi.component.resolver.Resolver;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.eclipse.osgi.impl.service.component.ComponentFactoryImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.ComponentFactory;

/**
 * Register services for satisfied Component Configurations and listen for 
 * configuration changes from ConfigAdmin
 * 
 */
public class InstanceProcess implements ConfigurationListener {

	/* set this to true to compile in debug messages */
	static final boolean			DEBUG					= false;

	/** Main SCR class */
	protected Main					main;

	/* Actually does the work of building and disposing of instances */
	public BuildDispose				buildDispose;

	protected WorkQueue				workQueue;

	//configListener service registered by this class
	protected ServiceRegistration	configListener;

	/**
	 * Handle Instance processing
	 * 
	 * @param main - the Main class of the SCR
	 */
	public InstanceProcess(Main main) {

		this.main = main;

		// use Main's workqueue
		workQueue = main.workQueue;
		buildDispose = new BuildDispose(main);
		
		configListener = main.context.registerService(
				ConfigurationListener.class.getName(), this, null);

	}

	/**
	 * dispose cleanup, the SCR is shutting down
	 */
	public void dispose() {

		if (configListener != null) {
			main.context.ungetService(configListener.getReference());
		}
		
		buildDispose.dispose();
		buildDispose = null;
		main = null;
		workQueue = null;
	}

	/**
	 * Register and possibly activate Component Configurations.
	 * 
	 * Activate each Service Component that has the attribute immediate=true.
	 * 
	 * If Service Component has a service element, register the service or
	 * service factory.
	 * 
	 * If Service Component has the factory attribute set, register a 
	 * {@link org.osgi.service.component.ComponentFactory}.
	 * 
	 * @param componentDescriptionProps - List of satisfied Component Configurations
	 */
	public void registerComponentConfigs(List componentDescriptionProps) {

		ComponentDescriptionProp cdp;
		ComponentDescription cd;
		String factoryPid = null;

		// loop through CD+P list
		if (componentDescriptionProps != null) {
			Iterator it = componentDescriptionProps.iterator();
			while (it.hasNext()) {
				cdp = (ComponentDescriptionProp) it.next();
				cd = cdp.getComponentDescription();
				if (DEBUG)
					System.out
							.println("InstanceProcess: buildInstances: component name = "
									+ cd.getName());

				// if component is immediate - create instance immediately
				if (cd.isImmediate()) {
					try {
						buildDispose.buildComponentConfigInstance(null, cdp);
					}
					catch (ComponentException e) {
						Log
								.log(
										1,
										"[SCR] Error attempting to build Component.",
										e);
					}
				}

				// ComponentFactory
				if (cdp.isComponentFactory()) {
					if (DEBUG)
						System.out
								.println("InstanceProcess: buildInstances: ComponentFactory");
					// check if MSF
					ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) main.resolver.configAdminTracker
							.getService();

					if (configurationAdmin != null) {
						try {
							Configuration config = configurationAdmin
									.getConfiguration(cd.getName());
							if (config != null)
								factoryPid = config.getFactoryPid();
						}
						catch (IOException e) {
							Log
									.log(
											1,
											"[SCR] Error attempting to create componentFactory. ",
											e);
						}
					}

					// if MSF throw exception - can't be ComponentFactory and
					// MSF
					if (factoryPid != null) {
						throw new org.osgi.service.component.ComponentException(
								"ManagedServiceFactory and ConfigurationFactory are incompatible");
					}

					// if the factory attribute is set on the component element
					// then register a component factory service
					// for the Service Component on behalf of the Service
					// Component.
					cdp.setServiceRegistration(cd.getBundleContext()
							.registerService(ComponentFactory.class.getName(),
									new ComponentFactoryImpl(cdp, main),
									cdp.getProperties()));

					// if ServiceFactory or Service
				}
				else
					if (cd.getService() != null) {
						RegisterComponentService.registerService(this, cdp);
					}

			}// end while(more componentDescriptionProps)
		}// end if (componentDescriptionProps != null)
	}

	/**
	 * 
	 * Dispose of Component Configurations
	 * 
	 * @param componentDescriptionProps - list of Component Configurations to be
	 *         disposed
	 */

	public void disposeComponentConfigs(List componentDescriptionProps) {

		// loop through CD+P list to be disposed
		if (componentDescriptionProps != null) {
			Iterator it = componentDescriptionProps.iterator();
			while (it.hasNext()) {
				ComponentDescriptionProp cdp = (ComponentDescriptionProp) it
						.next();

				// dispose component
				buildDispose.disposeComponentConfig(cdp);
			}
		}

	}

	/**
	 * Dynamically bind references.
	 * 
	 * @param serviceTable {@link Reference}:{@link ComponentDescription} map to bind
	 */
	public void dynamicBind(Hashtable serviceTable) {
		Enumeration e = serviceTable.keys();
		while (e.hasMoreElements()) {
			Reference reference = (Reference) e.nextElement();
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) serviceTable
					.get(reference);
			List instances = cdp.getInstances();
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				ComponentInstanceImpl compInstance = (ComponentInstanceImpl) it
						.next();
				if (compInstance != null) {
					buildDispose.bindReference(reference, compInstance);

				}
			}
		}
	}

	/**
	 * Dynamically unbind references.
	 * 
	 * @param unbindJobs List of 
	 * {@link org.eclipse.osgi.component.resolver.Resolver.DynamicUnbindJob Resolver.DynamicUnbindJob}s
	 * to be unbound
	 */
	public void dynamicUnBind(List unbindJobs) {
		// for each unbind job
		Iterator itr = unbindJobs.iterator();
		while (itr.hasNext()) {
			Resolver.DynamicUnbindJob unbindJob = (Resolver.DynamicUnbindJob) itr
					.next();
			Reference reference = unbindJob.reference;
			ComponentDescriptionProp cdp = unbindJob.component;
			ServiceReference serviceReference = unbindJob.serviceReference;

			// get the list of instances created
			List instances = cdp.getInstances();
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				ComponentInstanceImpl compInstance = (ComponentInstanceImpl) it
						.next();
				Object instance = compInstance.getInstance();
				if (instance != null) {
					try {
						buildDispose.unbindDynamicReference(reference,
								compInstance, serviceReference);
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// all instances are now unbound
			reference.removeServiceReference(serviceReference);
		}
	}

	/**
	 * Listen for configuration changes
	 * 
	 * Service Components can receive properties from the Configuration Admin
	 * service. If a Component Configuration is activated and it’s properties are
	 * updated in the Configuration Admin service, the SCR must deactivate the
	 * component and activate the component again using the new properties.
	 * 
	 * @param event ConfigurationEvent
	 * 
	 * @see ConfigurationListener#configurationEvent(org.osgi.service.cm.ConfigurationEvent)
	 */
	public void configurationEvent(ConfigurationEvent event) {

		Configuration[] config = null;

		String pid = event.getPid();
		if (DEBUG)
			System.out.println("pid = " + pid);

		String fpid = event.getFactoryPid();
		if (DEBUG)
			System.out.println("fpid = " + fpid);

		switch (event.getType()) {
			case ConfigurationEvent.CM_UPDATED :

				String filter = (fpid != null ? "(&" : "")
						+ "("
						+ Constants.SERVICE_PID
						+ "="
						+ pid
						+ ")"
						+ (fpid != null ? ("("
								+ ConfigurationAdmin.SERVICE_FACTORYPID + "="
								+ fpid + "))") : "");

				// Get the config for this service.pid
				ConfigurationAdmin cm = (ConfigurationAdmin) main.resolver.configAdminTracker
						.getService();
				try {
					config = cm.listConfigurations(filter);
				}
				catch (IOException e) {
					Log
							.log(
									1,
									"[SCR] Error attempting to list CM Configurations ",
									e);
				}
				catch (InvalidSyntaxException e) {
					Log
							.log(
									1,
									"[SCR] Error attempting to list CM Configurations ",
									e);
				}

				// if NOT a factory
				if (fpid == null) {

					// find the spid == component name in the CD list
					ComponentDescription cd = (ComponentDescription) main.resolver.enabledCDsByName
							.get(pid);

					// there is only one CDP for this CD, so we can disable the
					// CD
					main.resolver.disableComponents(Collections
							.singletonList(cd));

					// now re-enable the CD - the resolver will pick up the new
					// config
					workQueue.enqueueWork(main, Main.ADD, Collections
							.singletonList(cd));

					// If a MSF
					// create a new CDP or update an existing one
				}
				else {
					// find the fpid == component name in the CD list
					ComponentDescription cd = (ComponentDescription) main.resolver.enabledCDsByName
							.get(fpid);

					// get cdp with this PID
					ComponentDescriptionProp cdp = cd
							.getComponentDescriptionPropByPID(pid);

					// if only the no-props cdp exists, replace it
					if (cdp == null
							&& cd.getComponentDescriptionProps().size() == 1
							&& ((ComponentDescriptionProp) cd
									.getComponentDescriptionProps().get(0))
									.getProperties().get(Constants.SERVICE_PID) == null) {
						cdp = (ComponentDescriptionProp) cd
								.getComponentDescriptionProps().get(0);
					}

					// if old cdp exists, dispose of it
					if (cdp != null) {
						// config already exists - dispose of it
						cd.removeComponentDescriptionProp(cdp);
						main.resolver.disposeComponentConfigs(Collections
								.singletonList(cdp));
					}

					// create a new cdp (adds to resolver enabledCDPs list)
					main.resolver.map(cd, config[0].getProperties());

					// kick the resolver to figure out if CDP is satisfied, etc
					workQueue.enqueueWork(main, Main.ADD, null);

				}

				break;
			case ConfigurationEvent.CM_DELETED :

				// if not a factory
				if (fpid == null) {
					// find the spid == component name in the CD list
					ComponentDescription cd = (ComponentDescription) main.resolver.enabledCDsByName
							.get(pid);

					// there is only one CDP for this CD, so we can disable the
					// CD
					main.resolver.disableComponents(Collections
							.singletonList(cd));

					// now re-enable the CD - the resolver will create CDP with
					// no
					// configAdmin properties
					workQueue.enqueueWork(main, Main.ADD, Collections
							.singletonList(cd));
				}
				else {
					// config is a factory

					// find the fpid == component name in the CD list
					ComponentDescription cd = (ComponentDescription) main.resolver.enabledCDsByName
							.get(fpid);

					// get CDP created for this config (with this PID)
					ComponentDescriptionProp cdp = cd
							.getComponentDescriptionPropByPID(pid);

					// if this was the last CDP created for this factory
					if (cd.getComponentDescriptionProps().size() == 1) {

						// there is only one CDP for this CD, so we can disable
						// the CD
						main.resolver.disableComponents(Collections
								.singletonList(cd));

						// now re-enable the CD - the resolver will create CDP
						// with no
						// configAdmin properties
						workQueue.enqueueWork(main, Main.ADD, Collections
								.singletonList(cd));

					}
					else {

						// we can just dispose this CDP
						cd.removeComponentDescriptionProp(cdp);
						disposeComponentConfigs(Collections.singletonList(cdp));
						main.resolver.satisfiedCDPs.remove(cdp);
						main.resolver.enabledCDPs.remove(cdp);

					}
				}
				break;
		}

	}
}
