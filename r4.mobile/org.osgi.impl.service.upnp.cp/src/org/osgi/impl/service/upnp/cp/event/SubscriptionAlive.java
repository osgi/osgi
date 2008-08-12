package org.osgi.impl.service.upnp.cp.event;

import java.util.Enumeration;

public class SubscriptionAlive extends Thread {
	private boolean				active	= true;
	private EventServiceImpl	esi;

	// Constructor used for storing refereneces
	public SubscriptionAlive(EventServiceImpl esi) {
		this.esi = esi;
		setDaemon(true);
	}

	// run method of the thread which keeps on polling all the sessions for
	// checking invalid
	// sessions.Sleeps for a particular time, then again checks.
	public void run() {
		while (active) {
			long curTime = System.currentTimeMillis();
			Enumeration timeouts = EventServiceImpl.subscriberList.keys();
			for (; timeouts.hasMoreElements();) {
				String key = (String) timeouts.nextElement();
				Subscription sc = (Subscription) EventServiceImpl.subscriberList
						.get(key);
				if (!sc.getInfinite()) {
					long val = (long) sc.getExpirytime();
					if (curTime - val >= 0) {
						EventServiceImpl.subscriberList.remove(key);
					}
				}
			}
			try {
				Thread.sleep(3000);
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
