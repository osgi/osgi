/**
 * 
 */
package org.osgi.impl.service.resourcemanagement.bundlemanagement;

import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.resourcemanagement.ResourceContext;

/**
 * @author mpcy8647
 *
 */
public class BundleManagerImpl implements BundleManager, BundleListener {

	/**
	 * the BundleManager has not been initialized. It can not be used.
	 */
	private static final int NOT_INITIALIZED = 0;

	/**
	 * the BundleManager is ready to be used.
	 */
	private static final int INITIALIZED = 1;

	/**
	 * the Bundle Manager has been destroyed and can not be used any more.
	 */
	private static final int DESTROYED = 2;

	/**
	 * if bundle identifier is in this list, the related bundle abject is
	 * currently lock, i.e. no other operation can be done until this bundle
	 * lock is released.
	 */
	private Map/* <Long, BundleLock> */bundleLocks;

	/**
	 * this map contains the association between bundle and resource contexts.
	 * if a bundle id is in this map, the bundle is associated to a resource
	 * contexts.
	 */
	private Map/* <Long, BundleHolder> */resourceContexts;


	/**
	 * bundle context.
	 */
	private BundleContext bundleContext;



	private int state = NOT_INITIALIZED;

	public BundleManagerImpl() {
		bundleLocks = new Hashtable/* <Long, BundleLock> */();
		resourceContexts = new Hashtable/* <Long, BundleHolder> */();
	}

	/* (non-Javadoc)
	 * @see org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManager#addBundleToHolder(long, org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleHolder)
	 */
	public void addBundleToHolder(long bundleId, BundleHolder bundleHolder)
			throws RuntimeException {
		checkState(state, INITIALIZED);

		// acquire bundle lock
		acquireBundleLock(bundleId);

		synchronized (resourceContexts) {
			if (resourceContexts.containsKey(bundleId)) {
				// release bundle lock
				releaseBundleLock(bundleId);

				// the bundle is already associated with a resource context
				throw new RuntimeException("Bundle " + bundleId
						+ " is already associated.");
			}

			resourceContexts.put(bundleId, bundleHolder);
		}

		bundleHolder.addBundleToHolder(bundleId);

		// release bundle lock
		releaseBundleLock(bundleId);

	}

	/* (non-Javadoc)
	 * @see org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleManager#removeBundleFromHolder(long, org.osgi.impl.service.resourcemanagement.bundlemanagement.BundleHolder)
	 */
	public void removeBundleFromHolder(long bundleId, BundleHolder bundleHolder)
			throws RuntimeException {
		checkState(state, INITIALIZED);

		// acquire bundle lock
		acquireBundleLock(bundleId);

		synchronized (resourceContexts) {
			if (!resourceContexts.containsKey(bundleId)) {
				// release bundle lock
				releaseBundleLock(bundleId);

				// the bundle is not associated to a Resource Context
				throw new RuntimeException("Bundle " + bundleId
						+ " is not associated with a ResourceContext");
			}

			BundleHolder currentBH = (BundleHolder) resourceContexts
					.get(bundleId);
			if (!currentBH.equals(bundleHolder)) {
				// release bundle lock
				releaseBundleLock(bundleId);

				// the bundle is hold by an other Resource Context
				throw new RuntimeException("Bundle " + bundleId
						+ " is not associated with this ResourceContext");
			}

			// de-associate bundle
			resourceContexts.remove(bundleId);
		}

		bundleHolder.removeBundleToHolder(bundleId);

		// release bundle lock
		releaseBundleLock(bundleId);

	}



	public void bundleChanged(BundleEvent event) {
		// create/delete Bundle Locks
		if (event.getType() == BundleEvent.INSTALLED) {
			addBundleLock(event.getBundle().getBundleId());
		} else if (event.getType() == BundleEvent.UNINSTALLED) {
			long bundleId = event.getBundle().getBundleId();
			removeBundleLock(bundleId);
			
			// if the bundle was associated to a context
			// remove it
			synchronized (resourceContexts) {
				BundleHolder holder = (BundleHolder) resourceContexts
						.get(bundleId);
				if (holder != null) {
					holder.removeBundleToHolder(bundleId);
				}
			}
			
		}
	}

	public void start(BundleContext pBundleContext)
			throws RuntimeException {
		

		checkState(state, NOT_INITIALIZED);

		bundleContext = pBundleContext;

		if (bundleContext == null) {
			throw new RuntimeException("bundleContext is null");
		}

		bundleContext.addBundleListener(this);

		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle currentBundle = bundles[i];
			addBundleLock(currentBundle.getBundleId());
		}

		state = INITIALIZED;

	}

	public void stop() {

		checkState(state, INITIALIZED);

		state = DESTROYED;

		bundleContext.removeBundleListener(this);

		// release all
		synchronized (bundleLocks) {
			bundleLocks.clear();
		}


	}

	public ResourceContext getResourceContext(long bundleId) {
		ResourceContext resourceContext;
		synchronized (resourceContexts) {
			resourceContext = (ResourceContext) resourceContexts.get(bundleId);
		}
		return resourceContext;
	}

	private void addBundleLock(long bundleId) {
		synchronized (bundleLocks) {
			if (!bundleLocks.containsKey(bundleId)) {
				BundleLock bl = new BundleLock();
				bundleLocks.put(bundleId, bl);
			}
		}
	}
	
	private void removeBundleLock(long bundleId) {
		synchronized (bundleLocks) {
			bundleLocks.remove(bundleId);
		}
	}

	private void acquireBundleLock(long bundleId) throws RuntimeException {
		synchronized (bundleLocks) {
			BundleLock bl = (BundleLock) bundleLocks.get(bundleId);
			if (bl == null) {
				throw new RuntimeException("Bundle " + bundleId
						+ " does not exist");
			} else {
				// the next call will be blocked until the lock is released.
				bl.acquireLock();
			}
		}
	}

	private void releaseBundleLock(long bundleId) throws RuntimeException {
		synchronized (bundleLocks) {
			BundleLock bl = (BundleLock) bundleLocks.get(bundleId);
			if (bl == null) {
				throw new RuntimeException();
			} else {
				bl.releaseLock();
			}
		}
	}

	private static void checkState(int currentState, int expectedState) throws RuntimeException{
		if (currentState != expectedState) {
			throw new RuntimeException("Invalid state");
		}
	}

}
