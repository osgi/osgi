
package org.osgi.impl.service.resourcemanagement.bundlemanagement;

/**
 *
 */
public interface BundleHolder {

	/**
	 * @param bundleId
	 */
	void addBundleToHolder(long bundleId);

	/**
	 * @param bundleId
	 */
	void removeBundleToHolder(long bundleId);
}
