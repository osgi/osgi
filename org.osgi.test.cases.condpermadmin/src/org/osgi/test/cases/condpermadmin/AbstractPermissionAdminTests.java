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

package org.osgi.test.cases.condpermadmin;

import java.io.IOException;
import java.net.URL;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.test.support.OSGiTestCase;

public abstract class AbstractPermissionAdminTests extends OSGiTestCase {
	protected ConditionalPermissionAdmin condPermAdmin;
	protected PermissionAdmin permAdmin;
	private Collection serviceRefs = new ArrayList();

	protected void setUp() throws Exception {
		super.setUp();
		serviceRefs.clear();
		// get condpermadmin
		condPermAdmin = (ConditionalPermissionAdmin) getService(ConditionalPermissionAdmin.class.getName());
		// get permadmin
		permAdmin = (PermissionAdmin) getService(PermissionAdmin.class.getName());
		
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

	    for (Iterator iRefs = serviceRefs.iterator(); iRefs.hasNext();) {
	    	getContext().ungetService((ServiceReference) iRefs.next());
	    }
	    serviceRefs.clear();

	    condPermAdmin = null;
	    permAdmin = null;
	}

	protected Bundle installBundle(String bundle, boolean start) throws BundleException, IOException {
		URL entry = getContext().getBundle().getEntry(bundle);
		assertNotNull("Can not find bundle: " + bundle, entry);
		Bundle b= getContext().installBundle(entry.toExternalForm(), entry.openStream());
	    // make sure the test bundles do not have any location permissions set; SimplePermissionPolicy sets these
	    permAdmin.setPermissions(b.getLocation(), null);
		if (start)
			b.start();
		return b;
	}

    public Object getService(String clazz)  {
        ServiceReference ref = getContext().getServiceReference(clazz);
        assertNotNull("No service found: " + clazz, ref);
        Object service = getContext().getService(ref);
        assertNotNull("No service found: " + clazz, service);
        serviceRefs.add(ref);
        return service;
    }
}
