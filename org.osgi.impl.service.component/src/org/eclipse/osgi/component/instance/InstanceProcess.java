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
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.osgi.component.Main;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.ComponentDescriptionProp;
import org.eclipse.osgi.component.resolver.ComponentProperties;
import org.eclipse.osgi.component.workqueue.WorkDispatcher;
import org.eclipse.osgi.component.workqueue.WorkQueue;
import org.eclipse.osgi.impl.service.component.ComponentFactoryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
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
public class InstanceProcess implements WorkDispatcher{
	
	/* set this to true to compile in debug messages */
	static final boolean				DEBUG	= false;
	
	/** SCR bundle context */
	protected BundleContext 			scrBundleContext;
	
	/** Main class */
	protected Main 						main;

	/** map CDP:serviceRegistrations [1:n] */
	protected Hashtable 				registrations;
	
	/** map CDP:factory */
	protected Hashtable					factories;
	
	/** Register Service class */
	protected RegisterComponentService 			registerService;
	
	/** Register ServiceFactory class */
	protected RegisterComponentServiceFactory 	registerServiceFactory;
	
	/** ConfigurationAdmin instance */
	protected ConfigurationAdmin 		configurationAdmin;

	/** Properties for this Component from the Configuration Admin */
	protected ComponentProperties 		componentProperties = null;
	
	/* Actually does the work of building and disposing of instances */
	private BuildDispose 				buildDispose;

	private WorkQueue workQueue;
	
	/** 
	 * Handle Instance processing building and disposing.  
	 * 
	 * @param main - the Main class of the SCR
	 */
	public InstanceProcess(Main main){
		
		this.main = main;
		
		//for now use Main's workqueue
		workQueue = main.workQueue;

		scrBundleContext = main.context;
			
		registrations = new Hashtable();
		
		factories = new Hashtable();
				
		buildDispose = new BuildDispose(main, registrations);
		
		componentProperties = new ComponentProperties(main);
	}
	
	/**
	 * dispose cleanup the SCR is shutting down
	 */
	public void dispose() {

		buildDispose.dispose();
		
		main = null;
		registrations = null;
		factories = null;
		componentProperties = null;
	}
		
	/**
	 * Build the Service Component Instances, includes activating and binding
	 * 
	 * @param componentDescriptionProps - an ArrayList of all instances to build.
	 */
	
	public void buildInstances(ArrayList componentDescriptionProps) {
				
		ArrayList serviceRegistrations = new ArrayList();
		ComponentDescriptionProp componentDescriptionProp;
		ComponentDescription componentDescription;
		String factoryPid = null;
				
		// loop through CD+P list of enabled
		if(componentDescriptionProps != null){
			Iterator it = componentDescriptionProps.iterator();
			while(it.hasNext()){
				componentDescriptionProp = (ComponentDescriptionProp)it.next();
				componentDescription = componentDescriptionProp.getComponentDescription();
				BundleContext bundleContext = main.framework.getBundleContext(componentDescription.getBundle());
				
				//if Service not provided - create instance immediately
				if (componentDescription.getService()== null){
					try {
						buildDispose.build(bundleContext, componentDescriptionProp, null, null);
					} catch (Exception e) {
						main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
					}
				} else {

					// ComponentFactory
					if (componentDescription.getFactory() != null){
						
						//check if MSF
						configurationAdmin = componentProperties.getConfigurationAdmin();
						
						try {
							Configuration config = configurationAdmin.getConfiguration(componentDescription.getName());
							if (config != null)
								factoryPid = config.getFactoryPid();
						} catch (Exception e) {
							main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
						}
						
						//if MSF throw exception - can't be ComponentFactory add MSF
						if (factoryPid != null) {
							throw new org.osgi.service.component.ComponentException("ManagedServiceFactory and ConfigurationFactory are incompatible");
						} else {
							ComponentFactory factory = new ComponentFactoryImpl(bundleContext, componentDescriptionProp, buildDispose );
							registerComponentFactory(bundleContext, componentDescriptionProp, factory);
							factory.newInstance(null);
						}
				
					// ServiceFactory
					} else if (componentDescription.getService().isServicefactory()){
						registerServiceFactory = new RegisterComponentServiceFactory(buildDispose);
						serviceRegistrations = registerServiceFactory.registerServices(bundleContext, componentDescriptionProp);
						registrations.put(componentDescriptionProp,serviceRegistrations);
								
					// No ServiceFactory
					} else {
						registerService = new RegisterComponentService(buildDispose);
						serviceRegistrations = registerService.registerServices(bundleContext, componentDescriptionProp);
						registrations.put(componentDescriptionProp,serviceRegistrations);
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
	
	public void disposeInstances(ArrayList componentDescriptionProps) {
		
		BundleContext bundleContext;
		ComponentDescriptionProp componentDescriptionProp;
		ComponentDescription componentDescription;
		
		//	loop through CD+P list to be disposed
		if(componentDescriptionProps != null){
			Iterator it = componentDescriptionProps.iterator();
			while(it.hasNext()){
				componentDescriptionProp = (ComponentDescriptionProp)it.next();
				componentDescription = componentDescriptionProp.getComponentDescription();
				
				//get BundleContext of the target bundle
				bundleContext = main.framework.getBundleContext(componentDescription.getBundle());
				String factoryPid = null;
		
				//if no Services provided - dispose of instance immediately
				if (componentDescription.getService()== null){
					buildDispose.dispose(bundleContext, componentDescriptionProp);
	
				} else {
			
					// if ComponentFactory or if just Services
					if (componentDescription.getFactory() != null){
						buildDispose.dispose(bundleContext, componentDescriptionProp);
						ServiceRegistration reg = (ServiceRegistration)factories.get(componentDescriptionProp);
						reg.unregister();
						
					}
					
					//unregister all services
					ArrayList serviceRegistrations = (ArrayList)registrations.get(componentDescriptionProp);
					Iterator iter = serviceRegistrations.iterator();
					while(iter.hasNext()){
						ServiceRegistration serviceRegistration = (ServiceRegistration)iter.next();
						try{
							serviceRegistration.unregister();
						} catch (IllegalStateException e) {
							//Service is already unregistered
							//do nothing
						}
					}
					//remove from service registrations list
					registrations.remove(componentDescriptionProp);
				
				}
			}
		}
		//TODO when dispose is complete, call back to resolver with list of disposed components
		//disableComponents(CD+Ps);

	}
	
	/**
	 * Register the Component Factory
	 * 
	 * @param context
	 * @param componentDescriptionProp
	 * @param factory
	 */
	public void registerComponentFactory(BundleContext context, ComponentDescriptionProp componentDescriptionProp, ComponentFactory factory){
		
		ComponentDescription componentDescription = componentDescriptionProp.getComponentDescription();
		// if the factory attribute is set on the component element then register a component factory service
		// for the Service Component on behalf of the Service Component.
		Hashtable properties = new Hashtable(2);
		properties.put(ComponentConstants.COMPONENT_NAME, componentDescription.getName());
		properties.put(ComponentConstants.COMPONENT_FACTORY, componentDescription.getFactory());
		ServiceRegistration serviceRegistration = context.registerService("org.osgi.service.component.ComponentFactory", factory ,properties );
		factories.put(componentDescriptionProp, serviceRegistration);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osgi.component.workqueue.WorkDispatcher#dispatchWork(int, java.lang.Object)
	 */
	public void dispatchWork(int workAction, Object workObject) {
		ComponentDescriptionProp componentDescriptionProp = (ComponentDescriptionProp) workObject;
		switch (workAction) {
//			case BUILD_FAILED :
//				main.resolver.markFailedInstance(componentDescriptionProp);
//				break;
//			case DISPOSED :
//				main.resolver.disposedComponents(componentDescriptionProps);
//				break;
		}
	}
	
	
}
