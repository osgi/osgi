/*
 * Copyright (c) 2004, Nokia
 * COMPANY CONFIDENTAL - for internal use only
 */
package org.osgi.service.condpermadmin;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;

import org.osgi.framework.Bundle;

/**
 * @author Peter Nagy 
 */
public class BundleLocationCondition implements Condition {

	boolean match;
	public BundleLocationCondition(final Bundle b,String location) {
		if ((location == null) || (b==null)) { match=false; return; }
		String l = (String) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return b.getLocation();
			}});
		match = l.equals(location);
	}

	public boolean isEvaluated() {
		return true;
	}

	public boolean isMutable() {
		return false;
	}

	public boolean isSatisfied() {
		return match;
	}

	public boolean isSatisfied(Condition[] conds, Dictionary oneShotData) {
		for(int i=0;i<conds.length;i++) {
			if (!((BundleLocationCondition)conds[i]).match) return false;
		}
		return true;
	}

}
