package org.osgi.impl.service.upnp.cd.event;

import java.io.*;
import java.net.*;
import java.util.*;

// This Thread class sends events (notifications) for a state change of a variable 
// to all its subscribers. creats an xml format of the statevariables and send it to 
// all the callback URLs.
class SendEvents extends Thread {
	private Hashtable			stateVariables;
	private String				callback;
	private Subscription		sc;
	private URL					eventUrl;
	private String				message;
	private DataOutputStream	dos;
	private BufferedInputStream	in;
	private String				host;
	private int					port;

	public SendEvents(Hashtable stateVariables, Subscription sc) {
		this.stateVariables = stateVariables;
		this.callback = sc.getCallbackURL();
		this.sc = sc;
	}

	// Run method of the thread. converts the statevariables to the respective
	// xml format and
	// if mulitiple callbacks are mentioned, sends the output to all the
	// callbacks.
	public void run() {
		String xml = convertXml(stateVariables);
		StringTokenizer st = new StringTokenizer(callback, "'<','>'");
		boolean result;
		for (; st.hasMoreTokens();) {
			String val = st.nextToken();
			result = notifySubscribers(val, sc, xml);
			if (result) {
				break;
			}
		}
	}

	// This method does the notification part . This method will be called for
	// each and every subscribers . This method sends all the subscriptin
	// messages using tcp/ip socket.
	boolean notifySubscribers(String callback, Subscription subscription,
			String xml) {
		String xmlDescription = xml;
		Socket eventSocket;
		try {
			try {
				eventSocket = createSocket(callback);
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			in = new BufferedInputStream(eventSocket.getInputStream());
			dos = new DataOutputStream(new BufferedOutputStream(eventSocket
					.getOutputStream(), 1024));
			formEventMessage(subscription, xmlDescription.length());
			dos.writeBytes(message);
			dos.writeBytes(xmlDescription);
			dos.flush();
			eventSocket.setSoTimeout(30 * 1000);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	// This method forms the Gena notify message.
	void formEventMessage(Subscription subscription, int content_length) {
		message = null;
		String path = eventUrl.getFile();
		subscription.setEventkey();
		if (path.trim().length() > 0) {
			message = GenaConstants.GENA_NOTIFY + " " + path + " "
					+ GenaConstants.GENA_SERVER_VERSION + "\r\n";
		}
		else {
			message = GenaConstants.GENA_NOTIFY + " " + callback + " "
					+ GenaConstants.GENA_SERVER_VERSION + "\r\n";
		}
		message = message + GenaConstants.GENA_HOST + ": "
				+ subscription.getHost() + "\r\n"
				+ GenaConstants.GENA_CONTENT_TYPE + ": text/xml\r\n"
				+ GenaConstants.GENA_CONTENT_LENGTH + ": " + content_length
				+ "\r\n" + GenaConstants.GENA_NT + ": upnp:event\r\n"
				+ GenaConstants.GENA_NTS + ": upnp:propchange\r\n"
				+ GenaConstants.GENA_SID + ": "
				+ subscription.getSubscriptionId() + "\r\n"
				+ GenaConstants.GENA_SEQ + ": " + subscription.getEventkey()
				+ "\r\n\r\n";
	}

	// This method creates a socket and returns it. Based on the given path,
	// extracts the host
	// and the port for creating the socket.If any error occurs while creating
	// the socket,
	// throws Exception with the error message.
	Socket createSocket(String path) throws Exception {
		Socket evt_socket = null;
		try {
			eventUrl = new URL(path);
			host = eventUrl.getHost();
			port = eventUrl.getPort();
			if (port == -1) {
				port = 80;
			}
			evt_socket = new Socket(host, port);
			return evt_socket;
		}
		catch (Exception e) {
			throw e;
		}
	}

	// This method is used to convert the given statevariables to an appropriate
	// xml format
	String convertXml(Hashtable stateVariables) {
		String xml = "<e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\">\r\n";
		for (Enumeration enum = stateVariables.keys(); enum.hasMoreElements();) {
			String key = (String) enum.nextElement();
			Object val = (Object) stateVariables.get(key);
			xml = xml + "  <e:property>\r\n" + "    <" + key + ">"
					+ val.toString() + "</" + key + ">\r\n"
					+ "  </e:property>\r\n";
		}
		xml = xml + "</e:propertyset>\r\n";
		return xml;
	}
}