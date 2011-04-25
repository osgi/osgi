package org.osgi.framework.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*
 * TODO 
 * (1) fill in missing Javadoc comments.
 * (2) Don't like open(). Implies there needs to be a close() as well. Maybe getContent()?
 * (3) Can this be merged with Resource in OBR?
 */

/**
 * A resource is the representation of a uniquely identified and typed data.
 * 
 * @ThreadSafe
 * @noimplement
 */
public interface Resource<C extends Capability, R extends Requirement> extends Revision<C, R> {
	/**
	 * Gets the attributes associated to this resource.These attributes may 
	 * contain information ResourceProcessors. For example, a WAR resource 
	 * could have attributes that guide the conversion to Web application 
	 * bundle, providing values for manifest headers, such as Web-ContextPath.
	 * @return The attributes associated with the resource.
	 */
	public Map<String, String> getAttributes();
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
