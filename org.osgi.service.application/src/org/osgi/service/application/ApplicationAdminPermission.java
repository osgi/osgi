/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.application;

import java.security.BasicPermission;

/**
 * This class implements permissions for manipulating applications and
 * application instances.
 * <P>
 * ApplicationAdminPermission can be targeted to a particular application (using
 * its PID as an identifier) or to all applications (when no PID is specified).
 * <P>
 * ApplicationAdminPermission may be granted for different actions: 
 * <code>lifecycle</code>, <code>schedule</code> and <code>lock</code>. 
 * The permission <code>schedule</code> implies the permission <code>lifecycle</code>. 
 */
public class ApplicationAdminPermission extends BasicPermission {
	private static final long serialVersionUID = 1L;

	/**
	 * Allows the lifecycle management of the target applications.
	 */
	public static final String LIFECYCLE = "lifecycle";

	/**
	 * Allows scheduling of the target applications. The permission to
	 * schedule an application implies that the scheduler can also 
	 * manage the lifecycle of that application i.e. <code>schedule</code>
	 * implies <code>lifecycle</code>
	 */
	public static final String SCHEDULE = "schedule";

	/**
	 * Allows setting/unsetting the locking state of the target applications.
	 */
	public static final String LOCK = "lock";

	/**
	 * Constructs an ApplicationAdminPermission. The name is the unique
	 * identifier of the target application. If the name is null then the
	 * constructed permission is granted for all targets.
	 * 
	 * @param pid -
	 *            unique identifier of the application, it may be null. The
	 *            value null indicates "all application".
	 * @param actions -
	 *            comma-separated list of the desired actions granted on the
	 *            applications. It must not be null. The order of the actions in
	 *            the list is not significant.
	 * 
	 * @exception NullPointerException
	 *                is thrown if the actions parameter is null
	 */
	public ApplicationAdminPermission(String pid, String actions) {
		super(pid, actions);
	}
}