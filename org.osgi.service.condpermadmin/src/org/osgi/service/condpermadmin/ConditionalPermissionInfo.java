/*
 * Copyright (c) 2004, Nokia
 * COMPANY CONFIDENTAL - for internal use only
 */
package org.osgi.service.condpermadmin;

import org.osgi.service.permissionadmin.PermissionInfo;

/**
 * @author Peter Nagy <peter.1.nagy@nokia.com>
 */
public interface ConditionalPermissionInfo {
	void delete();
	ConditionInfo[] getConditionInfos();
	PermissionInfo[] getPermissionInfos();

}
