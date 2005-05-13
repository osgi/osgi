/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package org.osgi.service.upnp;

/**
 * There are several defined error situations describing UPnP problems while a
 * control point invokes actions to UPnPDevices.
 * 
 * @since 1.1
 */
import java.lang.Exception;

public class UPnPException extends Exception {
    static final long serialVersionUID = -262013318122195146L;
	/**
	 * Key for an error information that is an int type variable and that is
	 * used to identify occured errors.
	 */
	private int	errorCode;

	/**
	 * This constructor creates a UPnPException on the specified error code and
	 * error description.
	 * 
	 * @param errorCode errorCode which defined UPnP Device Architecture V1.0.
	 * @param errordesc errorDescription which explain the type of propblem.
	 */
	public UPnPException(int errorCode, String errordesc) {
		super(errordesc);
		this.errorCode = errorCode;
	}

	/**
	 * Returns the UPnPError Code occured by UPnPDevices during invocation.
	 * 
	 * @return The UPnPErrorCode defined by a UPnP Forum working committee or
	 *         specified by a UPnP vendor.
	 */
	public int getUPnPError_Code() {
		return errorCode;
	}
}
