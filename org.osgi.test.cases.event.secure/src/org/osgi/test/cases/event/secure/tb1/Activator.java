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

package org.osgi.test.cases.event.secure.tb1;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.test.cases.event.secure.service.SenderService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A bundle that registers a SenderService service.
 *
 * @author $Id$
 */
public class Activator implements BundleActivator, SenderService {

	private ServiceRegistration<SenderService>	senderServiceReg;
	ServiceTracker<EventAdmin, EventAdmin>		eventAdminTracker;

	public void start(BundleContext context) throws Exception {
		eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(context, EventAdmin.class, null);
		eventAdminTracker.open();
		senderServiceReg = context.registerService(SenderService.class, this, null);
	}

	public void stop(BundleContext context) throws Exception {
		senderServiceReg.unregister();
		eventAdminTracker.close();
	}

	public void postEvent(final Event event) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			public Void run() {
				eventAdminTracker.getService().postEvent(event);
				return null;
			}
		});
	}

	public void sendEvent(final Event event) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			public Void run() {
				eventAdminTracker.getService().sendEvent(event);
				return null;
			}
		});
	}
}
