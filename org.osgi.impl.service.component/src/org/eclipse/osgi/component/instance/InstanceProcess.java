/*
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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

import org.eclipse.osgi.component.Log;
import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.resolver.ComponentProperties;
import org.eclipse.osgi.component.resolver.Reference;
import org.eclipse.osgi.component.resolver.Resolver;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.osgi.service.component.ComponentException;
import org.eclipse.osgi.impl.service.component.ComponentFactoryImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InstanceProcess implements WorkDispatcher, ConfigurationListener, ServiceTrackerCustomizer {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	static final String COMPONENT_FACTORY_CLASS = "org.osgi.service.component.ComponentFactory";
	static final String CMADMIN_SERVICE_CLASS = "org.osgi.service.cm.ConfigurationAdmin";
	static final String CONFIG_LISTENER_CLASS = "org.osgi.service.cm.ConfigurationListener";

	/** ConfigurationAdmin instance */
	protected ConfigurationAdmin configurationAdmin;

	/* ServiceTracker for configurationAdmin */
	private ServiceTracker configAdminTracker;

	/**
	 * Service Component instances need to be built.
	 */
	public static final int BUILT = 1;

	/**
	 * Service Component instances need to be disposed.
	 */
	public static final int DISPOSED = 2;

	/** map CDP:serviceRegistration [1:n] */
	public Hashtable registrations;

	/** map fpid:spids [1:n] */
	protected Hashtable fpids;

	/** Main SCR class */
	protected Main main;

	/** map CDP:factory */
	protected Hashtable factories;

	/** Properties for this Component from the Configuration Admin */
	protected ComponentProperties componentProperties = null;

	/* Actually does the work of building and disposing of instances */
	public BuildDispose buildDispose;

	protected WorkQueue workQueue;

	protected ServiceRegistration configListener;

	/** 
	 * Handle Instance processing building and disposing.  
	 * 
	 * @param main - the Main class of the SCR
	 */
	public InstanceProcess(Main main) {

		this.main = main;

		//for now use Main's workqueue
		workQueue = main.workQueue;
		registrations = new Hashtable();
		fpids = new Hashtable();
		factories = new Hashtable();
		buildDispose = new BuildDispose(main);
		componentProperties = new ComponentProperties(main);
		configAdminTracker = new ServiceTracker(main.context, CMADMIN_SERVICE_CLASS, this);
		configAdminTracker.open(true); //true for track all services
		registerConfigurationListener();

	}

	/**
	 * dispose cleanup, the SCR is shutting down
	 */
	public void dispose() {

		removeConfigurationListener();
		buildDispose.dispose();
		configAdminTracker.close();
		configAdminTracker = null;
		buildDispose = null;
		main = null;
		factories = null;
		workQueue = null;
		registrations = null;
		fpids = null;
		componentProperties = null;
	}

	/**
	 * Build the Service Component Instances, includes activating and binding
	 * 
	 * @param componentDescriptionProps - a List of all instances to build.
	 */

	public void buildInstances(List componentDescriptionProps) {

		ComponentDescriptionProp componentDescriptionProp;
		ComponentDescription componentDescription;
		String factoryPid = null;

		// loop through CD+P list of enabled
		if (componentDescriptionProps != null) {
			Iterator it = componentDescriptionProps.iterator();
			while (it.hasNext()) {
				componentDescriptionProp = (ComponentDescriptionProp) it.next();
				componentDescription = componentDescriptionProp.getComponentDescription();
				if (DEBUG)
					System.out.println("InstanceProcess: buildInstances: component name = " + componentDescription.getName());

				BundleContext bundleContext = main.framework.getBundleContext(componentDescription.getBundle());

				//if component is immediate - create instance immediately
				if (componentDescription.isImmediate()) {
					try {
						buildDispose.build(bundleContext, null, componentDescriptionProp, null);
					} catch (ComponentException e) {
						Log.log(1, "[SCR] Error attempting to build Component.", e);
					} 
				}

				// ComponentFactory
				if (componentDescription.getFactory() != null) {
					if (DEBUG)
						System.out.println("InstanceProcess: buildInstances: ComponentFactory");
					//check if MSF
					configurationAdmin = componentProperties.getConfigurationAdmin();

					try {
						Configuration config = configurationAdmin.getConfiguration(componentDescription.getName());
						if (config != null)
							factoryPid = config.getFactoryPid();
					} catch (IOException e) {
						Log.log(1, "[SCR] Error attempting to create componentFactory. ", e);
					}

					//if MSF throw exception - can't be ComponentFactory add MSF
					if (factoryPid != null) {
						throw new org.osgi.service.component.ComponentException("ManagedServiceFactory and ConfigurationFactory are incompatible");
					}
					ComponentFactory factory = new ComponentFactoryImpl(bundleContext, componentDescriptionProp, this);
					registerComponentFactory(bundleContext, componentDescriptionProp, factory);

					// if ServiceFactory or Service
				} else if (componentDescription.getService() != null) {
					registrations.put(
							componentDescriptionProp, 
							RegisterComponentService.registerService(
									this, 
									bundleContext, 
									componentDescriptionProp, 
									componentDescription.getService().isServicefactory(), 
									null)
								);
				}
				
			}//end while(more componentDescriptionProps)
		}//end if (componentDescriptionProps != null)
	}

	/**
	 * 
	 * Dispose of Component Instances, includes unregistering services and removing instances.
	 *
	 * @param componentDescriptionProps - list of ComponentDescriptions plus Property objects to be disposed
	 */

	public void disposeInstances(List componentDescriptionProps) {

		ComponentDescriptionProp componentDescriptionProp;
		ComponentDescription componentDescription;
		//	loop through CD+P list to be disposed
		if (componentDescriptionProps != null) {
			Iterator it = componentDescriptionProps.iterator();
			while (it.hasNext()) {
				componentDescriptionProp = (ComponentDescriptionProp) it.next();
				componentDescription = componentDescriptionProp.getComponentDescription();

				//if no Services provided - dispose of instance immediately
				if (componentDescription.getService() == null) {
					buildDispose.disposeComponent(componentDescriptionProp);
					//if ComponentFactory or if just Services
				} else {
					// if ComponentFactory 
					if (componentDescription.getFactory() != null) {
						buildDispose.disposeComponent(componentDescriptionProp);
						ServiceRegistration reg = (ServiceRegistration) factories.get(componentDescriptionProp);
						try {
							reg.unregister();
						} catch (IllegalStateException e) {
							//Service is already unregistered
							//do nothing
						}
					}
					//unregister all services
					ServiceRegistration serviceRegistration = (ServiceRegistration) registrations.get(componentDescriptionProp);
					try {
						if (serviceRegistration != null)
							serviceRegistration.unregister();
					} catch (IllegalStateException e) {
						//Service is already unregistered
						//do nothing
					}

					//remove from service registrations list
					registrations.remove(componentDescriptionProp);
				}
			}
		}

		// when dispose is complete, call back to resolver with list of disposed components
		workQueue.enqueueWork(this, DISPOSED, componentDescriptionProps);
	}

	/**Register Services
	 * 
	 * @param bundleContext
	 * @param componentDescriptionProp
	 * 
	 */

	public ServiceRegistration registerServices(BundleContext bundleContext, ComponentDescriptionProp componentDescriptionProp) {
		ServiceRegistration reg = RegisterComponentService.registerService(this, bundleContext, componentDescriptionProp, false, null);
		registrations.put(componentDescriptionProp, reg);
		return reg;
	}

	/**
	 * Register the Component Factory
	 * 
	 * @param context
	 * @param componentDescriptionProp
	 * @param factory
	 */
	private void registerComponentFactory(BundleContext context, ComponentDescriptionProp componentDescriptionProp, ComponentFactory factory) {
		ComponentDescription componentDescription = componentDescriptionProp.getComponentDescription();
		// if the factory attribute is set on the component element then register a component factory service
		// for the Service Component on behalf of the Service Component.
		Hashtable properties = new Hashtable(2);
		properties.put(ComponentConstants.COMPONENT_NAME, componentDescription.getName());
		properties.put(ComponentConstants.COMPONENT_FACTORY, componentDescription.getFactory());
		ServiceRegistration serviceRegistration = context.registerService(COMPONENT_FACTORY_CLASS, factory, properties);
		factories.put(componentDescriptionProp, serviceRegistration);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int, java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		//ComponentDescriptionProp componentDescriptionProp = (ComponentDescriptionProp) workObject;
		List componentDescriptionProps = (List) workObject;
		switch (workAction) {
			//			case BUILD_FAILED :
			//				main.resolver.markFailedInstance(componentDescriptionProp);
			//				break;
			case DISPOSED :
				main.resolver.disposedComponents(componentDescriptionProps);
				break;
			case BUILT :
				main.resolver.builtComponents(componentDescriptionProps);
				break;
		}
	}

	public void dynamicBind(Hashtable serviceTable) {
		Enumeration e = serviceTable.keys();
		while (e.hasMoreElements()) {
			Reference reference = (Reference) e.nextElement();
			ComponentDescriptionProp cdp = (ComponentDescriptionProp) serviceTable.get(reference);
			List instances = cdp.getInstances();
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				ComponentInstanceImpl compInstance = (ComponentInstanceImpl) it.next();
				if (compInstance != null) {
					buildDispose.bindReference(cdp, reference, compInstance, main.framework.getBundleContext(cdp.getComponentDescription().getBundle()));
					
				}
			}
		}
	}

	/**
	 * Called by dispatcher ( Resolver) when work available on queue
	 * 
	 * @param serviceTable Map of ReferenceDescription:subtable
	 * 			Subtable Maps cdp:serviceReference
	 *  
	 */
	public void dynamicUnBind(List unbindJobs) {
		//for each unbind job
		Iterator itr = unbindJobs.iterator();
		while (itr.hasNext()) {
			Resolver.DynamicUnbindJob unbindJob = (Resolver.DynamicUnbindJob) itr.next();
			Reference reference = unbindJob.reference;
			ComponentDescriptionProp cdp = unbindJob.component;
			ServiceReference serviceReference = unbindJob.serviceReference;

			//get the list of instances created
			List instances = cdp.getInstances();
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				ComponentInstanceImpl compInstance = (ComponentInstanceImpl) it.next();
				Object instance = compInstance.getInstance();
				if (instance != null) {
					try {
						buildDispose.unbindDynamicReference(cdp, reference, compInstance, serviceReference);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			//all instances are now unbound
			reference.removeServiceReference(serviceReference);
		}
	}

	/**
	 * registerConfigListener - Listen for changes in the configuration
	 * 
	 */
	public void registerConfigurationListener() {
		configListener = main.context.registerService(CONFIG_LISTENER_CLASS, this, null);

	}

	/**
	 * removeConfigurationListener -
	 * 
	 */
	public void removeConfigurationListener() {
		if (configListener != null)
			main.context.ungetService(configListener.getReference());

	}

	/**
	 * Listen for configuration changes
	 * 
	 * Service Components can receive properties from the Configuration 
	 * Admin service. If a Service Component is activated and it’s properties
	 * are updated in the Configuration Admin service, the SCR must deactivate the component
	 * and activate the component again using the new properties.
	 * 
	 * @param event ConfigurationEvent
	 */
	public void configurationEvent(ConfigurationEvent event) {

		Configuration[] config = null;
		ArrayList spids = null;
		ComponentDescriptionProp componentDescriptionProp;
		Enumeration keys;

		String pid = event.getPid();
		if (DEBUG)
			System.out.println("pid = " + pid);

		String fpid = event.getFactoryPid();
		if (DEBUG)
			System.out.println("fpid = " + fpid);

		switch (event.getType()) {
			case ConfigurationEvent.CM_UPDATED :

				// Get the config for this service.pid
				ConfigurationAdmin cm = (ConfigurationAdmin) configAdminTracker.getService();
				try {
					config = cm.listConfigurations("(service.pid=" + pid + ")");
				} catch (IOException e) {
					Log.log(1, "[SCR] Error attempting to list CM Configurations ", e);
				} catch (InvalidSyntaxException e) {
					Log.log(1, "[SCR] Error attempting to list CM Configurations ", e);
				}

				//If a MSF
				//Register a SF for each new config
				if (fpid != null) {

					// if fpid is found in list of saved factory pids
					if (fpids.containsKey(fpid)) {

						// get service pids associated to this factory pid
						spids = (ArrayList) fpids.get(fpid);
						if (spids == null)
							spids = new ArrayList();

						// if a service pid match is found then this is an UPDATE of the properties
						if (spids.contains(pid)) {

							updateServiceProperties(fpid, config);

							// else this is a NEW config
						} else {

							//register a new SF with config.getProperties();
							keys = registrations.keys();
							while (keys.hasMoreElements()) {
								componentDescriptionProp = (ComponentDescriptionProp) keys.nextElement();
								if (fpid.equals(componentDescriptionProp.getComponentDescription().getName())) {
									BundleContext bundleContext = main.framework.getBundleContext(componentDescriptionProp.getComponentDescription().getBundle());
									ServiceRegistration t = RegisterComponentService.registerService(this, bundleContext, componentDescriptionProp, false, config[0].getProperties());
									registrations.put(componentDescriptionProp, t);
								}
							}
							spids.add(pid);
							fpids.put(fpid, spids);
						}

						// fpid is not found so create a new entry
						// Since the config has never been updated before do an update instead of a new
					} else {

						spids = new ArrayList();
						spids.add(pid);
						fpids.put(fpid, spids);
						updateServiceProperties(fpid, config);
					}

					// else if NOT a factory	
				} else {

					//find the spid == implementation name in the CDP list
					//then get service registration and update properties
					updateServiceProperties(pid, config);
				}

				break;
			case ConfigurationEvent.CM_DELETED :

				//unregister the Service if the config was deleted
				String comparePid = fpid == null ? pid : fpid;
				updateServiceProperties(comparePid, null);

				break;
		}

	}

	private void updateServiceProperties(String pid, Configuration[] config) {

		Enumeration keys;
		ServiceRegistration serviceRegistration;
		ComponentDescriptionProp componentDescriptionProp;

		Dictionary props = null;
		if (config != null)
			props = config[0].getProperties();

		//New properties for Service
		keys = registrations.keys();
		while (keys.hasMoreElements()) {
			componentDescriptionProp = (ComponentDescriptionProp) keys.nextElement();
			if (pid.equals(componentDescriptionProp.getComponentDescription().getName())) {
				serviceRegistration = (ServiceRegistration) registrations.get(componentDescriptionProp);
				serviceRegistration.unregister();
				registrations.remove(componentDescriptionProp);
				BundleContext bundleContext = main.framework.getBundleContext(componentDescriptionProp.getComponentDescription().getBundle());
				ServiceRegistration reg = RegisterComponentService.registerService(this, bundleContext, componentDescriptionProp, false, props);
				registrations.put(componentDescriptionProp, reg);
			}
		}
	}

	/**
	 * A ConfigurationAdmin Service is being added to the ServiceTracker object.
	 *
	 * @param reference Reference to service being added to the <tt>ServiceTracker</tt> object.
	 * @return The service object to be tracked for the
	 * <tt>ServiceReference</tt> object or <tt>null</tt> if the <tt>ServiceReference</tt> object should not
	 * be tracked.
	 */
	public Object addingService(ServiceReference ref) {
		configurationAdmin = (ConfigurationAdmin) main.context.getService(ref);
		return configurationAdmin;
	}

	/**
	 * A ConfigurationAdmin Service tracked by the ServiceTracker object has been modified.
	 *
	 * @param reference Reference to service that has been modified.
	 * @param service The service object for the modified service.
	 */
	public void modifiedService(ServiceReference ref, Object object) {

	}

	/**
	 * The ConfigurationAdmin Service tracked by the Service Tracker has been removed
	 *
	 * <p>This method is called after a service is no longer being tracked
	 * by the <tt>ServiceTracker</tt> object.
	 *
	 * @param reference Reference to service that has been removed.
	 * @param service The service object for the removed service.
	 */
	public void removedService(ServiceReference reference, Object object) {
		main.context.ungetService(reference);
	}

}
