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
 
package org.eclipse.osgi.impl.service.component;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.*;
import org.osgi.service.component.*;
import org.eclipse.osgi.component.*;
import org.eclipse.osgi.component.model.*;


/**
 * A ComponentContext object is used by a Service Component to interact with it
 * execution context including locating services by reference name.
 * 
 * <p>
 * A component's implementation class may optionally implement an activate method:
 * 
 * <pre>
 * protected void activate(ComponentContext context);
 * </pre>
 * 
 * If a component implements this method, this method will be called when the
 * component is activated to provide the component's ComponentContext object.
 * 
 * <p>
 * A component's implementation class may optionally implement a deactivate
 * method:
 * 
 * <pre>
 * protected void deactivate(ComponentContext context);
 * </pre>
 * 
 * If a component implements this method, this method will be called when the
 * component is deactivated.
 * 
 * <p>
 * The activate and deactivate methods will be called using reflection and must
 * be at least protected accessible. These methods do not need to be public
 * methods so that they do not appear as public methods on the component's
 * provided service object. The methods will be located by looking through the
 * component's implementation class hierarchy for the first declaration of the
 * method. If the method is declared protected or public, the method will
 * called.
 * 
 * @version $Revision$
 */
public class ComponentContextImpl implements ComponentContext {
	
	/** The BundleContext this component is associated with */
	protected BundleContext bundleContext;
	
	/* ComponentInstance instance */
	ComponentInstance componentInstance;
	
	/* ComponentDescription */
	ComponentDescription componentDescription;
	
	/* ComponentDescription plus Properties */
	ComponentDescriptionProp componentDescriptionProp;
	
	Main main;
	

	/**
	 * Construct a ComponentContext object
	 *
	 * @param bundle The ComponentDescriptionProp we are wrapping.
	 */
	public ComponentContextImpl(Main main, BundleContext bundleContext, ComponentDescriptionProp component, ComponentInstance ci ) {
		this.componentDescriptionProp = component;
		this.componentDescription = component.getComponentDescription();
		this.componentInstance = ci;
		this.bundleContext = bundleContext;
		this.main = main;
	}


	/**
	 * Returns the component properties for this ComponentContext.
	 * 
	 * @return The properties for this ComponentContext. The properties are read
	 *         only and cannot be modified.
	 */
	public Dictionary getProperties(){
		
		Dictionary props = ((ComponentInstanceImpl)componentInstance).getProperties();
				
		if (props != null){
			Dictionary properties = componentDescriptionProp.getProperties();
			Enumeration keys = props.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				properties.put(key,props.get(key));
			}
			return properties;
		
		} else {
			return componentDescriptionProp.getProperties();
		}
	}

	/**
	 * Returns the service object for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @return A service object for the referenced service or <code>null</code> if
	 *         the reference cardinality is <code>0..1</code> or <code>0..n</code>
	 *         and no matching service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating the target service.
	 */
	public Object locateService(String name)throws ComponentException {
		
		ReferenceDescription[] references = componentDescription.getReferences();
		
		for (int i = 0; i<references.length; i++) {
			
			String referenceName = references[i].getName();
			
			//find the Reference Description with the specified name
			if(referenceName.equals(name)){
				
				//get the interface name
				String interfaceName = references[i].getInterfacename();
				
				//return the service object
				return bundleContext.getService(bundleContext.getServiceReference(interfaceName));
			}
		}
		return null;
	}

	/**
	 * Returns the service objects for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @return An array of service objects for the referenced service or
	 *         <code>null</code> if the reference cardinality is <code>0..1</code>
	 *         or <code>0..n</code> and no matching service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating a target service.
	 */
	public Object[] locateServices(String name) throws ComponentException {
		
		ReferenceDescription[] references = componentDescription.getReferences();
		
		for (int i = 0; i<references.length; i++) {
			
			String referenceName = references[i].getName();
			
					
			if(referenceName.equals(name)){
				
				//get the interface name and target 
				String interfaceName = references[i].getInterfacename();
				String target = references[i].getTarget();
								
				ServiceReference[] serviceReferences = null;
				try {
					serviceReferences = bundleContext.getServiceReferences(interfaceName,target);
				}catch (Exception e){
					throw new ComponentException(e.getMessage()); 
				}
								
				ArrayList serviceObjects = new ArrayList();
				
				//Get the service object
				for (int j=0; j<serviceReferences.length; j++){
					serviceObjects.add(bundleContext.getService(serviceReferences[j]));
				}
				
				return serviceObjects.toArray();
			}
		}
		return null;
	}

	/**
	 * Returns the BundleContext of the bundle which contains this component.
	 * 
	 * @return The BundleContext of the bundle containing this component.
	 */
	public BundleContext getBundleContext(){
		return bundleContext;
	}

	/**
	 * If the component is registered as a service using the
	 * <code>servicefactory=&quot;true&quot;</code> attribute, then this method
	 * returns the bundle using the service provided by this component.
	 * <p>
	 * This method will return <code>null</code> if the component is either:
	 * <ul>
	 * <li>Not a service, then no bundle can be using it as a service.
	 * <li>Is a service but did not specify the
	 * <code>servicefactory=&quot;true&quot;</code> attribute, then all bundles
	 * will use this component. 
	 * </ul>
	 * 
	 * @return The bundle using this component as a service or <code>null</code>.
	 */
	
	public Bundle getUsingBundle(){
		//TODO
		Bundle bundle = null;
		if (( componentDescription.getService() == null ) || (!componentDescription.getService().isServicefactory())){
			return null;
		} //else {
			//CD+P:instances
			//CD+P:registrations
			//ArrayList serviceRegistrations = main.resolver.getRegistrations(CDP);
			// ServiceRegistration.getServiceReference()
			//ServiceReference ref;
			//Bundle [] bundles = ref.getUsingBundles();
		//}
		return bundle;
			
	}
		

	/**
	 * Returns the ComponentInstance object for this component.
	 * 
	 * @return The ComponentInstance object for this component.
	 */
	public ComponentInstance getComponentInstance(){
		return componentInstance;
	}

	/**
	 * Enables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name The name of a component or <code>null</code> to indicate all
	 *        components in the bundle.
	 */
	public void enableComponent(String name){
		main.enableComponent(name, bundleContext.getBundle());
	}

	/**
	 * Disables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name The name of a component.
	 */
	public void disableComponent(String name){
		main.disableComponent(name,bundleContext.getBundle());
	}
	
	
}