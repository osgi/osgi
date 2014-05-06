/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2012
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2012 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

public final class BundleStatePojo {

	private int state;

	public BundleStatePojo() {
		
	}
	
	public BundleStatePojo(final int state) {
		this.state = state;
	}

	public void setState(final int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
}
