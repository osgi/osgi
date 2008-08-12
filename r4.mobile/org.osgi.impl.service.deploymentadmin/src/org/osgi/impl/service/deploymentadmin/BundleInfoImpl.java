package org.osgi.impl.service.deploymentadmin;

import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.BundleInfo;

public class BundleInfoImpl implements BundleInfo {
	
	private String  symbName;
	private Version version;
	
	BundleInfoImpl(String symbName, Version version) {
		this.symbName = symbName;
		this.version = version;
	}

	public String getSymbolicName() {
		return symbName;
	}

	public Version getVersion() {
		return version;
	}

	public String toString() {
		return "[BundleInfo SymbolicName: " + symbName + " Version: " + 
			version + "]";
	}
}
