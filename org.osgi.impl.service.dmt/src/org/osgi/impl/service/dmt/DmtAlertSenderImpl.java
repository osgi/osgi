/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.dmt;

import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.dmt.api.RemoteAlertSender;
import org.osgi.service.dmt.*;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DmtAlertSenderImpl implements DmtAlertSender,
		ServiceTrackerCustomizer {
	private BundleContext	bc;
	private Vector			alertSenders;

	public DmtAlertSenderImpl(BundleContext bc) {
		this.bc = bc;
		alertSenders = new Vector();
	}

	public void sendAlert(DmtSession session, int code, DmtAlertItem[] items)
			throws DmtException {
		String serverId = session.getPrincipal();
		if (serverId != null)
			sendAlert(serverId, String.valueOf(session.getSessionId()), code,
					items);
		// TODO throw exception or silently ignore the alert in case of local sessions?
	}

	public void sendAlert(String serverId, int code, DmtAlertItem[] items)
			throws DmtException {
		sendAlert(serverId, null, code, items);
	}

	public void sendAlert(int code, DmtAlertItem[] items) throws DmtException {
		sendAlert(null, null, code, items);
	}

	private void sendAlert(String serverId, String sessionId, int code,
			DmtAlertItem[] items) throws DmtException {
		RemoteAlertSender alertSender = getAlertSender(serverId);
		if (alertSender == null) {
			if (serverId == null)
				throw new DmtException(null, DmtException.ALERT_NOT_ROUTED,
						"Remote adapter not found or is not "
								+ "unique, cannot route alert without "
								+ "server ID.");
			throw new DmtException(null, DmtException.ALERT_NOT_ROUTED,
					"Cannot find remote adapter that can send "
							+ "the alert to server '" + serverId + "'.");
		}
		try {
			alertSender.sendAlert(serverId, sessionId, code, items);
		}
		catch (Exception e) {
			String message = "Error sending remote alert";
			if (serverId != null)
				message = message + " to server '" + serverId + "'";
			if (sessionId != null)
				message = message + " for session '" + sessionId + "'";
			throw new DmtException(null, DmtException.REMOTE_ERROR, message
					+ ".", e);
		}
	}

	public Object addingService(ServiceReference serviceRef) {
		RemoteAlertSender alertSender = (RemoteAlertSender) bc
				.getService(serviceRef);
		if (alertSender == null)
			return null;
		alertSenders.add(alertSender);
		return alertSender;
	}

	public void modifiedService(ServiceReference serviceRef, Object alertSender) {
	}

	public void removedService(ServiceReference serviceRef, Object alertSender) {
		// removes the plugin and ungets the service
		alertSenders.remove(alertSender);
		bc.ungetService(serviceRef);
	}

	private RemoteAlertSender getAlertSender(String serverId) {
		if (serverId == null)
			return getAlertSender();
		Iterator i = alertSenders.iterator();
		while (i.hasNext()) {
			RemoteAlertSender alertSender = (RemoteAlertSender) i.next();
			if (alertSender.acceptServerId(serverId))
				return alertSender;
		}
		return null;
	}

	private RemoteAlertSender getAlertSender() {
		if (alertSenders.size() != 1)
			return null;
		RemoteAlertSender alertSender = (RemoteAlertSender) alertSenders
				.firstElement();
		return alertSender.acceptServerId(null) ? alertSender : null;
	}
}
