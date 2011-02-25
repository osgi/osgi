package org.osgi.impl.service.dmt.dispatcher;

import info.dmtree.Uri;
import info.dmtree.spi.DmtConstants;
import info.dmtree.spi.MountPoint;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class MountPointImpl implements MountPoint {
	
	private static final List<String> STANDARD_PROPS = new ArrayList<String>();
	private String[] path;
	private String uri;
	private ServiceTracker eaTracker; 

	static {
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_NEW_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_SESSION_ID );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_LIST_NODES );
		STANDARD_PROPS.add( DmtConstants.EVENT_PROPERTY_LIST_UPCOMING_EVENT );
	}
	
	public MountPointImpl( String[] path, ServiceTracker eaTracker ) {
		this.path = path;
		this.uri = Uri.toUri(path);
		this.eaTracker = eaTracker;
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

		EventAdmin ea = (EventAdmin) eaTracker.getService();
		if ( ea == null ) {
			System.err.println( "MountPoint: no EventAdmin found --> can't post Events on behalf of the Plugin");
			return;
		}
		
		Hashtable props = new Hashtable<String, String[]>();
		
		if ( properties != null ) {
			Enumeration keys = properties.keys();
			while (keys.hasMoreElements()) {
				Object key = (Object) keys.nextElement();
				if ( ! STANDARD_PROPS.contains(key))
					props.put( key, properties.get(key));
			}
		}
		
		if ( relativeNodes != null )
			props.put("nodes", addPrefix(uri, relativeNodes) );
		if ( newRelativeNodes != null )
			props.put("newnodes", addPrefix(uri, newRelativeNodes) );
		
		Event e = new Event(topic, props);
		ea.postEvent(e);
	}
	
	private String[] addPrefix( String prefix, String[] uris ) {
		String[] newUris = new String[uris.length];
		for (int i = 0; i < uris.length; i++) {
			newUris[i] = prefix + "/" + uris[i];
		}
		return newUris;
	}

	public void sendEvent(String topic, String[] relativeURIs,
			Dictionary properties) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("method not yet implemented");
	}

}
