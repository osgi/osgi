
package org.osgi.impl.service.resourcemanagement.persistency;

import java.util.List;

/**
 *
 */
public class ResourceContextInfo {

	private final String	name;
//	private final List<Long>	bundleIds;
	private final List	bundleIds;

	/**
	 * @param pName
	 * @param pBundleIds list of Long of bundleIds
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
	 * @return list of Long of bundleIds.
	 */
	public List  getBundleIds() {
		return bundleIds;
	}

}
