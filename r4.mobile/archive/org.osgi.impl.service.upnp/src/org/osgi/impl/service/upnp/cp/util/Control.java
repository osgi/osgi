package org.osgi.impl.service.upnp.cp.util;

import java.util.*;

// This is the interface for the applications to communicate with the control layer. 
public interface Control {
	public Dictionary sendControlRequest(String path, String host,
			String serviceType, String actionName, Dictionary arguments,
			boolean first) throws Exception;
}
