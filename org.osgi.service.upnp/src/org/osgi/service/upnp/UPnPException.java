/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.upnp;

/**
 * There are several defined error situations describing UPnP problems while a
 * control point invokes actions to UPnPDevices.
 */
import java.lang.Exception;

public class UPnPException extends Exception {
	/**
	 * Key for an error information that is an int type variable and that is
	 * used to identify occured errors.
	 */
	private int	errorCode;

	/**
	 * This constructor creates a UPnPException on the specified error code and
	 * error description.
	 * 
	 * @param erorCode errorCode which defined UPnP Device Architecture V1.0.
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
