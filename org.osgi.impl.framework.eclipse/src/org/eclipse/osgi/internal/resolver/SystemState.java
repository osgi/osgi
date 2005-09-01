/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.resolver;

import org.eclipse.osgi.service.resolver.*;
import org.osgi.framework.BundleException;

public class SystemState extends StateImpl {
	synchronized public boolean addBundle(BundleDescription description) {
		if (!super.addBundle(description))
			return false;
		updateTimeStamp();
		return true;
	}

	synchronized public boolean removeBundle(BundleDescription toRemove) {
		if (!super.removeBundle(toRemove))
			return false;
		updateTimeStamp();
		return true;
	}

	synchronized public boolean updateBundle(BundleDescription newDescription) {
		if (!super.updateBundle(newDescription))
			return false;
		updateTimeStamp();
		return true;
	}

	private void updateTimeStamp() {
		if (getTimeStamp() == Long.MAX_VALUE)
			setTimeStamp(0);
		setTimeStamp(getTimeStamp() + 1);
	}

	public StateDelta compare(State state) throws BundleException {
		// we don't implement this (no big deal: the system state is private to the framework)
		throw new UnsupportedOperationException();
	}

	synchronized public StateDelta resolve() {
		StateDelta delta = super.resolve();
		if (delta.getChanges().length > 0)
			updateTimeStamp(); // resolver linkage has changed; update the timestamp
		return delta;
	}

	synchronized public StateDelta resolve(boolean incremental) {
		StateDelta delta = super.resolve(incremental);
		if (delta.getChanges().length > 0)
			updateTimeStamp(); // resolver linkage has changed; update the timestamp
		return delta;
	}

	synchronized public StateDelta resolve(BundleDescription[] reResolve) {
		StateDelta delta = super.resolve(reResolve);
		if (delta.getChanges().length > 0)
			updateTimeStamp(); // resolver linkage has changed; update the timestamp
		return delta;
	}

	synchronized public ExportPackageDescription linkDynamicImport(BundleDescription importingBundle, String requestedPackage) {
		ExportPackageDescription result = super.linkDynamicImport(importingBundle, requestedPackage);
		if (result == null)
			return null;
		// resolver linkage has changed; update the timestamp
		updateTimeStamp();
		return result;
	}
}
