/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.impl.bundle.autoconf;

import java.io.Serializable;

public final class StoredConfiguration implements Serializable {
	private static final long	serialVersionUID	= 1L;
	public final String packageName;
	public final String resourceName;
	public final String pid;
	public final String pidAlias;
	public final String factoryPid;
	public StoredConfiguration(String packageName, String resourceName,String pid,String factoryPid,String pidAlias) {
		this.packageName = packageName;
		this.resourceName = resourceName;
		this.pid = pid;
		this.factoryPid = factoryPid;
		this.pidAlias = pidAlias;
	}
	
	public boolean equals(java.lang.Object oo) {
		if (oo==null) return false;
		if (!(oo instanceof StoredConfiguration)) return false;
		StoredConfiguration o = (StoredConfiguration) oo;
		
		if (!o.packageName.equals(packageName)) return false;
		if (!o.resourceName.equals(resourceName)) return false;
		if (!o.pid.equals(pid)) return false;
		if (o.factoryPid==null) {
			if (factoryPid!=null) return false;
		} else {
			if (!o.factoryPid.equals(factoryPid)) return false;
		}
		if (o.pidAlias==null) {
			if (pidAlias!=null) return false;
		} else {
			if (!o.pidAlias.equals(pidAlias)) return false;
		}
		
		return true;
	}
	
	public int hashCode() {
		int hc = packageName.hashCode()+resourceName.hashCode()+pid.hashCode();
		if (factoryPid!=null) hc+= factoryPid.hashCode();
		if (pidAlias!=null) hc+= pidAlias.hashCode();
		return hc;
	}
}