package org.osgi.impl.service.upnp.cp.util;

public class UPnPException extends Exception {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
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
