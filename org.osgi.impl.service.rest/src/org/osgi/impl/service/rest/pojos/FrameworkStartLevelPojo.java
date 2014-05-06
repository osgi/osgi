/*
 *	Licensed Materials - Property of IBM.
 *	(C) Copyright IBM Corporation 2011
 *	All Rights Reserved.
 *
 *	US Government Users Restricted Rights -
 *	Use, duplication or disclosure restricted by
 *	GSA ADP Schedule Contract with IBM Corporation.
 *
 *  Created by Jan S. Rellermeyer
 *  Copyright 2011 ibm.com. All rights reserved.
 */
package org.osgi.impl.service.rest.pojos;

import org.osgi.framework.startlevel.FrameworkStartLevel;

public final class FrameworkStartLevelPojo {

	private int value;
	private int initBundleStartLevel;

	public FrameworkStartLevelPojo(final FrameworkStartLevel fsl) {
		this.value = fsl.getStartLevel();
		this.value = fsl.getInitialBundleStartLevel();
	}

	public FrameworkStartLevelPojo() {

	}

	public int getStartLevel() {
		return value;
	}

	public void setStartLevel(final int sl) {
		this.value = sl;
	}

	public int getInitialBundleStartLevel() {
		return initBundleStartLevel;
	}

	public void setInitialBundleStartLevel(final int sl) {
		this.initBundleStartLevel = sl;
	}

	@Override
	public String toString() {
		return "Startlevel " + value;
	}

}
