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

import org.osgi.framework.BundleException;

public class BundleExceptionPojo {

	private int typecode;
	private String message;

	public BundleExceptionPojo(final BundleException be) {
		this.typecode = be.getType();
		this.message = be.toString();
	}

	public int getTypecode() {
		return typecode;
	}

	public void setTypecode(final int typecode) {
		this.typecode = typecode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

}
