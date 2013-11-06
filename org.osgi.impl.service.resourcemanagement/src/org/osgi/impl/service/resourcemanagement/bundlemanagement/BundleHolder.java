package org.osgi.impl.service.resourcemanagement.bundlemanagement;


public interface BundleHolder {

	void addBundleToHolder(long bundleId);

	void removeBundleToHolder(long bundleId);
}
