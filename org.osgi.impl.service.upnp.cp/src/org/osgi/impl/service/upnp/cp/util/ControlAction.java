package org.osgi.impl.service.upnp.cp.util;

import java.util.Dictionary;

public interface ControlAction {
	public Dictionary invokeAction(String actionName,
			java.util.Dictionary params) throws Exception;

	public String invokeQuery(String argName);
}
