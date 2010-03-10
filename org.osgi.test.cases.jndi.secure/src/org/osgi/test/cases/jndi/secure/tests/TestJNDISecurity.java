/*
 * Copyright (c) IBM Corporation (2010). All Rights Reserved.
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


package org.osgi.test.cases.jndi.secure.tests;

import javax.naming.Context;
import javax.naming.NameNotFoundException;

import org.osgi.service.jndi.JNDIContextManager;
import org.osgi.test.support.compatibility.DefaultTestBundleControl;

/** 
 * @version $Revision$ $Date$
 */
public class TestJNDISecurity extends DefaultTestBundleControl {
	
	public void testLookupAccessRestrictions() throws Exception {
		// Grab the context manager
		JNDIContextManager ctxManager = (JNDIContextManager) getService(JNDIContextManager.class);
		Context ctx = null;
		try {
			// Grab a default context for looking up services
			ctx = ctxManager.newInitialContext();
			assertNotNull("The context should not be null", ctx);
			// Try to grab a service that we shouldn't be allowed to grab.
			Object testObject = ctx.lookup("osgi:service/org.osgi.service.permissionadmin.PermissionAdmin");
		} catch (javax.naming.NameNotFoundException ex) {
			pass("javax.naming.NameNotFoundException caught in testLookupAccessRestrictions: SUCCESS");
			return;
		} finally {
			if (ctx != null) {
				ctx.close();
			}
			ungetService(ctxManager);
		}

		failException("testLookupAccessRestrictions failed, ", javax.naming.NameNotFoundException.class);
	}
}
