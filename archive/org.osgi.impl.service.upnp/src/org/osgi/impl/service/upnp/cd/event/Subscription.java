package org.osgi.impl.service.upnp.cd.event;

// This class acts as a repository for all the subscriptions. When ever a subscription is accepted 
// by the publisher , an subscription object is created and stored along with sid. This object
// is used for storing all the information about a subscription, renewal. 
public class Subscription {
	// unique service ID provided by the service vendor.
	private String	subscriptionId;
	private String	publisherpath;
	private long	expiryTime;
	private String	time;
	boolean			active		= false;
	private String	host;
	private String	callback;
	private int		eventKey	= -1;
	private boolean	infinite	= false;
	private String	serviceid;

	// Constructor which is used for intializing all the variables.
	public Subscription(String subscriptionId, String publisherpath, long t,
			String time, String host, String callback) {
		this.subscriptionId = subscriptionId;
		this.publisherpath = publisherpath;
		this.expiryTime = t;
		this.time = time;
		this.host = host;
		this.callback = callback;
	}

	// Returns the publisher path
	String getPublisherPath() {
		return publisherpath;
	}

	// Returns the service id.
	String getSubscriptionId() {
		return subscriptionId;
	}

	// Returns timer associated with this subscription
	long getExpirytime() {
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

	// Sets the timeout variable to a new value
	void setTimeout(String timeout) {
		time = timeout;
	}

	// Sets the expirytime for this subscription
	void setExpirytime(long expirytime) {
		expiryTime = expirytime;
	}

	// Returns the callback URL associated with this subscription object.
	String getCallbackURL() {
		return callback;
	}

	// This function sets the event key with an increment of 1. If reached the
	// max . value , ie
	// 4 bytes value, resets back to 1.
	void setEventkey() {
		if (eventKey >= 2147483647) {
			eventKey = 1;
		}
		else {
			++eventKey;
		}
	}

	// Returns the evetn key
	int getEventkey() {
		return eventKey;
	}

	// sets the infinite value to true
	void setInfinite(boolean value) {
		infinite = value;
	}

	// Returns the infinite value
	boolean getInfinite() {
		return infinite;
	}

	// sets the service id for this subscription
	void setServiceId(String serviceIdentifier) {
		serviceid = serviceIdentifier;
	}

	// Returns device udn
	String getServiceId() {
		return serviceid;
	}
}
