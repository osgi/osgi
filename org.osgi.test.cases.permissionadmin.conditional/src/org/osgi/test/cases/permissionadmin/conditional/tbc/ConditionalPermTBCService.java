
package org.osgi.test.cases.permissionadmin.conditional.tbc;

import java.security.Permission;

public interface ConditionalPermTBCService {
	
	public void checkPermission(Permission permission) throws SecurityException;

}
