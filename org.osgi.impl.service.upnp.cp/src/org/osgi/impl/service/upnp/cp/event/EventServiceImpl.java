package org.osgi.impl.service.upnp.cp.event;

import java.util.*;
import org.osgi.impl.service.upnp.cp.description.*;
import org.osgi.impl.service.upnp.cp.util.*;

public class EventServiceImpl implements EventService {
	static Hashtable		subscriberList	= null;
	private Subscription	theSubscription;
	private Hashtable		headers;
	private UPnPController	controller;
	private GenaConstants	gc;
	public static boolean	initial_seq_not_zero;
	public static boolean	initial_seq_no_increment;
	public static boolean	initial_seq_too_big;

	public EventServiceImpl(UPnPController contr, String IP) {
		controller = contr;
		subscriberList = new Hashtable();
		gc = new GenaConstants(IP);
	}

	// This method implements the subscribe method defined in Event Service API.
	// Once the subscription is accepted by the Controlled Device,
	// returns a subscription Object back to the caller.
	public void subscribe(String publisherpath, String hostString,
			UPnPListener listener, String timeout) throws Exception {
		if (publisherpath == null || publisherpath.trim().length() == 0) {
			throw new UPnPException("PublisherPath is null");
		}
		if (hostString == null || hostString.trim().length() == 0) {
			throw new UPnPException("host  is null");
		}
		if (listener == null) {
			throw new UPnPException("Invalid listener");
		}
		if (timeout == null || timeout.length() == 0) {
			throw new UPnPException("Request duration period is null");
		}
		Thread t = new Thread(new SendSubscription(publisherpath, hostString,
				timeout, listener));
		t.start();
	}

	// Method used for unsubscribing a subscription. If a valid subscription id
	// is passed,
	// unsubscribes from the evening layer so that no more notifications will be
	// send to that
	// subscription. If not a valid one, throws GenaException.
	public void unsubscribe(String subscriptionId) throws Exception {
		if (subscriptionId == null) {
			throw new UPnPException("service Id cannot be null");
		}
		for (Enumeration enum = subscriberList.keys(); enum.hasMoreElements();) {
			String val1 = (String) enum.nextElement();
		}
		Subscription sc = (Subscription) subscriberList.get(subscriptionId);
		if (sc == null) {
			throw new UPnPException("subscription not available for this id");
		}
		Thread t = new Thread(new SendUnsubscribe(sc));
		t.start();
	}

	// For renewing a subscription , this method is used. Accepts two arguments,
	// subscription
	// id and timeout period.
	public void renew(String subscriptionId, String timeout) throws Exception {
		if (subscriptionId == null) {
			throw new UPnPException("service Id cannot be null");
		}
		if (timeout == null || timeout.length() == 0) {
			throw new UPnPException("Request duration period is null");
		}
		Subscription sc = (Subscription) subscriberList.get(subscriptionId);
		if (sc == null) {
			throw new UPnPException("subscription expired or does not exist");
		}
		Thread t = new Thread(new SendRenewal(sc, timeout));
		t.start();
	}

	//  This method sets the port.
	public void setPort(int port) {
		gc.setPort(port);
	}

	// This method checks if the headers values are wrong. If it is wrong
	// returns the error message.
	String checkHeaders(Hashtable headerInfo) {
		String sid = (String) headerInfo.get(GenaConstants.GENA_SID);
		if (sid == null || subscriberList.get(sid) == null) {
			return GenaConstants.GENA_ERROR1;
		}
		String nt = (String) headerInfo.get(GenaConstants.GENA_NT);
		String nts = (String) headerInfo.get(GenaConstants.GENA_NTS);
		if (nt == null || nts == null) {
			return GenaConstants.GENA_ERROR2;
		}
		if (!nt.equals(GenaConstants.GENA_EVENT)
				|| !nts.equals(GenaConstants.GENA_PROP)) {
			return GenaConstants.GENA_ERROR1;
		}
		return GenaConstants.GENA_SUCESS;
	}

	//  This method checks for the sequence value from the headers.
	String checkSequence(Hashtable headers, Subscription subscription) {
		String seq = (String) headers.get(GenaConstants.GENA_SEQ);
		int newSequence;
		if (seq == null) {
			return GenaConstants.GENA_ERROR1;
		}
		try {
			newSequence = Integer.parseInt(seq);
		}
		catch (NumberFormatException e) {
			initial_seq_too_big = true;
			return GenaConstants.GENA_ERROR2;
		}
		if (subscription.getInitialEvent()) {
			if (newSequence != 0) {
				initial_seq_not_zero = true;
				return GenaConstants.GENA_ERROR2;
			}
			subscription.setEventkey(newSequence);
			subscription.setInitialEvent(false);
			return GenaConstants.GENA_SUCESS;
		}
		if ((newSequence - 1) != subscription.getEventkey()) {
			initial_seq_no_increment = true;
			return GenaConstants.GENA_ERROR2;
		}
		else {
			subscription.setEventkey(newSequence);
			subscription.setInitialEvent(false);
		}
		return GenaConstants.GENA_SUCESS;
	}

	// This method works on the CP side . When the events r being delivered from
	// the CD side,
	// Http stack receives it and passes the data to this method. This method
	// inturn notifies
	// all the registered listeners with all the changed variables.
	public String notifyListeners(Hashtable headers) {
		String result;
		result = checkHeaders(headers);
		if (!result.equals(GenaConstants.GENA_SUCESS)) {
			return result;
		}
		String sid = (String) headers.get(GenaConstants.GENA_SID);
		Subscription sc = (Subscription) subscriberList.get(sid.trim());
		if (sc == null) {
			return GenaConstants.GENA_ERROR3;
		}
		result = checkSequence(headers, sc);
		if (!result.equals(GenaConstants.GENA_SUCESS)) {
			return result;
		}
		UPnPListener ulr = sc.getListener();
		String time = sc.getTimeout();
		String xml = (String) headers.get(GenaConstants.GENA_BODY);
		Hashtable statevariables = getStateVariables(xml);
		UPnPEvent e = new UPnPEvent(UPnPEvent.UPNP_NOTIFY, sid, time,
				statevariables);
		new NotifyListeners(ulr, e).start();
		return GenaConstants.GENA_RESOK;
	}

	// This class notifies all the client with the UPnPEvent object by calling
	// serviceStateChanged method.
	public class NotifyListeners extends Thread {
		UPnPListener	listener;
		UPnPEvent		upnpEvent;

		// This constructor creates the NotifyListener object.
		public NotifyListeners(UPnPListener listener, UPnPEvent upnpEvent) {
			this.listener = listener;
			this.upnpEvent = upnpEvent;
		}

		// This method calls the serviceStateChanged from all the subscribed
		// listeners.
		public void run() {
			listener.serviceStateChanged(upnpEvent);
		}
	}

	// This method returns all the state variables from the given xml document.
	Hashtable getStateVariables(String xml) {
		Document doc = null;
		Hashtable values = new Hashtable();
		try {
			doc = new Document(xml, false, Description.bcd);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Vector v = doc.getElementsByTagName("e:property");
		for (int i = 0; i < v.size(); i++) {
			Element ele1 = (Element) v.elementAt(i);
			if (ele1.hasMoreElements()) {
				Vector ve2 = ele1.getAllElements();
				for (int j = 0; j < ve2.size(); j++) {
					Element ele2 = (Element) ve2.elementAt(j);
					values.put(ele2.getName(), ele2.getValue());
				}
			}
		}
		return values;
	}
}
