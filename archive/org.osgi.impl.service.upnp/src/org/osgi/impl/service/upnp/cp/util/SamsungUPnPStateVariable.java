package org.osgi.impl.service.upnp.cp.util;

import org.osgi.service.upnp.*;

public abstract interface SamsungUPnPStateVariable extends UPnPStateVariable {
	public abstract void setChangedValue(String value);

	public abstract String getChangedValue();
}