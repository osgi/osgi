package org.osgi.impl.service.resourcemanagement.persistency;

import java.util.Set;

public class ResourceContextInfo {

	private final String name;
	private final Set<Long> bundleIds;

	public ResourceContextInfo(String pName, Set<Long> pBundleIds) {
		name = pName;
		bundleIds = pBundleIds;
	}

	public String getName() {
		return name;
	}

	public Set<Long> getBundleIds() {
		return bundleIds;
	}



}
