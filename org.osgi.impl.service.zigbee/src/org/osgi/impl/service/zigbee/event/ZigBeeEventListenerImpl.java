
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeEventListener;
import org.osgi.service.zigbee.ZigBeeException;

/**
 * Mocked impl of ZigBeeEventListener.
 */
public class ZigBeeEventListenerImpl implements ZigBeeEventListener {

	public void notifyEvent(ZigBeeEvent event) {
		// TODO Auto-generated method stub
	}

	public void onFailure(ZigBeeException e) {
		// TODO Auto-generated method stub
	}

	public void notifyTimeOut(int timeout) {
		// TODO Auto-generated method stub
	}

}
