/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
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
	private static final List<String> VALID_TOPICS_WITHOUT_NEWNODES = new ArrayList<String>();
	private static final List<String> VALID_TOPICS_WITH_NEWNODES = new ArrayList<String>();
	private Plugin< ? >					plugin;
	private String[] path;
	private String uri;
	@SuppressWarnings("unused")
	private Bundle pluginBundle;
	private EventDispatcher eventDispatcher;

	static {
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_NEW_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_SESSION_ID );
		
		VALID_TOPICS_WITHOUT_NEWNODES.add( DmtConstants.EVENT_TOPIC_ADDED );
		VALID_TOPICS_WITHOUT_NEWNODES.add( DmtConstants.EVENT_TOPIC_DELETED );
		VALID_TOPICS_WITHOUT_NEWNODES.add( DmtConstants.EVENT_TOPIC_REPLACED );

		VALID_TOPICS_WITH_NEWNODES.add( DmtConstants.EVENT_TOPIC_RENAMED );
		VALID_TOPICS_WITH_NEWNODES.add( DmtConstants.EVENT_TOPIC_COPIED );
	}
	
	/**
	 * creates a new MountPointImpl
	 * @param plugin ... the associated plugin (required to check validity of this MP)
	 * @param path ... the absolute mount path of a plugin in the tree
	 * @param pluginBundle ... the Bundle that has registered the plugin
	 */
	public MountPointImpl(Plugin< ? > plugin, String[] path,
			Bundle pluginBundle) {
		this.plugin = plugin;
		this.path = path;
		this.uri = Uri.toUri(path);
		this.pluginBundle = pluginBundle;
		// individual eventDispatcher for this MountPoint
		this.eventDispatcher = new EventDispatcher(DmtAdminActivator.getContext(), -1, pluginBundle);
	}

	@Override
	public String[] getMountPath() {
		return path;
	}

	@Override
	public void postEvent(String topic, String[] relativeURIs,
			Dictionary<String, ? > properties) {
		postEvent(topic, relativeURIs, null, properties);
	}

	@Override
	public void postEvent(String topic, String[] relativeNodes,
			String[] newRelativeNodes, Dictionary<String, ? > properties) {

		// ignore, if associated plugin is already closed
		if ( plugin.closed )
			return;
		
		if ( ! VALID_TOPICS_WITHOUT_NEWNODES.contains(topic) && ! VALID_TOPICS_WITH_NEWNODES.contains(topic))
			throw new IllegalArgumentException( "Topic: '" + topic + "' is invalid!");
		
		Properties props = new Properties();
		if ( properties != null ) {
			Enumeration<String> keys = properties.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				if ( ! STANDARD_PROPS.contains(key))
					props.put( key, properties.get(key));
			}
		}
		// check for invalid nodes/newnodes values
		if ( relativeNodes != null )
			for (String node : relativeNodes)
				if ( node == null )
					throw new IllegalArgumentException( "null values are not allowed as element of nodes.");

		if ( newRelativeNodes != null ) {
			if ( ! VALID_TOPICS_WITH_NEWNODES.contains(topic))
				throw new IllegalArgumentException( "The paramenter newRelativeNodes is not allowed for topic: " + topic);
				
			for (String node : newRelativeNodes)
				if ( node == null )
					throw new IllegalArgumentException( "null values are not allowed as elements of newnodes.");
		}
		
		String[] nodes = relativeNodes != null ? addPrefix(uri, relativeNodes) : new String[]{};
		String[] newNodes = newRelativeNodes != null ? addPrefix(uri, newRelativeNodes) : new String[]{};

		if ( VALID_TOPICS_WITH_NEWNODES.contains(topic))
			// must have same size as relativeNodes
			if ( newNodes.length != nodes.length )
				throw new IllegalArgumentException( "newRelativeNodes must have same size as relativeNodes.");

		
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


	@Override
	public int hashCode() {
		return this.uri.hashCode();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( obj == null ) return false;
		if ( ! (obj instanceof MountPoint )) return false;
		return this.hashCode() == obj.hashCode();
	}

}
