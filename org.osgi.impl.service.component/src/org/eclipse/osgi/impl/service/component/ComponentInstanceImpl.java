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

import java.util.Dictionary;
import org.osgi.service.component.*;



/**
 * A ComponentInstance encapsulates an instance of a component.
 * ComponentInstances are created whenever an instance of a component is
 * created.
 * 
 * @version $Revision$
 */
public class ComponentInstanceImpl implements ComponentInstance {

	Object instance;
	Dictionary properties = null;
	
	/** ComponentInstanceImpl
	 * 
	 * @param Object instance
	 * 
	 */
	public ComponentInstanceImpl(Object instance,Dictionary properties){
		this.instance = instance;
		this.properties = properties;
		
		
	}
	
	/**
	 * Dispose of this component instance. The instance will be deactivated. If
	 * the instance has already been deactivated, this method does nothing.
	 */
	public void dispose(){
		instance = null;
		properties = null;
	}
	
	
	/**
	 * Returns the component instance. The instance has been activated.
	 * 
	 * @return The component instance or <code>null</code> if the instance has
	 *         been deactivated.
	 */
	public Object getInstance(){
		return instance;
	}
	
	/*
	 * returns the instance properties
	 */
	protected Dictionary getProperties(){
		return properties;
	}
}
