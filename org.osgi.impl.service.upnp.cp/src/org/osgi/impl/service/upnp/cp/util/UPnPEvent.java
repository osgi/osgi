package org.osgi.impl.service.upnp.cp.util;

import java.util.Hashtable;

// This event class provides a way to to identify the notify messages. 
// When ever a notification comes, this event class will be fired to all the suscribers.
public class UPnPEvent {
	public static final int	UPNP_SUBSCRIBE		= 1;
	public static final int	UPNP_RENEW			= 2;
	public static final int	UPNP_NOTIFY			= 4;
	public static final int	UPNP_UNSUBSCRIBE	= 3;
	public static final int	UPNP_TIME_EXPIRED	= 5;
	private int				type;
	private String			timeout;
	private Hashtable		statevariables;
	private String			serviceId;

	public UPnPEvent(int state, String serviceId, String timeout,
			Hashtable states) {
		this.type = state;
		this.timeout = timeout;
		this.statevariables = states;
		this.serviceId = serviceId;
	}

	// Type of the message(UPNP_SUBSCRIBE,UPNP_RENEW, UPNP_UNSUBSCRIBE,
	// UPNP_NOTIFY)
	public int getType() {
		return type;
	}

	// Returns the timeout in seconds.
	public String getTimeout() {
		return timeout;
	}

	// Returns a table containing all the changed variables and values.
	public Hashtable getList() {
		return statevariables;
	}

	public String getSubscriptionId() {
		return serviceId;
	}
}
