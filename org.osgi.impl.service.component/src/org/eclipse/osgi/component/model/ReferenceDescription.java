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

package org.eclipse.osgi.component.model;

import java.util.ArrayList;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ReferenceDescription {
	protected ComponentDescription parent;
	protected String name;
	protected String interfacename;
	protected String cardinality;
	protected String policy;
	protected String target;
	protected String bind;
	protected String unbind;
	
	//Service Objects that binded to this reference
	protected ArrayList serviceObjects = new ArrayList();

	public ReferenceDescription(ComponentDescription parent) {
		this.parent = parent;
		cardinality = "1..1";
		policy = "static";

	}

	/**
	 * @return Returns the bind.
	 */
	public String getBind() {
		return bind;
	}

	/**
	 * @param bind The bind to set.
	 */
	public void setBind(String bind) {
		this.bind = bind;
	}

	/**
	 * @return Returns the cardinality.
	 */
	public String getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality The cardinality to set.
	 */
	public void setCardinality(String cardinality) {
		// TODO validate
		this.cardinality = cardinality;
	}

	/**
	 * @return Returns the interfacename.
	 */
	public String getInterfacename() {
		return interfacename;
	}

	/**
	 * @param interfacename The interfacename to set.
	 */
	public void setInterfacename(String interfacename) {
		this.interfacename = interfacename;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the policy.
	 */
	public String getPolicy() {
		return policy;
	}

	/**
	 * @param policy The policy to set.
	 */
	public void setPolicy(String policy) {
		// TODO validate
		this.policy = policy;
	}

	/**
	 * @return Returns the target.
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target The target to set.
	 */
	public void setTarget(String target) {
		// TODO validate
		this.target = target;
	}

	/**
	 * @return Returns the unbind.
	 */
	public String getUnbind() {
		return unbind;
	}

	/**
	 * @param unbind The unbind to set.
	 */
	public void setUnbind(String unbind) {
		this.unbind = unbind;
	}

	public ComponentDescription getComponentDescription() {
		return parent;
	}
	
	public void addServiceObject(Object object)
	{
		serviceObjects.add(object);
	}
	
	public Object[] getServiceObjects()
	{
		if(serviceObjects.size() == 0)
		{
			return null;
		}
		return serviceObjects.toArray();
	}
	
	public boolean containsServiceObject(Object serviceObject)
	{
	   return serviceObjects.contains(serviceObject);	
	}
}
