package org.osgi.impl.service.upnp.cp.util;

import org.osgi.impl.service.upnp.cp.description.*;

public interface UPnPDeviceListener {
	public void addDevice(String uuid, RootDevice deviceinfo);

	public void removeDevice(String uuid);
}
