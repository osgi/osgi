package org.osgi.impl.service.upnp.cp.util;

public interface EventService {
	public void subscribe(String publisherpath, String host,
			UPnPListener listener, String timeout) throws Exception;

	public void renew(String subscriptionId, String timeout) throws Exception;

	public void unsubscribe(String id) throws Exception;
}
