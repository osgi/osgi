package org.osgi.impl.service.upnp.cp.util;

public interface UPnPListener {
	// This method will be called whenver a publisher sends any upnp events
	// which are meaningful to this listener.
	public void serviceStateChanged(UPnPEvent upnpevent);
}
