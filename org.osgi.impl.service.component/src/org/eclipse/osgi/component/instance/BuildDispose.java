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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.osgi.impl.service.component.*;
import org.eclipse.osgi.component.model.*;
import org.eclipse.osgi.component.*;
import org.osgi.service.component.*;
import org.osgi.framework.*;


/**
 * This class provides the following function
 * 
 * Build of a component instance - includes activate, bind, instantiate.
 * Dispose of a component instance - includes deactivate, unbind, unreference.
 * 
 */
public class BuildDispose {

	/* set this to true to compile in debug messages */
	static final boolean		DEBUG	= false;
	
	protected ComponentDescription componentDescription;
	protected ComponentContext componentContext;
	protected InvokeMethod invoke;
	protected BundleContext bundleContext;
	
	// Map CD+P:instances (1:n)
	protected Hashtable instanceMap;
	
	// Map CD+P:componentContext(1:1)
	protected Hashtable contextMap;
		
	// Map CD+P:service registrations
	protected Hashtable registrations;
	
	//Main SCR class
	protected Main main;
	
	/**
	 * BuildDispose - Build or Dispose of the Instance
	 * 
	 * @param main Main SCR class
	 * @param registrations - map of ComponentDescriptionProps to service registrations
	 */

	public BuildDispose(Main main, Hashtable registrations){
		instanceMap = new Hashtable();
		contextMap = new Hashtable();
		invoke = new InvokeMethod();
		this.registrations = registrations;
		this.main = main;
	}
	
	/**
	 * dispose cleanup the SCR is shutting down
	 */
	public void dispose() {
		instanceMap = null;
		contextMap = null;
		invoke = null;
		registrations = null;
		main = null;
	}
	
	/**
	 * build the ComponentInstance
	 * 
	 * @param bundleContext - bundle context of the bundle containg the Service Component
	 * @param component - Component Description with Properties Object
	 * @param instance - instance object
	 * @param properties - properties for this instance
	 * 
	 * @return ComponentInstance
	 */
	public ComponentInstance build(BundleContext bundleContext,ComponentDescriptionProp component, Object instance,Dictionary properties) throws Exception {
		
		ArrayList instances = new ArrayList();
		componentDescription = component.getComponentDescription();
		this.bundleContext = bundleContext;
		
		if(instance == null)
			instance = createInstance(componentDescription);
		
		ComponentInstance componentInstance = instantiate(instance,properties);
		
		bind(instance);
		
		activate(component, componentInstance);

		instances.add(componentInstance);
		instanceMap.put(component,instances);

		return componentInstance;
	}
	
	/** dispose of the Component Instance
	 * 
	 * @param bundleContext
	 * @param component Component Description plus properties
	 */
	public void dispose(BundleContext bundleContext, ComponentDescriptionProp component){
		
		ComponentInstance componentInstance;
		Object instance;
		
		// get all instances for this component
		ArrayList instances = (ArrayList)instanceMap.get(component);
		
		if(instances != null){
			Iterator it = instances.iterator();
			while(it.hasNext()){
				
				componentInstance = (ComponentInstance)it.next();
				instance = componentInstance.getInstance();
		
				deactivate(instance);
		
				unbind(instance);
		
				instance = null;
				componentInstance = null;
			}
		}
		instanceMap.remove(component);
	}
	
	/**
	 * Create the new Instance
	 * 
	 * @return Object instance
	 */
	public Object createInstance(ComponentDescription componentDescription) throws Exception {
		
		Object instance = null;
			
		String classname = componentDescription.getImplementation().getClassname();
		try {
			instance =  (Object) ((componentDescription.getBundle()).loadClass(classname)).newInstance();
		}catch(Exception e){
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
			throw e; //rethrow so we can eventually tell the resolver that this CD is bad
		}

		return instance;
	}
	
	/**Register Services
	 * 
	 * @param bundleContext
	 * @param componentDescriptionProp
	 * 
	 */
	public void registerServices(BundleContext bundleContext,ComponentDescriptionProp componentDescriptionProp){
		ArrayList serviceRegistrations = new ArrayList();
		
		RegisterComponentService registerService = new RegisterComponentService(this);
		serviceRegistrations = registerService.registerServices(bundleContext, componentDescriptionProp);
		registrations.put(componentDescriptionProp,serviceRegistrations);
	}
	
	/**
	 * instantiate - create the ComponentInstance
	 *
	 * @param instance 
	 */
	public ComponentInstance instantiate(Object instance,Dictionary properties){
				
		return new ComponentInstanceImpl(instance,properties);
	}
	
	/**
	 * Call the bind method for each of the Referenced Services in this Service Component
	 *
	 * @param instance 
	 */
	public void bind(Object instance) throws Exception{
		
		ReferenceDescription[] referenceDescriptions = null;
		
		//Get all the required service Reference Descriptions for this Service Component
		referenceDescriptions = componentDescription.getReferences();
			
		//call the Bind method if the Reference Description includes one
		if( referenceDescriptions.length > 0 ){
			int i=0;
			while ( i < referenceDescriptions.length ) {
				if(referenceDescriptions[i].getBind() != null){
					bindReference(referenceDescriptions[i],instance);
				}
				i++;
			}
			
		} 
	}
	
	/** 
	 * Call the bind method for this referenceDescription
	 * 
	 * @param referenceDescription
	 * @param instance 
	 * 
	 */
	public void bindReference(ReferenceDescription referenceDescription, Object instance) throws Exception{
		
		ServiceReference[] serviceReferences = null;
		int i,j;
				
		//if there is a published service, then get the ServiceObject and call bind
		try {	
			//get All Registered services using this target filter

			//TODO target not working
			//serviceReferences = bundleContext.getServiceReferences(referenceDescription.getInterfacename(),referenceDescription.getTarget());
			serviceReferences = bundleContext.getServiceReferences(referenceDescription.getInterfacename(),null);
		} catch (Exception e){
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
			throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
		}
		
		if (serviceReferences != null) {
			if((referenceDescription.getCardinality().equals("1..1")) || (referenceDescription.getCardinality().equals("0..1"))){
								
				Object serviceObject = (Object)bundleContext.getService(serviceReferences[0]);
				if(referenceDescription.getBind() != null){
					try {
						invoke.bindComponent(referenceDescription.getBind(), instance, serviceObject);
					}catch(Exception e){
						main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
						throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
					}
				}				
			} else if ((referenceDescription.getCardinality().equals("1..n")) || (referenceDescription.getCardinality().equals("0..n"))) {
				for(j = 0; j<serviceReferences.length; j++){
					if(referenceDescription.getBind() != null){
						try {
							invoke.bindComponent(referenceDescription.getBind(),instance, bundleContext.getService(serviceReferences[j]));
						}catch(Exception e){
							main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
							throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
						}
					}
				}
			}
		}	
		
	}

	/**
	 * Call the unbind method for each of the Referenced Services in this Serivce Component
	 *
	 * @param instance 
	 */
	public void unbind(Object instance) {
		
		ReferenceDescription[] referenceDescriptions = null;
		
		//Get all the required service reference descriptions for this Service Component
		referenceDescriptions = componentDescription.getReferences();
			
		//call the unBind method if the Reference Description includes one
		if( referenceDescriptions.length > 0 ){
			int i=0;
			while ( i < referenceDescriptions.length ) {
				if(referenceDescriptions[i].getUnbind() != null){
					unbindReference(referenceDescriptions[i],instance);
				}
				i++;
			}
			
		} 
	}

	/** 
	 * Call the unbind method for this Reference Description
	 * 
	 * @param referenceDescription
	 * @param instance 
	 */
	public void unbindReference(ReferenceDescription referenceDescription, Object instance){
		
		ServiceReference[] serviceReferences = null;
								
		String interfaceName = referenceDescription.getInterfacename();
		String target = referenceDescription.getTarget();
		
		//if there is a published service, then call unbind
		try {	
			
			//get All Registered services using this target filter
			serviceReferences = bundleContext.getServiceReferences(interfaceName,referenceDescription.getTarget());
		} catch (Exception e){
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
		}
		
		if (serviceReferences != null) {
			if((referenceDescription.getCardinality().equals("1..1")) || (referenceDescription.getCardinality().equals("0..1"))){
								
				Object serviceObject = (Object)bundleContext.getService(serviceReferences[0]);
				if(referenceDescription.getUnbind() != null)
					try {
						invoke.unbindComponent(referenceDescription.getUnbind(), instance, serviceObject);
					}catch(Exception e){
						main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
					}
								
			} else if ((referenceDescription.getCardinality().equals("1..n")) || (referenceDescription.getCardinality().equals("0..n"))) {
				for(int j = 0; j<serviceReferences.length; j++){
					if(referenceDescription.getUnbind() != null)
						try {
							invoke.unbindComponent(referenceDescription.getUnbind(),instance, bundleContext.getService(serviceReferences[j]));
						}catch(Exception e){
							main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
						}
					
				}
			}
		}	
		
	}
	
	/**
	 * Invoke the Serivce Component activate method
	 *
	 * @param component - ComponentDescriptionProperty object
	 * @param componentInstance - 
	 */
	public void activate(ComponentDescriptionProp component, ComponentInstance componentInstance) throws Exception{
				
		//Create ComponentContext
		componentContext = createComponentContext(component, componentInstance);
			
		//call the activate method on the Service Component
		try {
			invoke.activateComponent(componentInstance.getInstance(),componentContext);
		}catch(Exception e){
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
			throw e;//rethrow exception so resolver is eventually notified that this CDP is bad
		}
	
	}
	
	/**
	 * Invoke the Service Component deactivate method
	 *
	 * @param instance 
	 */
	public void deactivate(Object instance) {
		try {
			invoke.deactivateComponent(instance, componentContext);
		}catch(Exception e){
			main.framework.publishFrameworkEvent(FrameworkEvent.ERROR, componentDescription.getBundle(),e);
		}

	}
		
	
	/**
	 * createComponentContext
	 * 
	 * @param component
	 * @param componentInstance
	 * 
	 * @return ComponentContext
	 */
	public ComponentContext createComponentContext(ComponentDescriptionProp component, ComponentInstance componentInstance){
		ComponentContext context = new ComponentContextImpl(main, bundleContext, component, componentInstance);
		contextMap.put(component,context);
		return context;
	}

	
}
