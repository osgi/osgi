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
	public boolean addBundle(BundleDescription description) {
		if (!super.addBundle(description))
			return false;
		updateTimeStamp();
		return true;
	}

	public boolean removeBundle(BundleDescription toRemove) {
		if (!super.removeBundle(toRemove))
			return false;
		updateTimeStamp();
		return true;
	}

	public boolean updateBundle(BundleDescription newDescription) {
		if (!super.updateBundle(newDescription))
			return false;
		updateTimeStamp();
		return true;
	}

	private void updateTimeStamp() {
		if (getTimeStamp() == Long.MAX_VALUE)
			setTimeStamp(0);
		setTimeStamp(getTimeStamp()+1);
	}

	public StateDelta compare(State state) throws BundleException {
		// we don't implement this (no big deal: the system state is private to the framework)
		throw new UnsupportedOperationException();
	}

	public StateDelta resolve() {
		StateDelta delta = super.resolve();
		if (delta.getChanges().length > 0)
			updateTimeStamp(); // resolver linkage has changed; update the timestamp
		return delta;
	}
	public StateDelta resolve(boolean incremental) {
		StateDelta delta = super.resolve(incremental);
		if (delta.getChanges().length > 0)
			updateTimeStamp(); // resolver linkage has changed; update the timestamp
		return delta;
	}
	public StateDelta resolve(BundleDescription[] reResolve) {
		StateDelta delta = super.resolve(reResolve);
		if (delta.getChanges().length > 0)
			updateTimeStamp(); // resolver linkage has changed; update the timestamp
		return delta;
	}
}
