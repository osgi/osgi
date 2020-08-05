package org.osgi.impl.service.upnp.cp.util;

import java.util.Dictionary;

import org.osgi.service.upnp.UPnPService;

public abstract interface SamsungUPnPService extends UPnPService {
	public abstract void setChangedValue(Dictionary<String,Object> vals);

	public abstract String getChangedValue(String val);
}