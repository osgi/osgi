/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.profile;

public interface ProfileLogger {

	/**
	 * 
	 *@see Profile#initProps()
	 */
	public void initProps();

	/**
	 *@see Profile#logTime(int, String, String, String) 
	 */
	public void logTime(int flag, String id, String msg, String description);

	/**
	 * 
	 * @see Profile#getProfileLog()
	 */
	public String getProfileLog();
}
