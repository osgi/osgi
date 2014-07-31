
package org.osgi.impl.service.resourcemanagement.persistency;

import java.util.List;

/**
 *
 */
public class ResourceContextInfo {

	private final String	name;
	// List<Long> bundleIds
	private final List		bundleIds;

	/**
	 * @param pName
	 * @param pBundleIds
	 */
	public ResourceContextInfo(String pName, List pBundleIds) {
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
	 * @return list<Long> of bundleIds.
	 */
	public List getBundleIds() {
		return bundleIds;
	}

}
