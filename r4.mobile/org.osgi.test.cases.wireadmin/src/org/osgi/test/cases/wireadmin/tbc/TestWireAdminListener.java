package org.osgi.test.cases.wireadmin.tbc;

import java.util.Dictionary;
import org.osgi.service.wireadmin.*;

/**
 * Used to test the correct event dispatchment within the wire admin as required
 * in the rfc
 * 
 * @author Vasil Panushev
 * @version 1.1 Aug 2005
 */
public class TestWireAdminListener implements WireAdminListener {
	private WireAdminControl	control	= null;
	private boolean				dummy	= false;
	protected boolean			called	= false;

	public TestWireAdminListener(WireAdminControl control, boolean dummy) {
		this.control = control;
		this.dummy = dummy;
	}

	/**
	 * Called whenever event is dispatched in the wire admin
	 * 
	 * @param event describes the fired event
	 */
	public void wireAdminEvent(WireAdminEvent event) {
		if (dummy) {
			control.log("dummy listener", "Dummy received an event! Error");
			return;
		}
		Wire wire = event.getWire();
		Dictionary dict = null;
		control.log("wire listener", "received event "
				+ getEventName(event.getType()));
		if (wire != null) {
			dict = event.getWire().getProperties();
			String prop = (String) dict.get("org.osgi.test.wireadmin.property");
			control.log("wire listener", "wire is "
					+ ("42".equals(prop) ? "OK" : "other than expected"));
		}
		else {
			control
					.log("wire listener",
							"event.getWire() returned null. No specific wire was responsible for the event");
		}
		if ((event.getType() & (WireAdminEvent.CONSUMER_EXCEPTION + WireAdminEvent.PRODUCER_EXCEPTION)) != 0) {
			Throwable t = event.getThrowable();
			if (t == null) {
				control.log("wire listener", "Throwable not passed! Error");
			}
			else {
				if ("testing".equals(t.getMessage()))
					control
							.log("wire listener",
									"correct Throwable passed! OK");
				else
					control.log("wire listener",
							"wrong Throwable passed! Error");
			}
		}
		called = true;
		synchronized (WireAdminControl.synch) {
			WireAdminControl.synch.notify();
		}
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