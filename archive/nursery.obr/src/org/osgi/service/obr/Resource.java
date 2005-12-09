package org.osgi.service.obr;

import java.net.URI;
import java.util.List;

import org.osgi.framework.Version;

/**
 * A resource is an abstraction of a downloadable thing, like a bundle.
 * 
 * Resources have capabilities and requirements. All a resource's 
 * requirements must be satisfied before it can be installed.
 *
 * @version $Revision$
 */
public interface Resource {
	
	/**
	 * A locally generated unique id for one Repository Admin service. This id
	 * is not persistent.
	 * 
	 * @return
	 */
	String getLocalId();
	
	/**
	 * Answer the description of the resource
	 */
	public String getDescription();

	/**
	 * Answer the license file.
	 * 
	 */
	public URI getLicense();

	/**
	 * Answer the resource name.
	 * 
	 * Name and version make this resource unique.
	 */
	public String getName();

	/**
	 * Answer the resource version.
	 */
	public Version getVersion();
	/**
	 * Answer the URI where this resource can be loaded from.
	 */
	public URI getURI();

	/**
	 * Answer the resource copyright statement.
	 */
	public String getCopyright();


	/**
	 * Answer the resource documentation.
	 * 
	 */
	public URI getDocumentation();

	/**
	 * Answer the resource source URI.
	 * 
	 */
	public URI getSource();

	/**
	 * Answer the resource download size.
	 * 
	 */
	public long getSize();
	
	Requirement [] getRequirements();
	Requirement [] getRequests();
	Requirement [] getExtends();
	Capability [] getCapabilities();
	String [] getCategories();
}
