package org.osgi.service.subsystem.a;

import org.osgi.service.coordinator.Coordination;

/*
 * TODO 
 * (1) fill in missing Javadoc comments.
 * (2) Don't like open(). Implies there needs to be a close() as well. Maybe getContent()?
 * (3) Can this be merged with Resource in OBR?
 */

/**
 * A resource is the representation of a uniquely identified and typed data. 
 * For example, a bundle is represented as a resource with a type {@link 
 * SubsystemConstants#RESOURCE_NAMESPACE_BUNDLE RESOURCE_NAMESPACE_BUNDLE}.
 * 
 * Resources should decide whether or not it's possible to participate within
 * the provided Coordination. This decision might be based on some
 * preprocessing or immediately attempting to carry out the request. If this
 * step fails, instead of throwing an exception, a Resource should fail the
 * Coordination instead of participating and return.
 * 
 * In the typical case, a Resource will immediately attempt to carry out the
 * requested operation. If the operation fails, a Resource must not
 * participate in the Coordination but fail it instead. If the operation 
 * succeeds, a Resource must participate in the Coordination before returning.
 * Participant.ended is used for any necessary cleanup and, as the last thing,
 * a call to the appropriate ResourceListener method occurs. Participant.failed
 * is used to rollback the operation.
 * 
 * However, it may be desirable in some cases, particularly those that are
 * difficult or impossible to rollback, to not carry out the operation until it
 * is known that the Coordination has succeeded within the Participant.ended
 * method. In this case, a Resource would perform any necessary preprocessing,
 * participate in the Coordination, and return without carrying out the
 * operation. Note that if this approach is taken and the operation fails
 * during the Participant.ended method, there will be no opportunity to fail
 * the Coordination and rollback work done by other Resources.
 * 
 * @ThreadSafe
 */
public interface Resource extends ResourceDescription {
	public void install(Coordination coordination);
	
	public void start(Coordination coordination);
	
	public void stop(Coordination coordination);
	
	public void uninstall(Coordination coordination);
	
	public void update(Coordination coordination);
}
