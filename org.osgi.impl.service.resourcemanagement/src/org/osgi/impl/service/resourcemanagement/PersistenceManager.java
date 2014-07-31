
package org.osgi.impl.service.resourcemanagement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.impl.service.resourcemanagement.persistency.Persistence;
import org.osgi.impl.service.resourcemanagement.persistency.PersistenceImpl;
import org.osgi.impl.service.resourcemanagement.persistency.ResourceContextInfo;
import org.osgi.service.resourcemanagement.ResourceContext;
import org.osgi.service.resourcemanagement.ResourceManager;

/**
 * This class is used to handle the ResourceContext persistence for the
 * ResourceManager. It handles the restoring of the ResourceContext and the
 * 
 * @author mpcy8647
 * 
 */
public class PersistenceManager implements BundleListener {

	/**
	 * read/write a json file for ResourceContext's persistence
	 */
	private final Persistence		persistence;

	/**
	 * bundle context.
	 */
	private final BundleContext		bundleContext;

	/**
	 * this map contains bundles which has not been resolved during the
	 * restoring phase.
	 */
	private final Map				unresolvedBundles;

	/**
	 * resource manager instance.
	 */
	private final ResourceManager	resourceManager;

	/**
	 * Creates a new Persistence Manager.
	 * 
	 * @param pBbundleContext bundle context
	 * @param pResourceManager resource manager
	 */
	public PersistenceManager(BundleContext pBbundleContext,
			ResourceManager pResourceManager) {
		this.bundleContext = pBbundleContext;
		persistence = new PersistenceImpl();
		unresolvedBundles = new Hashtable();
		resourceManager = pResourceManager;

	}

	/**
	 * This method is called whenever a bundle changes its states.
	 */
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.INSTALLED) {
			// a new bundle has been installed
			// check if an entry exists into the unresolvedBundles map
			// for this new bundle
			long bundleId = event.getBundle().getBundleId();
			Long bundleIdLong = new Long(bundleId);
			String resourceContextName = (String) unresolvedBundles
					.get(bundleIdLong);
			if (resourceContextName != null) {
				// retrieves the ResourceContext based on resourceContextName
				ResourceContext resourceContext = resourceManager
						.getContext(resourceContextName);
				if (resourceContext != null) {
					try {
						resourceContext.addBundle(bundleId);
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}

		} else
			if (event.getType() == BundleEvent.UNINSTALLED) {
				// a bundle has been uninstalled
				// this case should not be used
			}

	}

	/**
	 * Restore the persisted ResourceContexts. This method also makes this
	 * instance as a BundleListener.
	 */
	public void restoreContexts() {
		ResourceContextInfo[] rcis = persistence.load(bundleContext);

		// iterate over the loaded ResourceContextInfo
		for (int i = 0; i < rcis.length; i++) {
			ResourceContextInfo rci = rcis[i];

			// for each ResourceContextInfo, creates a new ResourceContext
			String contextName = rci.getName();
			ResourceContext resourceContext = resourceManager.createContext(
					contextName, null);

			// try to associate bundle
			List bundleIds = rci.getBundleIds();
			for (Iterator it = bundleIds.iterator(); it.hasNext();) {
				Long bundleId = (Long) it.next();
				try {
					resourceContext.addBundle(bundleId.longValue());
				} catch (RuntimeException e) {
					// adding bundle may fail if the bundle has not been
					// installed/activated as
					// the resource manager will be launched very early in the
					// framework startup phase.
					// in this case, add the bundle id into the unresolvedBundle
					// map.
					unresolvedBundles.put(bundleId, resourceContext.getName());
				}
			}

		}

		// register this instance as a BundleListener
		bundleContext.addBundleListener(this);
	}

	/**
	 * Persist as file the existing ResourceContext. Unregisters this instance
	 * as a BundleListener.
	 */
	public void persist() {
		// unregister this instance as a BundleListener
		bundleContext.removeBundleListener(this);

		// retrieve all existing ResourceContext
		ResourceContext[] resourceContexts = resourceManager.listContext();
		ResourceContextInfo[] rcis = new ResourceContextInfo[resourceContexts.length];

		// iterate over all existing ResourceContext
		for (int i = 0; i < resourceContexts.length; i++) {
			ResourceContext resourceContext = resourceContexts[i];
			String currentContextName = resourceContext.getName();

			long[] bundleIds = resourceContext.getBundleIds();
			List bundleIdsSet = new ArrayList();
			// add all associated bundle into bundleIds
			for (int j = 0; j < bundleIds.length; j++) {
				bundleIdsSet.add(bundleIds[j]);
			}

			// iterate over the unresolved bundle map
			for (Iterator it = unresolvedBundles.keySet().iterator(); it
					.hasNext();) {
				Long bundleId = (Long) it.next();
				String contextName = (String) unresolvedBundles.get(bundleId);
				if (contextName.equals(currentContextName)) {
					// add the current bundleId into bundleIdsSet
					bundleIdsSet.add(bundleId);
				}
			}

			// create a ResourceContextInfo
			ResourceContextInfo rci = new ResourceContextInfo(
					resourceContext.getName(), bundleIdsSet);
			rcis[i] = rci;

		}

		persistence.persist(bundleContext, rcis);
	}

}
