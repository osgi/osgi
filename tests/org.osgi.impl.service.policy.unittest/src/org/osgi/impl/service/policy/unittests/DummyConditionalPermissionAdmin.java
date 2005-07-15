/*
 * ============================================================================
 * (c) Copyright 2005 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
package org.osgi.impl.service.policy.unittests;

import java.security.AccessControlContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.permissionadmin.PermissionInfo;

public class DummyConditionalPermissionAdmin extends HashMap implements
		ConditionalPermissionAdmin
{
	private static final long	serialVersionUID	= 3618422626792781877L;



	public class PI implements ConditionalPermissionInfo, Comparable {
		String id;
		ConditionInfo[] conditionInfo;
		PermissionInfo[] permissionInfo;

		public PI(String id,ConditionInfo[] conditionInfo,PermissionInfo[] permissionInfo) {
			this.id = id;
			this.conditionInfo = conditionInfo;
			this.permissionInfo = permissionInfo;
		}
		public void delete() {
			remove(id);
		}
		public ConditionInfo[] getConditionInfos() {
			return conditionInfo;
		}
		public PermissionInfo[] getPermissionInfos() {
			return permissionInfo;
		}
		public int compareTo(Object o) {
			PI other = (PI) o;
			return toString().compareTo(other.toString());
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<conditionInfo.length;i++) {
				sb.append(conditionInfo[i].getEncoded());
				sb.append('\n');
			}
			for(int i=0;i<permissionInfo.length;i++) {
				sb.append(permissionInfo[i].getEncoded());
				sb.append('\n');
			}
			return sb.toString();
		}
		public String getName() {
			return id;
		}
	}
	
	public Enumeration getConditionalPermissionInfos() {
		final Iterator iter = values().iterator();	
		return new Enumeration() {
			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			public Object nextElement() {
				return iter.next();
			} 
		};
	}



	public AccessControlContext getAccessControlContext(String[] signers) {
		throw new IllegalArgumentException();
	}

	public ConditionalPermissionInfo addConditionalPermissionInfo(ConditionInfo[] conds, PermissionInfo[] perms) {
		throw new IllegalArgumentException();
	}

	public ConditionalPermissionInfo setConditionalPermissionInfo(String name, ConditionInfo[] conds, PermissionInfo[] perms) {
		PI pi = new PI(name,conds,perms);
		put(name,pi);
		return pi;
	}

	public ConditionalPermissionInfo getConditionalPermissionInfo(String name) {
		return getConditionalPermissionInfo(name);
	}
}
