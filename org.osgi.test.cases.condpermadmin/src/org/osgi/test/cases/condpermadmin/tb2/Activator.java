/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.test.cases.condpermadmin.tb2;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.condpermadmin.service.ConditionalDomTBCService;
import org.osgi.test.cases.condpermadmin.service.ConditionalPermTBCService;


public class Activator implements BundleActivator, ConditionalPermTBCService {
	
	private BundleContext context;

  public void start(BundleContext context) throws Exception {
    this.context = context;
		context.registerService(ConditionalPermTBCService.class.getName(),this,null);
	}
	
	public void stop(BundleContext context) throws Exception {
	}
  
  public void checkPermission(final Permission permission) 
    throws SecurityException {
    try {
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Void>() {
						public Void run() throws SecurityException {
          SecurityManager security = System.getSecurityManager();
          security.checkPermission(permission);
          return null;
        }
      });
    } catch (PrivilegedActionException e) {
      throw (SecurityException) e.getException();
    }
  }

  public void checkStack(Permission permission) throws SecurityException {
		ServiceReference<ConditionalDomTBCService> ref = context
				.getServiceReference(ConditionalDomTBCService.class);
    ConditionalDomTBCService service = context.getService(ref);
    service.checkStack(permission);
    context.ungetService(ref);
  }

  public void checkStack2(Permission permission) throws SecurityException {
	  checkStack(permission);
  }
}
