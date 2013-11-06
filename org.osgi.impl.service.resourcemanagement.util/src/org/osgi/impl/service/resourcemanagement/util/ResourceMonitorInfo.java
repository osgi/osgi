package org.osgi.impl.service.resourcemanagement.util;

import org.osgi.service.resourcemanagement.monitor.MemoryMonitor;

public class ResourceMonitorInfo {

	private final String resourceContextName;
	private boolean enabled;

	public ResourceMonitorInfo(final String pResourceContextName,
			final boolean pEnabled) {
		resourceContextName = pResourceContextName;
		enabled = pEnabled;
	}

	public ResourceMonitorInfo(final MemoryMonitor memoryMonitor) {
		resourceContextName = memoryMonitor.getContext().getName();
		enabled = memoryMonitor.isEnabled();
	}
	
	public ResourceMonitorInfo(final String memoryMonitorInfoAsCsv) {
		String[] splitted = memoryMonitorInfoAsCsv.split(";");
		if (splitted != null) {
			resourceContextName = splitted[0];
			enabled = Boolean.valueOf(splitted[1]);
		} else {
			throw new NullPointerException(
					"ResourceContextName or Enabled is null");
		}

	}

	public String getResourceContextName() {
		return resourceContextName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean pEnabled) {
		enabled = pEnabled;
	}

	public String toCsv() {
		return resourceContextName + ";" + enabled;
	}

}
