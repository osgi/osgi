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
package org.osgi.impl.service.dmt;

import java.util.List;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.impl.service.dmt.export.DmtPrincipalPermissionAdmin;
import org.osgi.service.dmt.DmtAdmin;

/*
 * Service factory class for the Dmt Admin service. Stores each Dmt Admin
 * delegate that is in use, and forwards DMT events to each one, for delivery to
 * the registered event listeners.
 */
public class DmtAdminFactory implements ServiceFactory<DmtAdmin> {
	DmtAdminCore					dmtAdmin;
	private List<DmtAdminDelegate>	delegates;
    private Context context;
    private LocalEventProxy localEventProxy;

    DmtAdminFactory(Context context, DmtAdminCore dmtAdmin, 
            DmtPrincipalPermissionAdmin dmtPermissionAdmin) {
        this.context = context;
        
        this.dmtAdmin = dmtAdmin;
        localEventProxy = new LocalEventProxy();
        
        new Thread(localEventProxy).start();
        
		delegates = new Vector<>();
    }

    @Override
	public DmtAdmin getService(Bundle bundle,
			ServiceRegistration<DmtAdmin> registration) {
        DmtAdminDelegate delegate = new DmtAdminDelegate(dmtAdmin, context, bundle);
        delegates.add(delegate);
        return delegate;
    }

    @Override
	public void ungetService(Bundle bundle,
			ServiceRegistration<DmtAdmin> registration, DmtAdmin service) {
        DmtAdminDelegate delegate = (DmtAdminDelegate) service;
        delegates.remove(delegate);
        delegate.close();
    }
    
    void stop() {
        localEventProxy.stop();
    }
    
    class LocalEventProxy implements Runnable {
        private boolean running = false;
        
        @Override
		public void run() {
            running = true;
            
            while(running) {
                DmtEventCore event = EventDispatcher.getNextLocalEvent(10000);
                if(event != null)
                	// events are now dispatched by the dmtAdmin centrally
                	dmtAdmin.dispatchEvent(event);
            }
        }
        
        void stop() {
            running = false;
        }
    }
}
