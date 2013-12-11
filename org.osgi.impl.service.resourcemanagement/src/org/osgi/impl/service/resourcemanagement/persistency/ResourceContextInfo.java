package org.osgi.impl.service.resourcemanagement.persistency;

import java.util.List;

public class ResourceContextInfo {

	private final String name;
	private final List/* <Long> */bundleIds;

	public ResourceContextInfo(String pName, List/* <Long> */pBundleIds) {
		name = pName;
		bundleIds = pBundleIds;
	}

	public String getName() {
		return name;
	}

	public List/* <Long> */getBundleIds() {
		return bundleIds;
	}



}
