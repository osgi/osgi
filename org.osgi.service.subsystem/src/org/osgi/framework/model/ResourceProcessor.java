package org.osgi.framework.model;

import org.osgi.service.subsystem.SubsystemException;

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
public interface ResourceProcessor {
	/**
	 * Process a resource according to the provided operation. The type of
	 * processing to perform is defined by the type of ResourceOperation. The 
	 * resource processor must register as a Participant with the provided 
	 * Coordination and should process the resource according to the 
	 * notifications given to the Participant. For example, if the resource 
	 * processor participant is told the Coordination failed, then the resource 
	 * processor should not perform the operation at all or should undo any 
	 * processing that has already been performed. If the resource processor is 
	 * unable to undo previous work, then it must throw an exception back to 
	 * the Coordinator service from the Participant.failed method. Must call
	 * ResourceOperation.completed in Participant.ended.
	 * @param operation
	 * @throws SubsystemException
	 */
	public void process(ResourceOperation<?> operation) throws SubsystemException;
}
