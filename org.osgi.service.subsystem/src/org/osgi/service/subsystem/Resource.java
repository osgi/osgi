package org.osgi.service.subsystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.osgi.framework.Version;
import org.osgi.service.coordinator.Coordination;

/*
 * TODO 
 * (1) Should the install, start, stop, uninstall, and update methods take a
 * Subsystem as a parameter or is there a one to one relationship between
 * Resource and Subsystem (i.e. the Subsystem would be passed as a constructor
 * argument)?
 * (2) Should these methods also be able to throw an exception if something
 * goes wrong during the "prepare" phase (i.e. before deciding whether or not
 * to participate in the coordination)?
 * (3) fill in missing Javadoc comments.
 */

/**
 * A resource is the representation of a uniquely identified and typed data. 
 * For example, a bundle is represented as a resource with a type {@link 
 * SubsystemConstants#RESOURCE_NAMESPACE_BUNDLE RESOURCE_NAMESPACE_BUNDLE}.
 * 
 * @ThreadSafe
 * @noimplement
 */
public interface Resource {
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
	 * Gets the symbolic name of the resource.
	 * @return The symbolic name of the resource.
	 */
	public String getSymbolicName();
	
	/**
	 * Gets the version of the resource.
	 * @return The version of the resource.
	 */
	public Version getVersion();
	
	/**
	 * 
	 * @param coordination
	 */
	public void install(Coordination coordination);
	
	/**
	 * Opens the resource for reading.
	 * @return The <code>InputStream</code> from which the resource can be read.
	 * @throws IOException If it is not possible to open the resource for 
	 *         reading.
	 */
	public InputStream open() throws IOException;
	
	/**
	 * 
	 * @param coordination
	 */
	public void start(Coordination coordination);
	
	/**
	 * 
	 * @param coordination
	 */
	public void stop(Coordination coordination);
	
	/**
	 * 
	 * @param coordination
	 */
	public void uninstall(Coordination coordination);
	
	/**
	 * 
	 * @param coordination
	 */
	public void update(Coordination coordination);
}
