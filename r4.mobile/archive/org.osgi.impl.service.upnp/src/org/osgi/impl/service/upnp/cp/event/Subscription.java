package org.osgi.impl.service.upnp.cp.event;

import org.osgi.impl.service.upnp.cp.util.*;

public class Subscription {
	private String				subscriptionId;
	private String				publisherpath;
	private long				expiryTime;
	private String				time;
	boolean						active			= false;
	private UPnPListener		listener;
	private String				host;
	private EventServiceImpl	esi;
	private int					eventKey		= 0;
	boolean						initialEvent	= true;
	private boolean				infinite		= false;
	boolean						waiting			= false;

	// Constructor which is used for intializing all the variables.
	public Subscription(String subscriptionId, String publisherpath, long t,
			String time, UPnPListener listener, String host) {
		this.subscriptionId = subscriptionId;
		this.publisherpath = publisherpath;
		this.expiryTime = t;
		this.time = time;
		this.listener = listener;
		this.host = host;
	}

	// Returns the publisher path
	String getPublisherPath() {
		return publisherpath;
	}

	// Returns the service id.
	synchronized String getSubscriptionId() {
		return subscriptionId;
	}

	// Returns timer associated with this subscription
	synchronized long getExpirytime() {
		return expiryTime;
	}

	// Returns the timeout value assigned for this subscription
	public String getTimeout() {
		return time;
	}

	// This method is used to activate this subscription object
	void setActive(boolean res) {
		active = res;
	}

	// Returns true if this subsctiption object is a valid one else returns
	// false.
	boolean getActive() {
		return active;
	}

	// Returns the host variable
	String getHost() {
		return host;
	}

	// Returns the UPnP Listener object
	UPnPListener getListener() {
		return listener;
	}

	// Sets the timeout variable to a new value
	void setTimeout(String timeout) {
		time = timeout;
	}

	// Sets the timer with new timer obejct
	void setExpirytime(long expirytime) {
		expiryTime = expirytime;
	}

	// Removes the listener from this subsctiption object
	void removeListener() {
		listener = null;
	}

	// Returns the event key
	int getEventkey() {
		return eventKey;
	}

	// Returns the event key
	void setEventkey(int key) {
		eventKey = key;
	}

	// Returns the event key
	boolean getInitialEvent() {
		return initialEvent;
	}

	// Returns the event key
	void setInitialEvent(boolean res) {
		initialEvent = res;
	}

	void setInfinite(boolean value) {
		infinite = value;
	}

	synchronized boolean getInfinite() {
		return infinite;
	}

	synchronized boolean waiting() {
		return waiting;
	}

	synchronized void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}
}
