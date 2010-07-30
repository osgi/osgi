package org.osgi.impl.service.dmt.dispatcher;

import java.util.Hashtable;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import info.dmtree.MountPoint;
import info.dmtree.Uri;

public class MountPointImpl implements MountPoint {
	
	private String[] path;
	private String uri;
	private ServiceTracker eaTracker;
	
	public MountPointImpl( String[] path, ServiceTracker eaTracker ) {
		this.path = path;
		this.uri = Uri.toUri(path);
		this.eaTracker = eaTracker;
	}

	public String[] getMountPath() {
		return path;
	}

	public void postEvent(String topic, String[] relativeNodes,
			String[] newRelativeNodes) {

		EventAdmin ea = (EventAdmin) eaTracker.getService();
		if ( ea == null ) {
			System.err.println( "MountPoint: no EventAdmin found --> can't post Events on behalf of the Plugin");
			return;
		}
		
		Hashtable<String, String[]> props = new Hashtable<String, String[]>();
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
			newUris[i] = prefix + "." + uris[i];
		}
		return newUris;
	}

}
