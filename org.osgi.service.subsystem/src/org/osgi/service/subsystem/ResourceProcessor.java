package org.osgi.service.subsystem;

/*
 * TODO
 * Pull out discussion of coordinations from Javadoc and put in Resource. The
 * resource processor should process all requests immediately and throw an
 * exception if anything goes wrong.
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
	 * Process a resource during the installing phase of a Subsystem. The 
	 * resource processor must register as a Participant with the provided and 
	 * should process the resource according to the notifications given to the 
	 * Participant. For example, if the resource processor participant is told 
	 * the Coordination failed, then the resource processor should not install 
	 * the resource, or should undo any installation processing that has 
	 * already been performed. If the resource processor is unable to undo a 
	 * partial installation, then it must throw an exception back to the 
	 * Coordinator service from the Particpant.failed method.
	 * @param subsystem The subsystem to which the resource belongs.
	 * @param resource The resource to be installed.
	 * @throws SubsystemException If a problem is detected with the resource or 
	 *         coordination, for example, the resource namespace does not match 
	 *         the namespace of the resource processor.
	 */
	public void install(Subsystem subsystem, Resource resource) throws SubsystemException;
	
	/**
	 * Process a resource during the starting phase of a Subsystem. The 
	 * resource processor must register as a Participant with the provided and 
	 * should process the resource according to the notifications given to the 
	 * Participant. For example, if the resource processor participant is told 
	 * the Coordination failed, then the resource processor should not start 
	 * the resource, or should undo any start processing that has already been 
	 * performed. If the resource processor is unable to undo a partial start, 
	 * then it must throw an exception back to the Coordinator service from the 
	 * Particpant.failed method.
	 * @param subsystem The subsystem to which the resource belongs.
	 * @param resource The resource to be started.
	 * @throws SubsystemException If a problem is detected with the resource or 
	 *         coordination, for example, the resource namespace does not match 
	 *         the namespace of the resource processor.
	 */
	public void start(Subsystem subsystem, Resource resource) throws SubsystemException;
	
	/**
	 * Process a resource during the stopping phase of a Subsystem. The 
	 * resource processor must register as a Participant with the provided and 
	 * should process the resource according to the notifications given to the 
	 * Participant. For example, if the resource processor participant is told 
	 * the Coordination failed, then the resource processor should not stop the 
	 * resource, or should undo any stop processing that has already been 
	 * performed. If the resource processor is unable to undo a partial stop, 
	 * then it must throw an exception back to the Coordinator service from the 
	 * Particpant.failed method.
	 * @param subsystem The subsystem to which the resource belongs.
	 * @param resource The resource to be stopped.
	 * @throws SubsystemException If a problem is detected with the resource or 
	 *         coordination, for example, the resource namespace does not match 
	 *         the namespace of the resource processor.
	 */
	public void stop(Subsystem subsystem, Resource resource) throws SubsystemException;
	
	/**
	 * Process a resource during the uninstalling phase of a Subsystem. The 
	 * resource processor must register as a Participant with the provided and 
	 * should process the resource according to the notifications given to the 
	 * Participant. For example, if the resource processor participant is told 
	 * the Coordination failed, then the resource processor should not 
	 * uninstall the resource, or should undo any uninstallation processing 
	 * that has already been performed. If the resource processor is unable to 
	 * undo a partial uninstallation, then it must throw an exception back to 
	 * the Coordinator service from the Particpant.failed method.
	 * @param subsystem The subsystem to which the resource belongs.
	 * @param resource The resource to be uninstalled.
	 * @throws SubsystemException If a problem is detected with the resource or 
	 *         coordination, for example, the resource namespace does not match 
	 *         the namespace of the resource processor.
	 */
	public void uninstall(Subsystem subsystem, Resource resource) throws SubsystemException;
	
	/**
	 * Process a resource during the updating phase of a Subsystem. The 
	 * resource processor must register as a Participant with the provided and 
	 * should process the resource according to the notifications given to the 
	 * Participant. For example, if the resource processor participant is told 
	 * the Coordination failed, then the resource processor should not update 
	 * the resource, or should undo any update processing that has already been 
	 * performed. If the resource processor is unable to undo a partial update, 
	 * then it must throw an exception back to the Coordinator service from the 
	 * Particpant.failed method.
	 * @param subsystem The subsystem to which the resource belongs.
	 * @param resource The resource to be updated.
	 * @throws SubsystemException If a problem is detected with the resource or 
	 *         coordination, for example, the resource namespace does not match 
	 *         the namespace of the resource processor.
	 */
	public void update(Subsystem subsystem, Resource resource) throws SubsystemException;
}
