package org.osgi.impl.service.upnp.cd.event;

import java.io.DataOutputStream;
import java.util.Hashtable;

// This class processes the renewal request from the subscriber. Check the respective headers
// and if its a valid request, does the renewal and returns the subscriber the renewal response
// method.
public class RenewalCheck extends ErrorCheck {
	private DataOutputStream	dos;
	private Hashtable			headers;

	// constructor for intializing variables
	public RenewalCheck(DataOutputStream dos, Hashtable headers) {
		this.dos = dos;
		this.headers = headers;
	}

	// This method does all the renewal processing. Checks for valid headers and
	// if valid
	// renews the subscription.
	public void renew() {
		if (!checkHeaders(headers)) {
			writeData(result);
			return;
		}
		if (!check_SID(headers)) {
			writeData(result);
			return;
		}
		String timeout = (String) headers.get("timeout");
		String sid = (String) headers.get("sid");
		Subscription subscription = (Subscription) EventRegistry
				.getSubscriber(sid);
		setTime(timeout, subscription);
		String message = formSubscription_Okay_Message(headers, subscription);
		writeData(message);
	}

	// This method writes the output back to the subscriber.
	void writeData(String message) {
		try {
			dos.writeBytes(message);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}