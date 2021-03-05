/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.impl.service.resourcemonitoring.bundlemanagement;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.resourcemonitoring.ResourceContext;

/**
 * @author mpcy8647
 * 
 */
public class BundleManagerImpl implements BundleManager, BundleListener {

	/**
	 * the BundleManager has not been initialized. It can not be used.
	 */
	private static final int	NOT_INITIALIZED	= 0;

	/**
	 * the BundleManager is ready to be used.
	 */
	private static final int	INITIALIZED		= 1;

	/**
	 * the Bundle Manager has been destroyed and can not be used any more.
	 */
	private static final int	DESTROYED		= 2;

	/**
	 * if bundle identifier is in this list, the related bundle abject is
	 * currently lock, i.e. no other operation can be done until this bundle
	 * lock is released.
	 */
	private Map<Long,BundleLock>	bundleLocks;

	/**
	 * this map contains the association between bundle and resource contexts.
	 * if a bundle id is in this map, the bundle is associated to a resource
	 * contexts.
	 */
	private Map<Long,BundleHolder>	resourceContexts;

	/**
	 * bundle context.
	 */
	private BundleContext		bundleContext;

	private int					state			= NOT_INITIALIZED;

	/**
	 * 
	 */
	public BundleManagerImpl() {
		bundleLocks = new Hashtable<>();
		resourceContexts = new Hashtable<>();
	}

	@Override
	public void addBundleToHolder(long bundleId, BundleHolder bundleHolder)
			throws BundleManagerException {
		checkState(state, INITIALIZED);

		// acquire bundle lock
		acquireBundleLock(bundleId);

		synchronized (resourceContexts) {
			if (resourceContexts.containsKey(Long.valueOf(Long.toString(bundleId)))) {
				// release bundle lock
				releaseBundleLock(bundleId);

				// the bundle is already associated with a resource context
				throw new BundleManagerException("Bundle " + bundleId
						+ " is already associated with a resource context.");
			}

			resourceContexts.put(Long.valueOf(Long.toString(bundleId)), bundleHolder);
		}

		bundleHolder.addBundleToHolder(bundleId);

		// release bundle lock
		releaseBundleLock(bundleId);

	}

	@Override
	public void removeBundleFromHolder(long bundleId, BundleHolder bundleHolder)
			throws BundleManagerException {
		checkState(state, INITIALIZED);

		// acquire bundle lock
		acquireBundleLock(bundleId);

		synchronized (resourceContexts) {
			if (!resourceContexts.containsKey(Long.valueOf(Long.toString(bundleId)))) {
				// release bundle lock
				releaseBundleLock(bundleId);

				// the bundle is not associated to a Resource Context
				throw new BundleManagerException("Bundle " + bundleId
						+ " is not associated with a ResourceContext");
			}

			BundleHolder currentBH = resourceContexts
					.get(Long.valueOf(Long.toString(bundleId)));
			if (!currentBH.equals(bundleHolder)) {
				// release bundle lock
				releaseBundleLock(bundleId);

				// the bundle is hold by an other Resource Context
				throw new BundleManagerException("Bundle " + bundleId
						+ " is not associated with this ResourceContext");
			}

			// de-associate bundle
			resourceContexts.remove(Long.valueOf(Long.toString(bundleId)));
		}

		bundleHolder.removeBundleToHolder(bundleId);

		// release bundle lock
		releaseBundleLock(bundleId);

	}

	@Override
	public void bundleChanged(BundleEvent event) {
		// create/delete Bundle Locks
		if (event.getType() == BundleEvent.INSTALLED) {
			addBundleLock(event.getBundle().getBundleId());
		} else
			if (event.getType() == BundleEvent.UNINSTALLED) {
				long bundleId = event.getBundle().getBundleId();
				removeBundleLock(bundleId);

				// if the bundle was associated to a context
				// remove it
				synchronized (resourceContexts) {
					BundleHolder holder = resourceContexts
							.get(Long.valueOf(Long.toString(bundleId)));
					if (holder != null) {
						holder.removeBundleToHolder(bundleId);
					}
				}

			}
	}

	@Override
	public void start(BundleContext pBundleContext)
			throws BundleManagerException {
		try {
			checkState(state, NOT_INITIALIZED);
		} catch (BundleManagerException e) {
			e.printStackTrace();
		}

		bundleContext = pBundleContext;

		if (bundleContext == null) {
			throw new BundleManagerException("bundleContext is null");
		}

		bundleContext.addBundleListener(this);

		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle currentBundle = bundles[i];
			addBundleLock(currentBundle.getBundleId());
		}

		state = INITIALIZED;
	}

	@Override
	public void stop() {
		try {
			checkState(state, INITIALIZED);
		} catch (BundleManagerException e) {
			e.printStackTrace();
		}

		state = DESTROYED;

		bundleContext.removeBundleListener(this);

		// release all
		synchronized (bundleLocks) {
			bundleLocks.clear();
		}
	}

	@Override
	public ResourceContext getResourceContext(long bundleId) {
		ResourceContext resourceContext;
		synchronized (resourceContexts) {
			resourceContext = (ResourceContext) resourceContexts.get(Long.valueOf(Long.toString(bundleId)));
		}
		return resourceContext;
	}

	private void addBundleLock(long bundleId) {
		synchronized (bundleLocks) {
			if (!bundleLocks.containsKey(Long.valueOf(Long.toString(bundleId)))) {
				BundleLock bl = new BundleLock();
				bundleLocks.put(Long.valueOf(Long.toString(bundleId)), bl);
			}
		}
	}

	private void removeBundleLock(long bundleId) {
		synchronized (bundleLocks) {
			bundleLocks.remove(Long.valueOf(Long.toString(bundleId)));
		}
	}

	private void acquireBundleLock(long bundleId) throws BundleManagerException {
		synchronized (bundleLocks) {
			BundleLock bl = bundleLocks.get(Long.valueOf(Long.toString(bundleId)));
			if (bl == null) {
				throw new BundleManagerException("Bundle " + bundleId
						+ " does not exist");
			} else {
				// the next call will be blocked until the lock is released.
				bl.acquireLock();
			}
		}
	}

	private void releaseBundleLock(long bundleId) throws BundleManagerException {
		synchronized (bundleLocks) {
			BundleLock bl = bundleLocks.get(Long.valueOf(Long.toString(bundleId)));
			if (bl == null) {
				throw new BundleManagerException("BundleLock bl == null.");
			} else {
				bl.releaseLock();
			}
		}
	}

	private static void checkState(int currentState, int expectedState) throws BundleManagerException {
		if (currentState != expectedState) {
			throw new BundleManagerException("Invalid state");
		}
	}

}
