package org.osgi.impl.service.upnp.cd.util;

import java.lang.Exception;

public class UPnPException extends Exception {
	private Throwable	th;

	public Throwable getNestedException() {
		return th;
	}

	// One argument Constructor which accepts a string and constructs this
	// exception object.
	public UPnPException(String s) {
		super(s);
	}

	// Constructor which accepts the message and an exception.
	public UPnPException(String s, Throwable t) {
		super(s);
		th = t;
	}
}
