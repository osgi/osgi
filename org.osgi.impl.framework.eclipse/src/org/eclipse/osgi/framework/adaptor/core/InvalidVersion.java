/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.adaptor.core;

import org.osgi.framework.Version;

/**
 * This class is used to hold invalid version strings.  This is used to support 
 * OSGi R3 bundles which could have an invalid Bundle-Version header.  An 
 * InvalidVersion always has a value of 0.0.0.
 */
public class InvalidVersion extends Version {
	private String invalidVersion;
	/**
	 * Constructs a BadVersion using the specified invalid version string.
	 * @param badVersion an invalid version string.
	 */
	public InvalidVersion(String badVersion) {
		super(0, 0, 0, null);
		this.invalidVersion = badVersion;
	}

	/**
	 * Returns the invalid version string.
	 * @return the invalid version string.
	 */
	public String getInvalidVersion() {
		return invalidVersion;
	}

	public String toString() {
		return invalidVersion;
	}
}
