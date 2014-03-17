
package org.osgi.impl.service.zigbee.event;

import org.osgi.service.zigbee.ZCLException;
import org.osgi.service.zigbee.ZigBeeEvent;
import org.osgi.service.zigbee.ZigBeeEventListener;

/**
 * Mocked impl of ZigBeeEventListener.
 */
public class ZigBeeEventListenerImpl implements ZigBeeEventListener {

	public void notifyEvent(ZigBeeEvent event) {
		// TODO Auto-generated method stub
	}

	public void onFailure(ZCLException e) {
		// TODO Auto-generated method stub
	}

	public void notifyTimeOut(int timeout) {
		// TODO Auto-generated method stub
	}

}
