/*
 * Copyright (c) 2004, Nokia
 * COMPANY CONFIDENTAL - for internal use only
 */
package org.osgi.service.condpermadmin;

import java.util.Enumeration;

import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public interface ConditionalPermissionAdmin {
	ConditionalPermissionInfo addCollection(ConditionInfo[] conds,PermissionInfo perms[]);
	Enumeration getCollections();
}
