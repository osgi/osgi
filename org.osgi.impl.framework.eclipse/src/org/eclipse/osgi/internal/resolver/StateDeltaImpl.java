/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.internal.resolver;

import java.util.*;

import org.eclipse.osgi.service.resolver.*;

public class StateDeltaImpl implements StateDelta {
	private State state;
	private Map changes = new HashMap();

	public StateDeltaImpl(State state) {
		this.state = state;
	}

	public BundleDelta[] getChanges() {
		return (BundleDelta[]) changes.values().toArray(new BundleDelta[changes.size()]);
	}

	public BundleDelta[] getChanges(int mask, boolean exact) {
		List result = new ArrayList();
		for (Iterator changesIter = changes.values().iterator(); changesIter.hasNext();) {
			BundleDelta change = (BundleDelta) changesIter.next();
			if (mask == change.getType() || (!exact && (change.getType() & mask) == mask))
				result.add(change);
		}
		return (BundleDelta[]) result.toArray(new BundleDelta[result.size()]);
	}

	public State getState() {
		return state;
	}

	void recordBundleAdded(BundleDescriptionImpl added) {
		BundleDeltaImpl change = (BundleDeltaImpl) changes.get(added);
		if (change == null) {
			changes.put(added, new BundleDeltaImpl(added, BundleDelta.ADDED));
			return;
		}
		if (change.getType() == BundleDelta.REMOVED) {
			changes.remove(added);
			return;
		}
		int newType = change.getType();
		if ((newType & BundleDelta.REMOVED) != 0)
			newType &= ~BundleDelta.REMOVED;
		change.setType(newType | BundleDelta.ADDED);
		change.setBundle(added);
	}

	void recordBundleUpdated(BundleDescriptionImpl updated) {
		BundleDeltaImpl change = (BundleDeltaImpl) changes.get(updated);
		if (change == null) {
			changes.put(updated, new BundleDeltaImpl(updated, BundleDelta.UPDATED));
			return;
		}
		if ((change.getType() & (BundleDelta.ADDED | BundleDelta.REMOVED)) != 0)
			return;
		change.setType(change.getType() | BundleDelta.UPDATED);
		change.setBundle(updated);
	}

	void recordBundleRemoved(BundleDescriptionImpl removed) {
		BundleDeltaImpl change = (BundleDeltaImpl) changes.get(removed);
		if (change == null) {
			changes.put(removed, new BundleDeltaImpl(removed, BundleDelta.REMOVED));
			return;
		}
		if (change.getType() == BundleDelta.ADDED) {
			changes.remove(removed);
			return;
		}
		int newType = change.getType();
		if ((newType & BundleDelta.ADDED) != 0)
			newType &= ~BundleDelta.ADDED;
		change.setType(newType | BundleDelta.REMOVED);
	}

	void recordBundleRemovalPending(BundleDescriptionImpl removed) {
		removed.setRemovalPending(true);
		BundleDeltaImpl change = (BundleDeltaImpl) changes.get(removed);
		if (change == null) {
			changes.put(removed, new BundleDeltaImpl(removed, BundleDelta.REMOVAL_PENDING));
			return;
		}
		int newType = change.getType();
		if ((newType & BundleDelta.REMOVAL_COMPLETE) != 0)
			newType &= ~BundleDelta.REMOVAL_COMPLETE;
		change.setType(newType | BundleDelta.REMOVAL_PENDING);
	}

	void recordBundleRemovalComplete(BundleDescriptionImpl removed) {
		BundleDeltaImpl change = (BundleDeltaImpl) changes.get(removed);
		if (change == null) {
			changes.put(removed, new BundleDeltaImpl(removed, BundleDelta.REMOVAL_COMPLETE));
			return;
		}
		int newType = change.getType();
		if ((newType & BundleDelta.REMOVAL_PENDING) != 0)
			newType &= ~BundleDelta.REMOVAL_PENDING;
		change.setType(newType | BundleDelta.REMOVAL_COMPLETE);
	}

	void recordBundleResolved(BundleDescriptionImpl resolved, boolean result) {
		if (resolved.isResolved() == result)
			return;  // do not record anything if nothing has changed
		BundleDeltaImpl change = (BundleDeltaImpl) changes.get(resolved);
		int newType = result ? BundleDelta.RESOLVED : BundleDelta.UNRESOLVED;
		if (change == null) {
			change = new BundleDeltaImpl(resolved, newType);
			changes.put(resolved, change);
			return;
		}

		// new type will have only one of RESOLVED|UNRESOLVED bits set
		newType = newType | (change.getType() & ~(BundleDelta.RESOLVED | BundleDelta.UNRESOLVED));
		change.setType(newType);
		change.setBundle(resolved);
	}
}