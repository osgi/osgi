/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.IOException;

/**
 * Exception thrown when a reply indicates an exception on the execution side.
 */
public class LinkException extends IOException {
	Exception	_exception;

	public LinkException(Exception e) {
		_exception = e;
	}

	public Exception getException() {
		return _exception;
	}
}
