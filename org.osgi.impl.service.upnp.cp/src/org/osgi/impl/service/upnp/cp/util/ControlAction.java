package org.osgi.impl.service.upnp.cp.util;

import java.util.Dictionary;

public interface ControlAction {
	public Dictionary<String,Object> invokeAction(String actionName,
			java.util.Dictionary<String,Object> params) throws Exception;

	public String invokeQuery(String argName);
}
