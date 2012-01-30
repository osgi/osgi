package org.osgi.impl.service.dmt.dispatcher;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.dmt.DmtAdminActivator;
import org.osgi.impl.service.dmt.EventDispatcher;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.MountPoint;

/**
 * Implementation of the MountPoint interface.
 * Instances of this object are created to be passed to plugins that implement
 * the MountPlugin interface. They provide the plugins with methods to 
 * post internal events and to figure out their real mounted path in the tree 
 * (incl. list indexes etc.).   
 * @author steffen
 */
public class MountPointImpl implements MountPoint {
	
	private static final List<String> STANDARD_PROPS = new ArrayList<String>();
	private String[] path;
	private String uri;
	private Bundle pluginBundle;
	private EventDispatcher eventDispatcher;

	static {
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_NEW_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_SESSION_ID );
	}
	
	/**
	 * creates a new MountPointImpl
	 * @param path ... the absolute mount path of a plugin in the tree
	 * @param pluginBundle ... the Bundle that has registered the plugin
	 */
	public MountPointImpl( String[] path, Bundle pluginBundle ) {
		this.path = path;
		this.uri = Uri.toUri(path);
		this.pluginBundle = pluginBundle;
		// individual eventDispatcher for this MountPoint
		this.eventDispatcher = new EventDispatcher(DmtAdminActivator.getContext(), -1, pluginBundle);
	}

	public String[] getMountPath() {
		return path;
	}

	public void postEvent(String topic, String[] relativeURIs,
			Dictionary properties) {
		postEvent(topic, relativeURIs, null, properties);
		
	}

	public void postEvent(String topic, String[] relativeNodes,
			String[] newRelativeNodes, Dictionary properties) {

		Properties props = new Properties();
		if ( properties != null ) {
			Enumeration keys = properties.keys();
			while (keys.hasMoreElements()) {
				Object key = (Object) keys.nextElement();
				if ( ! STANDARD_PROPS.contains(key))
					props.put( key, properties.get(key));
			}
		}
		
		String[] nodes = relativeNodes != null ? addPrefix(uri, relativeNodes) : new String[]{};
		String[] newNodes = newRelativeNodes != null ? addPrefix(uri, newRelativeNodes) : new String[]{};

		// post this event via normal dispatching (local and EA)
		this.eventDispatcher.dispatchPluginInternalEvent(topic, nodes, newNodes, props);
	}

	
	private String[] addPrefix( String prefix, String[] uris ) {
		String[] newUris = new String[uris.length];
		for (int i = 0; i < uris.length; i++) {
			newUris[i] = prefix + "/" + uris[i];
		}
		return newUris;
	}


	public int hashCode() {
		return this.uri.hashCode();
	}
	
	public boolean equals( Object obj ) {
		if ( obj == null ) return false;
		if ( ! (obj instanceof MountPoint )) return false;
		return this.hashCode() == obj.hashCode();
	}

}
