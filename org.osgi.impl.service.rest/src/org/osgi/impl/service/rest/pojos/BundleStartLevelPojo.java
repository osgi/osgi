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

import org.osgi.framework.startlevel.BundleStartLevel;

public final class BundleStartLevelPojo {

	private int startLevel;
	private boolean activationPolicyUsed;
	private boolean persistentlyStarted;

	public BundleStartLevelPojo(final BundleStartLevel sl) {
		this.startLevel = sl.getStartLevel();
		this.activationPolicyUsed = sl.isActivationPolicyUsed();
		this.persistentlyStarted = sl.isPersistentlyStarted();
	}

	public BundleStartLevelPojo() {

	}

	public int getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(final int sl) {
		this.startLevel = sl;
	}

	public boolean getActivationPolicyUsed() {
		return activationPolicyUsed;
	}

	public void setActivationPolicyUsed(boolean activationPolicyUsed) {
		this.activationPolicyUsed = activationPolicyUsed;
	}

	public boolean isPersistentlyStarted() {
		return persistentlyStarted;
	}

	public void setPersistentlyStarted(boolean persistentlyStarted) {
		this.persistentlyStarted = persistentlyStarted;
	}

	@Override
	public String toString() {
		return "Startlevel " + startLevel;
	}

}