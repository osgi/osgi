/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.module;

import org.eclipse.osgi.service.resolver.BaseDescription;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.osgi.framework.Version;

/*
 * A companion to BaseDescription from the state used while resolving.
 */
public abstract class VersionSupplier {
	BaseDescription base;
	boolean dropped = false;

	VersionSupplier(BaseDescription base) {
		this.base = base;
	}

	public Version getVersion() {
		return base.getVersion();
	}

	public String getName() {
		return base.getName();
	}

	public BaseDescription getBaseDescription() {
		return base;
	}

	// returns true if this version supplier has been dropped and is no longer available as a wire
	boolean isDropped() {
		return dropped;
	}

	// sets the dropped status.  This should only be called by the VersionHashMap 
	// when VersionSuppliers are removed
	void setDropped(boolean dropped) {
		this.dropped = dropped;
	}

	/*
	 * returns the BundleDescription which supplies this VersionSupplier
	 */
	abstract public BundleDescription getBundle();

	public String toString() {
		return base.toString();
	}
}
