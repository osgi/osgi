package org.osgi.impl.service.upnp.cd.event;

import java.io.DataOutputStream;
import java.util.Hashtable;

// This class processes the unsubscribe message of the subscriber. Checks for the valid unsubscription
// header , and bif valid unsubscribes the subscription by removing the entry from the Event Registry.
public class UnsubscribeCheck extends ErrorCheck {
	private DataOutputStream	dos;
	private Hashtable			headers;

	public UnsubscribeCheck(DataOutputStream dos, Hashtable headers) {
		this.dos = dos;
		this.headers = headers;
	}

	// This method will be called by processor for processing unsubscription
	// request.Checks for the
	// valid headers and if valid, removes the entry from eventregistry.Notifies
	// event registry to
	// check whether the eventlistener associated with this subscription can be
	// unregistred from the
	// framework or not.
	public void unsubscribe() {
		if (!checkHeaders(headers)) {
			writeData(result);
			return;
		}
		if (!check_SID(headers)) {
			writeData(result);
			return;
		}
		String sid = (String) headers.get("sid");
		Subscription subscription = EventRegistry.getSubscriber(sid);
		String serviceId = subscription.getServiceId();
		EventRegistry.removeSubscriber(sid);
		subscription = null;
		writeData(GenaConstants.GENA_SERVER_VERSION + GenaConstants.GENA_OK_200);
		EventRegistry.checkForServiceId(serviceId);
	}

	// writes the data back to the client. It can be an error message or a valid
	// unsubscription
	// response message.
	void writeData(String message) {
		try {
			dos.writeBytes(message);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
		}
	}
}