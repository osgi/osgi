package org.osgi.impl.service.upnp.cd.event;

import java.io.DataOutputStream;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.impl.service.upnp.cd.ssdp.UPnPExporter;
import org.osgi.service.upnp.*;

// This class checks for a valid subscription request. If valid, creates a new suscription object,
// along with a unique sid and sends it back to the subscriber. 
public class SubscriptionCheck extends ErrorCheck {
	private DataOutputStream	dos;
	private Hashtable			headers;
	//private String result;
	private UPnPService			upnpservice;
	private BundleContext		context;
	private Hashtable			serviceStates;

	public SubscriptionCheck(DataOutputStream dos, Hashtable headers,
			BundleContext context) {
		this.dos = dos;
		this.headers = headers;
		this.context = context;
		serviceStates = new Hashtable();
	}

	// This mehod will be called by processor object to process the given
	// subscription. Checks
	// for all valid headers, if valid, creates a new subscription along with
	// sid, and sends it
	// back to the subscriber. Intial events will be delivered on a separate
	// thread.
	public void subscribe() {
		String publisherUrl = (String) headers.get("publisherpath");
		if (publisherUrl.startsWith("/")) {
			publisherUrl = publisherUrl.substring(1);
		}
		boolean availableCheck = checkIsAvailable(publisherUrl);
		if (!availableCheck) {
			writeData(GenaConstants.GENA_SERVER_VERSION
					+ GenaConstants.GENA_ERROR_503);
			return;
		}
		if (!checkHeaders(headers)) {
			writeData(result);
			return;
		}
		String resultStr = invalid_CALLBACK();
		if (!resultStr.equals("success")) {
			writeData(result);
			return;
		}
		String subscriptionId = null;
		int i = 0;
		while (true) {
			subscriptionId = "uuid:" + i++ + "-samsungUPnP";
			if (EventRegistry.getSubscriber(subscriptionId) == null) {
				break;
			}
		}
		Subscription subscription = createNewSubscription(subscriptionId,
				publisherUrl);
		String timeOut = (String) headers.get("timeout");
		setTime(timeOut, subscription);
		String message = formSubscription_Okay_Message(headers, subscription);
		writeData(message);
		// send initial event message????
		//System.out.println("####----> cd->event->SubscriptionCheck-> send
		// initial event message????");
		//new SendEvents(serviceStates,subscription).start();
		String serviceid = upnpservice.getId();
		int index = publisherUrl.lastIndexOf("urn");
		String udn = publisherUrl.substring(0, index);
		if (!EventRegistry.checkIsListenerAvailable(udn + serviceid)) {
			Filter filter = null;
			GenaEventListener listener = new GenaEventListener(subscriptionId,
					upnpservice);
			try {
				filter = context.createFilter("(&(" + UPnPDevice.UDN + "="
						+ udn + ")(" + UPnPService.ID + "=" + serviceid + "))");
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
			Hashtable description = new Hashtable();
			ServiceRegistration registration = null;
			description.put("upnp.filter", filter);
			try {
				registration = context.registerService(
						"org.osgi.service.upnp.UPnPEventListener", listener,
						description);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			EventRegistry.insertListeners(udn + serviceid, registration);
		}
		subscription.setServiceId(udn + serviceid);
	}

	// Checks whether the subscription eventing url is available or not. It not
	// available sends
	//  the error message back to the subscriber.
	boolean checkIsAvailable(String eventsuburl) {
		boolean check_result = false;
		Hashtable TypePair = new Hashtable();
		Hashtable VariablePair = new Hashtable();
		String variableName;
		Object variableObject;
		upnpservice = UPnPExporter.getEventEntry(eventsuburl);
		if (upnpservice != null) {
			UPnPStateVariable[] stateVariables = upnpservice
					.getStateVariables();
			for (int i = 0; i < stateVariables.length; i++) {
				if (stateVariables[i].sendsEvents()) {
					check_result = true;
					variableName = stateVariables[i].getName();
					variableObject = stateVariables[i].getDefaultValue();
					if (variableObject == null)
						serviceStates.put(variableName, "null");
					else
						serviceStates.put(variableName, variableObject);
				}
			}
		}
		return check_result;
	}

	// creates a new subscription object and stores in Event Registry table.
	Subscription createNewSubscription(String subscriptionId,
			String publisherPath) {
		String callback = (String) headers.get("callback");
		String hostString = (String) headers.get("host");
		String publisherpath = publisherPath;
		Subscription subscription = new Subscription(subscriptionId.trim(),
				publisherpath, 0, null, hostString, callback);
		subscription.setActive(true);
		EventRegistry.insertSubscriber(subscriptionId.trim(), subscription);
		return subscription;
	}

	// writes the data back to the client.
	private void writeData(String output) {
		try {
			dos.writeBytes(output);
			dos.flush();
			dos.close();
		}
		catch (Exception e) {
		}
	}

	// This method checks for valid callback urls along with notification type
	// header.
	public String invalid_CALLBACK() {
		String nt = (String) headers.get("nt");
		String callback = (String) headers.get("callback");
		if (callback == null) {
			return GenaConstants.GENA_SERVER_VERSION
					+ GenaConstants.GENA_ERROR_412;
		}
		if (nt == null) {
			return GenaConstants.GENA_SERVER_VERSION
					+ GenaConstants.GENA_ERROR_400;
		}
		if (!nt.equals("upnp:event")) {
			return GenaConstants.GENA_SERVER_VERSION
					+ GenaConstants.GENA_ERROR_412;
		}
		StringTokenizer st = new StringTokenizer(callback, "'<','>'");
		if (st.countTokens() == 0) {
			return GenaConstants.GENA_SERVER_VERSION
					+ GenaConstants.GENA_ERROR_412;
		}
		for (; st.hasMoreTokens();) {
			String token = st.nextToken();
			if (!token.startsWith("http://")) {
				return GenaConstants.GENA_SERVER_VERSION
						+ GenaConstants.GENA_ERROR_412;
			}
		}
		return "success";
	}
}
