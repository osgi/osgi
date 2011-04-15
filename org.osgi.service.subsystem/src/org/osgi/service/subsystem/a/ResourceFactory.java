package org.osgi.service.subsystem.a;

/*
 * TODO
 * (1) Perhaps pass a collection of resource contexts? There could be more than one 
 * resource of the same type. Coordinations span multiple resources anyway.
 * (2) Can this be merged with ResourceProcessor in OBR?
 */

/**
 * A ResourceProcessor processes resources from a specific namespace or 
 * namespaces (e.g. bundle). Namespaces not defined by the OSGi Alliance should 
 * use a reverse domain name scheme to avoid collision (e.g. com.acme.config).
 * <p>
 * ResourceProcessors are registered in the OSGi Service Registry. They 
 * advertise the namespaces they support using the service property 
 * osgi.resource.namespace. The type of this property is a String+.
 * <p>
 * A resource processor performs the operation corresponding to those provided 
 * by SubsystemAdmin and Subsystem that affect a subsystem's lifecycle (e.g. 
 * install and start). For example, SubsystemAdmin.install() would delegate to 
 * on a resource processor if there were any resources to install for the 
 * namespace that the resource processor supported.
 * 
 * @ThreadSafe
 */
public interface ResourceFactory {
	public Resource create(ResourceDescription description, ResourceListener listener, Subsystem subsystem) throws SubsystemException;
}
