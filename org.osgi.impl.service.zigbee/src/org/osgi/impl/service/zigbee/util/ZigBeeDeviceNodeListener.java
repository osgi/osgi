
package org.osgi.impl.service.zigbee.util;

public interface ZigBeeDeviceNodeListener {
	public void addDevice(String uuid);

	public void removeDevice(String uuid);
}
