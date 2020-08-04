package org.osgi.impl.service.upnp.cp.util;

import java.util.Dictionary;

// This is the interface for the applications to communicate with the control layer. 
public interface Control {
	public Dictionary<String,Object> sendControlRequest(String path,
			String host, String serviceType, String actionName,
			Dictionary<String,Object> arguments,
			boolean first) throws Exception;
}
