/**
 * 
 */

package org.osgi.service.resource;


/**
 * A ResourceMonitorFactory are OSGI services used to create ResourceMonitor
 * instance. These factories should only be used by ResourceManager singleton or
 * authorities.
 * 
 * @author mpcy8647
 * 
 */
public interface ResourceMonitorFactory {

	/**
	 * Return the type of ResourceMonitor instance this factory is able to
	 * create.
	 * 
	 * @return factory type
	 */
	public String getType();

	/**
	 * Create a new ResourceMonitor instance. This instance is associated with
	 * the ResourceContext instance provided as argument (
	 * {@link ResourceContext#addResourceMonitor(ResourceMonitor)} is called by
	 * the factory). The newly ResourceMonitor instance is disabled. It can be
	 * enabled by calling {@link ResourceMonitor#setMonitored(boolean)}.
	 * 
	 * @param resourceContext ResourceContext instance associated with the newly
	 *        created ResourceMonitor instance
	 * @return a ResourceMonitor instance
	 */
	public ResourceMonitor createResourceMonitor(ResourceContext resourceContext);

}
