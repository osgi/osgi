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

/**
 * 
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class ReferenceDescription {

	private static final int		CARDINALITY_HIGH_DEFAULT	= 1;
	private static final int		CARDINALITY_LOW_DEFAULT		= 1;
	static final String				POLICY_DEFAULT				= "static";

	protected ComponentDescription	parent;
	protected String				name;
	protected String				interfacename;

	// Cardinality indicates the number of services, matching this
	// reference,
	// which will bind to this Service Component. Possible values are:
	// 0..1, 0..n, 1..1 (i.e. exactly one), 1..n (i.e. at least one).
	// This attribute is optional. If it is not specified, then a
	// cardinality
	// of “1..1” is used.
	protected int					cardinalityHigh;
	protected int					cardinalityLow;
	protected String				policy;
	protected String				target;
	protected String				bind;
	protected String				unbind;

	public ReferenceDescription(ComponentDescription parent) {
		this.parent = parent;

		// set defaults
		cardinalityHigh = CARDINALITY_HIGH_DEFAULT;
		cardinalityLow = CARDINALITY_LOW_DEFAULT;
		policy = POLICY_DEFAULT;
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
	public int getCardinalityHigh() {
		return cardinalityHigh;
	}

	/**
	 * @param cardinality The cardinality to set.
	 */
	public void setCardinality(String cardinality) {
		if (cardinality.equals("0..1")) {
			cardinalityLow = 0;
			cardinalityHigh = 1;
		}
		else
			if (cardinality.equals("0..n")) {
				cardinalityLow = 0;
				cardinalityHigh = 999999999; // infinite
			}
			else
				if (cardinality.equals("1..1")) {
					cardinalityLow = 1;
					cardinalityHigh = 1;
				}
				else
					if (cardinality.equals("1..n")) {
						cardinalityLow = 1;
						cardinalityHigh = 999999999;
					}
					else {
						// TODO throw exception?
					}
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

	// if the cardinality is "1..1" or "1..n" then this refernce is required
	public boolean isRequired() {
		return (cardinalityLow == 1);
	}
}
