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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.model.ProvideDescription;
import org.eclipse.osgi.component.resolver.ComponentProperties;
import org.eclipse.osgi.component.resolver.Reference;
import org.eclipse.osgi.component.resolver.Resolver;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.eclipse.osgi.impl.service.component.ComponentFactoryImpl;
import org.eclipse.osgi.impl.service.component.ComponentInstanceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentFactory;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InstanceProcess implements WorkDispatcher {

	/* set this to true to compile in debug messages */
	static final boolean DEBUG = false;

	static final String COMPONENT_FACTORY_CLASS = "org.osgi.service.component.ComponentFactory";

	/**
	 * Service Component instances need to be built.
	 */
	public static final int BUILT = 1;

	/**
	 * Service Component instances need to be disposed.
	 */
	public static final int DISPOSED = 2;

	/** map CDP:serviceRegistration [1:1] */
	public Hashtable registrations;
	
	/** SCR bundle context */
	protected BundleContext scrBundleContext;

	/** Main SCR class */
	protected Main main;

	/** map CDP:factory */
	protected Hashtable factories;

	/** map CDP:instance */
	protected Hashtable instances;

	/** ConfigurationAdmin instance */
	protected ConfigurationAdmin configurationAdmin;

	/** Properties for this Component from the Configuration Admin */
	protected ComponentProperties componentProperties = null;

	/* Actually does the work of building and disposing of instances */
	public BuildDispose buildDispose;

	private WorkQueue workQueue;

	/** 
	 * Handle Instance processing building and disposing.  
	 * 
	 * @param main - the Main class of the SCR
	 */
	public InstanceProcess(Main main) {

		this.main = main;

		//for now use Main's workqueue
		workQueue = main.workQueue;
		scrBundleContext = main.context;
		registrations = new Hashtable();
		factories = new Hashtable();
		instances = new Hashtable();
		buildDispose = new BuildDispose(main, registrations);
		componentProperties = new ComponentProperties(main);

	}

	/**
	 * dispose cleanup, the SCR is shutting down
	 */
	public void dispose() {

		buildDispose.dispose();
		buildDispose = null;
		main = null;
		factories = null;
		instances = null;
		workQueue = null;
		registrations = null;
		componentProperties = null;
		scrBundleContext = null;
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
		Object obj = null;

		//first create instance for each CDP
		//The advance creation of the instance allows us to deal with conflict
		//resolution i.e. provide an instance when locateService is called
		//even if registerService has not been completed.
		// loop through CD+P list of enabled
		if (componentDescriptionProps != null) {
			Iterator it = componentDescriptionProps.iterator();
			while (it.hasNext()) {
				componentDescriptionProp = (ComponentDescriptionProp) it.next();
				componentDescription = componentDescriptionProp.getComponentDescription();
				try {
					obj = buildDispose.createInstance(componentDescription);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (obj != null) {
					instances.put(componentDescription, obj);
				}

			}
		}

		// loop through CD+P list of enabled
		if (componentDescriptionProps != null) {
			Iterator it = componentDescriptionProps.iterator();
			while (it.hasNext()) {
				componentDescriptionProp = (ComponentDescriptionProp) it.next();
				componentDescription = componentDescriptionProp.getComponentDescription();
				BundleContext bundleContext = main.framework.getBundleContext(componentDescription.getBundle());

				//if Service not provided - create instance immediately
				if (componentDescription.getService() == null) {
					try {
						buildDispose.build(bundleContext, null, componentDescriptionProp, null, null);
					} catch (Exception e) {
						main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), e);
					}
				} else {

					// ComponentFactory
					if (componentDescription.getFactory() != null) {

						//check if MSF
						configurationAdmin = componentProperties.getConfigurationAdmin();

						try {
							Configuration config = configurationAdmin.getConfiguration(componentDescription.getName());
							if (config != null)
								factoryPid = config.getFactoryPid();
						} catch (Exception e) {
							main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(), e);
						}

						//if MSF throw exception - can't be ComponentFactory add MSF
						if (factoryPid != null) {
							throw new org.osgi.service.component.ComponentException("ManagedServiceFactory and ConfigurationFactory are incompatible");
						}
						ComponentFactory factory = new ComponentFactoryImpl(bundleContext, componentDescriptionProp, this);
						registerComponentFactory(bundleContext, componentDescriptionProp, factory);
						factory.newInstance(null);

						// ServiceFactory
					} else if (componentDescription.getService().isServicefactory()) {
						registrations.put(
								componentDescriptionProp, 
								RegisterComponentService.registerService(this,bundleContext, componentDescriptionProp,true)
								);

						// No ServiceFactory
					} else {
						registrations.put(
								componentDescriptionProp,
								RegisterComponentService.registerService(this,bundleContext, componentDescriptionProp, false)
								);
					}
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
				} else {
					// if ComponentFactory or if just Services
					if (componentDescription.getFactory() != null) {
						buildDispose.disposeComponent(componentDescriptionProp);
						ServiceRegistration reg = (ServiceRegistration) factories.get(componentDescriptionProp);
						reg.unregister();
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

	public Object getInstanceWithInterface(String interfaceName) {
		for (int i = 0; i < instances.size(); i++) {
			Enumeration keys = instances.keys();
			while (keys.hasMoreElements()) {
				ComponentDescription cd = (ComponentDescription) keys.nextElement();
				ProvideDescription[] provides = cd.getService().getProvides();
				for (int j = 0; j < provides.length; j++) {
					if (interfaceName.equals(provides[j].getInterfacename())) {
						return (instances.get(cd));
					}
				}
			}
		}
		return null;
	}

	public Object getInstance(ComponentDescription componentDescription) {
		Object obj = instances.get(componentDescription);
		if (obj != null)
			instances.remove(componentDescription);
		return obj;
	}

	/**Register Services
	 * 
	 * @param bundleContext
	 * @param componentDescriptionProp
	 * 
	 */

	public void registerServices(BundleContext bundleContext, ComponentDescriptionProp componentDescriptionProp) {
		registrations.put(
				componentDescriptionProp, 
				RegisterComponentService.registerService(this,bundleContext, componentDescriptionProp,false)
				);
	}

	/**
	 * Register the Component Factory
	 * 
	 * @param context
	 * @param componentDescriptionProp
	 * @param factory
	 */
	public void registerComponentFactory(BundleContext context, ComponentDescriptionProp componentDescriptionProp, ComponentFactory factory) {
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
			List instances = (List) buildDispose.instanceMap.get(cdp);
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				ComponentInstanceImpl compInstance = (ComponentInstanceImpl) it.next();
				if (compInstance != null) {
					try {
						buildDispose.bindReference(reference, compInstance);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
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
			List instances = (List) buildDispose.instanceMap.get(cdp);
			Iterator it = instances.iterator();
			while (it.hasNext()) {
				ComponentInstanceImpl compInstance = (ComponentInstanceImpl) it.next();
				Object instance = compInstance.getInstance();
				if (instance != null) {
					try {
						buildDispose.unbindDynamicReference(cdp,reference, compInstance, serviceReference);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			
			//all instances are now unbound
			reference.removeServiceReference(serviceReference);
		}
	}
}

