package org.osgi.model.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;

/*
 * TODO 
 * (1) fill in missing Javadoc comments.
 * (2) Don't like open(). Implies there needs to be a close() as well. Maybe getContent()?
 * (3) Can this be merged with Resource in OBR?
 */

/**
 * A resource is the representation of a uniquely identified and typed data.
 * 
 * A resources can be wired together via capabilities and requirements.
 * 
 * TODO decide on identity characteristics of a revision. Given in OSGi 
 * multiple bundles can be installed with same bsn/version this cannot be
 * used as a key.
 * 
 * What then is identity of a resource? Object identity? URI (needs getter method?)
 * 
 * @param <R>
 * @param <C>
 * 
 * @ThreadSafe
 * @version $Id$
 */
public interface Resource<C extends Capability, R extends Requirement> {
	/**
	 * The unique name of the revision
	 * @return the name of the revision
	 */
	String getSymbolicName();

	/**
	 * The version of this revision.
	 * 
	 * @return the version of this revision
	 */
	Version getVersion();

	/**
	 * Returns the capabilities declared by this revision.
	 * 
	 * @param namespace The name space of the declared capabilities to return or
	 *        {@code null} to return the declared capabilities from all name
	 *        spaces.
	 * @return A list containing a snapshot of the declared
	 *         {@link Capability}s, or an empty list if this
	 *         revision declares no capabilities in the specified name space.
	 */
	List<C> getDeclaredCapabilities(String namespace);

	/**
	 * Returns the requirements declared by this bundle revision.
	 * 
	 * @param namespace The name space of the declared requirements to return or
	 *        {@code null} to return the declared requirements from all name
	 *        spaces.
	 * @return A list containing a snapshot of the declared
	 *         {@link Requirement}s, or an empty list if this bundle
	 *         revision declares no requirements in the specified name space.
	 */
	List<R> getDeclaredRequirements(String namespace);
	/**
	 * Gets the attributes associated to this resource.These attributes may 
	 * contain information ResourceProcessors. For example, a WAR resource 
	 * could have attributes that guide the conversion to Web application 
	 * bundle, providing values for manifest headers, such as Web-ContextPath.
	 * @return The attributes associated with the resource.
	 */
	public Map<String, Object> getAttributes();
	/**
	 * Gets the location of the resource.
	 * <p>
	 * The location is typically the real URL of the resource, but is not 
	 * required to be.
	 * @return The location of the resource.
	 */
	public String getLocation();
	/**
	 * Gets the namespace of the resource.
	 * @return The namespace of the resource.
	 */
	public String getNamespace();

	/**
	 * Opens the resource for reading.
	 * @return The <code>InputStream</code> from which the resource can be read.
	 * @throws IOException If it is not possible to open the resource for 
	 *         reading.
	 */
	public InputStream open() throws IOException;
}
