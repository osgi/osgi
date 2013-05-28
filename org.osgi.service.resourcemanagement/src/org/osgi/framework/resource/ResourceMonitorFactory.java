/**
 * 
 */
package org.osgi.framework.resource;

/**
 * @author mpcy8647
 *
 */
public interface ResourceMonitorFactory {

	public String getType();

	public ResourceMonitor createResourceMonitor(ResourceContext resourceContext);

}
