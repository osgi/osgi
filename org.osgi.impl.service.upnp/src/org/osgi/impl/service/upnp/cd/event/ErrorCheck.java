package org.osgi.impl.service.upnp.cd.event;

import java.text.SimpleDateFormat;
import java.util.*;

// This class contains the implementation of Control interface defined in the api package.
public class ErrorCheck {
	public String	result;

	// This method will be called by the subscription check and renwal check to
	// check ,
	// headers.
	boolean checkHeaders(Hashtable headers) {
		if (headers.get("sid") != null) {
			if (headers.get("nt") != null || headers.get("callback") != null) {
				result = GenaConstants.GENA_SERVER_VERSION
						+ GenaConstants.GENA_ERROR_400;
				return false;
			}
		}
		return true;
	}

	// This method will be called by the subscription check to check ,
	// whether the request contains valid sid or not
	boolean check_SID(Hashtable headers) {
		String sid = (String) headers.get("sid");
		if (sid == null || EventRegistry.getSubscriber(sid) == null) {
			result = GenaConstants.GENA_ERROR_412;
			return false;
		}
		return true;
	}

	// This method will be called by the subscription check and renwal check to
	// check ,
	// timeoutheadr, converts it to milli seconds and will be stored with the
	// subscription object.
	void setTime(String timeOut, Subscription subscription) {
		String timeconvert = null;
		if (timeOut.startsWith("Second-") || timeOut.startsWith("Seconds-")) {
			int result = timeOut.indexOf("-");
			timeconvert = timeOut.substring(result + 1);
		}
		else
			if (timeOut.equals("infinite")) {
				timeconvert = timeOut;
			}
		boolean infinite = false;
		long newtime;
		if (timeconvert.trim().equals("infinite")) {
			newtime = 0;
			infinite = true;
		}
		else {
			newtime = new Integer(Integer.parseInt(timeconvert) * 1000)
					.longValue();
			if (newtime < 1800000) {
				timeOut = "Second-1800";
				newtime = 1800000;
			}
		}
		long timeInMillies = newtime + System.currentTimeMillis();
		subscription.setTimeout(timeOut);
		if (infinite) {
			subscription.setInfinite(true);
		}
		subscription.setExpirytime(timeInMillies);
	}

	// This method will be called by the subscription check and renwal check to
	// form ,
	// subscription okay message which will be send back to the subscribers.
	String formSubscription_Okay_Message(Hashtable headers,
			Subscription subscription) {
		String message = GenaConstants.GENA_SERVER_VERSION
				+ GenaConstants.GENA_OK_200 + "\r\n" + "DATE: " + getDate()
				+ "\r\n" + "SERVER: " + System.getProperty("os.name") + "/"
				+ System.getProperty("os.version")
				+ " UPnP/1.0 SamsungUPnP/1.0\r\n" + "SID: "
				+ subscription.getSubscriptionId() + "\r\n" + "TIMEOUT: "
				+ subscription.getTimeout() + "\r\n\r\n";
		return message;
	}

	// This method will be called by the subscription check and renwal check to
	// get ,
	// the system time according to upnp specification
	String getDate() {
		SimpleDateFormat responseDateFormat;
		GregorianCalendar gregCal = new GregorianCalendar();
		responseDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		responseDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String datetimevalue = responseDateFormat.format(gregCal.getTime());
		return datetimevalue;
	}
}