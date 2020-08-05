package org.osgi.impl.service.dmt.dispatcher;

import org.osgi.framework.ServiceReference;

/**
 * This is a callback interface that can be implemented to get notified,
 * when mappings have changed.
 *   
 * @author steffen
 *
 */
public interface MappingListener {
	
	void pluginMappingChanged(String pluginRoot, ServiceReference< ? > ref);

}
