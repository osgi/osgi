package org.osgi.impl.service.upnp.cp.event;

public class SendRenewal extends GenaSocket implements Runnable {
	private String			timeout;
	private Subscription	subscription;
	private String			renewalMessage;

	public SendRenewal(Subscription subscription, String timeout) {
		this.subscription = subscription;
		this.timeout = timeout;
	}

	// This method creates the renewal message and sends it to the given host.
	public void run() {
		formRenewalMessage();
		try {
			createSocket(subscription.getHost());
			sendSocket(renewalMessage);
			parseRequest();
			updateSubscription();
			subscription.setWaiting(false);
		}
		catch (Exception e) {
			subscription.setWaiting(false);
			System.out.println(e.getMessage());
		}
	}

	// This method creates the renewal message in the renewalMessage string
	// variable.
	void formRenewalMessage() {
		renewalMessage = GenaConstants.GENA_SUBSCRIBE + " "
				+ subscription.getPublisherPath().trim() + " "
				+ GenaConstants.GENA_SERVER_VERSION + "\r\n"
				+ GenaConstants.GENA_HOST + ": " + subscription.getHost()
				+ "\r\n" + GenaConstants.GENA_SID + ": " + sid + "\r\n"
				+ GenaConstants.GENA_TIMEOUT + ": " + timeout + "\r\n\r\n";
	}

	// This method is used to update the subscription object's expirty time and
	// timeout values.
	void updateSubscription() throws Exception {
		checkSubscriptionId();
		checkTimeoutDuration();
		if (timeDuration == 0) {
			subscription.setInfinite(true);
		}
		else {
			subscription.setExpirytime(timeDuration);
		}
		subscription.setTimeout(receivedTimeout);
	}
}