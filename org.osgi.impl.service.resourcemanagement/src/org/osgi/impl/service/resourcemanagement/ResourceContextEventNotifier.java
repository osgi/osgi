package org.osgi.impl.service.resourcemanagement;

import org.osgi.framework.BundleContext;
import org.osgi.service.resourcemanagement.ResourceContextEvent;

public interface ResourceContextEventNotifier {

	public void start(BundleContext context);

	public void stop(BundleContext context);
	
	public void notify(ResourceContextEvent event);
}