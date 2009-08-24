package org.osgi.test.cases.wireadmin.junit;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.service.wireadmin.Wire;
import org.osgi.service.wireadmin.WireAdminEvent;
import org.osgi.service.wireadmin.WireAdminListener;

/**
 * Used to test the correct event dispatchment within the wire admin as required
 * 
 * @author Vasil Panushev
 * @version 1.1 Aug 2005
 */
public class TestWireAdminListener implements WireAdminListener {
	private final WireAdminControl	wac;
	private final boolean	dummy;
	private List			valuesReceived	= new ArrayList();
	private boolean			called			= false;

	public TestWireAdminListener(WireAdminControl wac, boolean dummy) {
		this.wac = wac;
		this.dummy = dummy;
	}

	/**
	 * Called whenever event is dispatched in the wire admin
	 * 
	 * @param event describes the fired event
	 */
	public void wireAdminEvent(WireAdminEvent event) {
		Wire wire = event.getWire();
		if (wire != null) {
			// check for right test case
			if (!wac.getName().equals(
					wire.getProperties().get("org.osgi.test.wireadmin"))) {
				WireAdminControl
						.log("received an event from a different test case");
				return;
			}
		}
		try {
			int type = event.getType();
			if (dummy) {
				WireAdminControl.log("Dummy received an event! Error");
				synchronized (this) {
					valuesReceived.add(new Integer(type));
				}
				return;
			}
			WireAdminControl.log("received event " + getEventName(type));
			if (wire != null) {
				Dictionary dict = event.getWire().getProperties();
				String prop = (String) dict
						.get("org.osgi.test.wireadmin.property");
				if ("42".equals(prop)) {
					WireAdminControl.log("wire is OK");
					synchronized (this) {
						valuesReceived.add(new Integer(type));
					}
				}
				else {
					WireAdminControl.log("wire is other than expected");
				}
			}
			else {
				WireAdminControl
						.log("event.getWire() returned null. No specific wire was responsible for the event");
			}

			if ((event.getType() & (WireAdminEvent.CONSUMER_EXCEPTION | WireAdminEvent.PRODUCER_EXCEPTION)) != 0) {
				Throwable t = event.getThrowable();
				if (t == null) {
					WireAdminControl.log("Throwable not passed! Error");
					synchronized (this) {
						valuesReceived.add("no throwable received");
					}
				}
				else {
					synchronized (this) {
						valuesReceived.add(t.getMessage());
					}
					if ("testing".equals(t.getMessage())) {
						WireAdminControl.log("correct Throwable passed! OK");
					}
					else {
						WireAdminControl.log("wrong Throwable passed! Error");
					}
				}
			}
		}
		finally {
			synchronized (this) {
				called = true;
				notify();
			}
		}
	}

	synchronized void waitForCall(long timeout) throws InterruptedException {
		if (called) {
			return;
		}
		wait(timeout);
	}

	synchronized List resetValuesReceived() {
		List result = valuesReceived;
		valuesReceived = new ArrayList();
		called = false;
		return result;
	}

	private String getEventName(int type) {
		switch (type) {
			case WireAdminEvent.PRODUCER_EXCEPTION :
				return "PRODUCER_EXCEPTION";
			case WireAdminEvent.CONSUMER_EXCEPTION :
				return "CONSUMER_EXCEPTION";
			case WireAdminEvent.WIRE_CREATED :
				return "WIRE_CREATED";
			case WireAdminEvent.WIRE_UPDATED :
				return "WIRE_UPDATED";
			case WireAdminEvent.WIRE_DELETED :
				return "WIRE_DELETED";
			case WireAdminEvent.WIRE_CONNECTED :
				return "WIRE_CONNECTED";
			case WireAdminEvent.WIRE_DISCONNECTED :
				return "WIRE_DISCONNECTED";
			case WireAdminEvent.WIRE_TRACE :
				return "WIRE_TRACE";
			default :
				return "UNKNONW";
		}
	}
}