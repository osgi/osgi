package org.osgi.impl.service.upnp.cp.event;

import java.util.Enumeration;

public class SubscriptionRenew extends Thread {
	private boolean				active	= true;
	private EventServiceImpl	esi;

	// Constructor used for storing refereneces
	public SubscriptionRenew(EventServiceImpl esi) {
		this.esi = esi;
		setDaemon(true);
	}

	// run method of the thread which keeps on polling all the sessions for
	// checking invalid
	// sessions.Sleeps for a particular time, then again checks.
	public void run() {
		while (active) {
			long curTime = System.currentTimeMillis();
			Enumeration subscriptionIds = EventServiceImpl.subscriberList
					.keys();
			for (; subscriptionIds.hasMoreElements();) {
				String subscriptionId = (String) subscriptionIds.nextElement();
				Subscription sc = (Subscription) EventServiceImpl.subscriberList
						.get(subscriptionId);
				if (!(sc.getInfinite() || sc.waiting())) {
					long val = (long) sc.getExpirytime();
					if ((val - curTime) <= 5000) {
						System.out.println("renew subscription"
								+ subscriptionId);
						try {
							sc.setWaiting(true);
							esi.renew(subscriptionId, sc.getTimeout());
						}
						catch (Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}
			try {
				Thread.sleep(5000);
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	// Method which will be called from httpserver when the server shuts down.
	// Since this threads
	// continously runs, there should be a way to come out of the loop.Sets the
	// variable to false.
	public void surrender(boolean b) {
		active = b;
	}
}
