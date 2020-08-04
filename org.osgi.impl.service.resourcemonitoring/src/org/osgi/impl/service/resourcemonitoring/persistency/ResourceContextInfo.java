
package org.osgi.impl.service.resourcemonitoring.persistency;

import java.util.List;

/**
 *
 */
public class ResourceContextInfo {

	private final String	name;
	// private final List<Long> bundleIds;
	private final List<Long>	bundleIds;

	/**
	 * @param pName
	 * @param pBundleIds list of Long of bundleIds
	 */
	public ResourceContextInfo(String pName, List<Long> pBundleIds) {
		name = pName;
		bundleIds = pBundleIds;
	}

	/**
	 * @return name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return list of Long of bundleIds.
	 */
	public List<Long> getBundleIds() {
		return bundleIds;
	}

}
