/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.framework.secure.classloading.exports.security;

import java.security.Permission;

/**
 * A simple permission used in tests
 * 
 * @author $Id$
 */
public class SomePermission extends Permission {

	private static final long	serialVersionUID	= 1L;
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
