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

package org.eclipse.osgi.component.resolver;


import org.eclipse.osgi.component.model.*;
import org.osgi.framework.*;


/**
 *
 * Wrapper for a References Description includes eligible state
 * 
 * @version $Revision$
 */
public class Reference {
	
	/* set this to true to compile in debug messages */
	static final boolean		DEBUG	= false;
	
	boolean eligible = false;
	
	protected ReferenceDescription referenceDescription;
	
	protected String interfaceName;
		
	/**
	 * Reference object 
	 * 
	 * @param referenceDescription
	 */
	public Reference(ReferenceDescription referenceDescription){
		this.referenceDescription = referenceDescription;
	}
	
	/**
	 * resolveReferences
	 * 
	 * @param scrBundleContext
	 */
	public void resolveReferences(BundleContext scrBundleContext){
			
		ServiceReference[] serviceReferences = null;
		String interfaceName,target;
							
		interfaceName = referenceDescription.getInterfacename();
		target = referenceDescription.getTarget();
			
		//TODO
		//Check the Config Admin properties for a filter - which would overwrite the one set previously. 
		//<reference-name>.target 
		//target = properties.get(referenceDescription.getName()+".target");
		//if(target == null)
		//	target = referenceDescription.getTarget();
		if(DEBUG)
			System.out.println("Reference.resolveReferences: Required Reference = "+interfaceName);
						
		//Get all service references for this target filter
		try {	
			serviceReferences = scrBundleContext.getServiceReferences(interfaceName,null);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		//if there is no service published that this Service Component References
		if (serviceReferences == null) {
			
			//and the cardinality is 0..1 or 0..n then still ok to activate the component
			if ((referenceDescription.getCardinality().equals("0..1"))|| (referenceDescription.getCardinality().equals("0..n"))){
				eligible = true;				
			} else {
				eligible = false;
				return;
			}
		}
		eligible = true;
		return;
	}
	
	/**
	 * Return the interface name 
	 * 
	 * @return
	 */
	public String getInterfaceName()
	{
		if (interfaceName == null)
			interfaceName = referenceDescription.getInterfacename();
		return interfaceName;
	}
	
	/**
	 * Check if this Reference is resolved (Eligible)
	 * 
	 * @return true if all requirements are met for this reference  
	 */
	public boolean isEligible(){
		return eligible;
	}
	
}
