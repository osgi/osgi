/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.permissionadmin.conditional.tbc;

import java.security.AllPermission;

import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractPermissionAdminTests extends OSGiTestCase {
	protected ConditionalPermissionAdmin condPermAdmin;
	private ServiceReference condPermAdminRef;
	protected PermissionAdmin permAdmin;
	private ServiceReference permAdminRef;

	protected void setUp() throws Exception {
		super.setUp();
		// get condpermadmin
		condPermAdminRef = getContext().getServiceReference(ConditionalPermissionAdmin.class.getName());
		assertNotNull("No ConditionalPermissionAdmin available", condPermAdminRef);
		condPermAdmin = (ConditionalPermissionAdmin) getContext().getService(condPermAdminRef);
		assertNotNull("No ConditionalPermissionAdmin available", condPermAdmin);

		// get permadmin
		permAdminRef = getContext().getServiceReference(PermissionAdmin.class.getName());
		assertNotNull("No PermissionAdmin available", permAdminRef);
		permAdmin = (PermissionAdmin) getContext().getService(permAdminRef);
		assertNotNull("No PermissionAdmin available", permAdmin);
		
	    // make sure this bundle has all permissions
	    permAdmin.setPermissions(getContext().getBundle().getLocation(), new PermissionInfo[] {new PermissionInfo("(" + AllPermission.class.getName() +")")});
	}

	protected void tearDown() throws Exception {
		// clear any permissions set
		ConditionalPermissionUpdate update = condPermAdmin.newConditionalPermissionUpdate();
	    update.getConditionalPermissionInfos().clear();
	    update.commit();
	    permAdmin.setDefaultPermissions(null);
	    String[] locations = permAdmin.getLocations();
	    if (locations != null)
	    	for (int i = 0; i < locations.length; i++)
	    		permAdmin.setPermissions(locations[i], null);

		if (condPermAdminRef != null)
			getContext().ungetService(condPermAdminRef);
		condPermAdminRef = null;
		condPermAdmin = null;

		if (permAdminRef != null)
			getContext().ungetService(permAdminRef);
		permAdminRef = null;
		permAdmin = null;
	}
}
