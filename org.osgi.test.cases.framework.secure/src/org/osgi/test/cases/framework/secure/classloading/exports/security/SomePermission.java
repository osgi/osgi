/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.framework.secure.classloading.exports.security;

import java.security.Permission;

/**
 * A simple permission used in tests
 * 
 * @version $Id$
 */
public class SomePermission extends Permission {

	private boolean	deny;

	/**
	 * Creates a new instance of SomePermission. Only when this class is loaded
	 * by tb6b and the resource tb6b.properties is found, the permission is
	 * granted.
	 */
	public SomePermission(String _resource, String _action) {
		super("SomePermission");

		deny = (getClass().getResource("/tb6b.properties") == null);
	}

	/**
	 * Checks two Permission objects for equality. Return true if the loading
	 * bundle is tb6b.
	 * 
	 * @param _object
	 * @return return true if the loading bundle is tb6b, otherwise return
	 *         false.
	 */
	public boolean equals(Object _object) {
		return deny;
	}

	/**
	 * Returns the actions as a String. Always return an empty string.
	 * 
	 * @return always an empty string
	 */
	public String getActions() {
		return "";
	}

	/**
	 * Returns the hash code value for this Permission object. Always return 0.
	 * 
	 * @return always 0
	 */
	public int hashCode() {
		return 0;
	}

	/**
	 * Checks if the specified permission's actions are "implied by" this
	 * object's actions. Return true if the loading bundle is tb6b.
	 * 
	 * @param _permission
	 * @return return true if the loading bundle is tb6b, otherwise return
	 *         false.
	 */
	public boolean implies(Permission _permission) {
		return deny;
	}

}