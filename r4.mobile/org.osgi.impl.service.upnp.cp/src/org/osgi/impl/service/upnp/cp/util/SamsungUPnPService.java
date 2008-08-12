package org.osgi.impl.service.upnp.cp.util;

import java.util.Dictionary;
import org.osgi.service.upnp.*;

public abstract interface SamsungUPnPService extends UPnPService {
	public abstract void setChangedValue(Dictionary vals);

	public abstract String getChangedValue(String val);
}