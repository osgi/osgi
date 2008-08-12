/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.impl.service.policy.unittests.util;

import java.security.Permission;

import org.osgi.framework.Bundle;

public class AdminPermission extends Permission {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long	serialVersionUID	= 1L;

	public AdminPermission(Bundle bundle, String action) { super(""); }
	public AdminPermission(String a,String b) { super(""); }
	
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean implies(Permission permission) {
		// TODO Auto-generated method stub
		return false;
	}

}
