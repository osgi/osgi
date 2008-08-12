package org.osgi.impl.service.upnp.cp.event;

public class SendUnsubscribe extends GenaSocket implements Runnable {
	private Subscription	subscription;
	private String			subscriptionId;
	private String			unsubscribeMessage;

	public SendUnsubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscriptionId = subscription.getSubscriptionId();
	}

	// This method creates the unsubscribe message and sends the message to the
	// subscription host.
	public void run() {
		formUnsubscribeMessage();
		try {
			createSocket(subscription.getHost());
			sendSocket(unsubscribeMessage);
			subscriptionId = subscription.getSubscriptionId();
			EventServiceImpl.subscriberList.remove(subscriptionId);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method creates the unsubscribe message for writing to the socket.
	void formUnsubscribeMessage() {
		unsubscribeMessage = GenaConstants.GENA_UNSUBSCRIBE + " "
				+ subscription.getPublisherPath().trim() + " "
				+ GenaConstants.GENA_SERVER_VERSION + "\r\n"
				+ GenaConstants.GENA_HOST + ": " + subscription.getHost()
				+ "\r\n" + GenaConstants.GENA_SID + ": " + subscriptionId
				+ "\r\n\r\n";
	}
}
