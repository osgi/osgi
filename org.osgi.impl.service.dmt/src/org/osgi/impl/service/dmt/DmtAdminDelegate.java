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

import org.osgi.framework.Bundle;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtIllegalStateException;
import org.osgi.service.dmt.DmtSession;

/*
 * Dmt Admin service provider class. An instance of this is returned by
 * DmtServiceFactory for each bundle using the DmtAdmin service. Calls to the
 * getSession methods are forwarded to a singleton DmtAdminCore instance. This
 * class provides support for event listener registration, and event delivery to
 * the registered listeners.
 */
public class DmtAdminDelegate implements DmtAdmin {
    

    private DmtAdminCore dmtAdmin;
	@SuppressWarnings("unused")
	private Context			context;
    private Bundle initiatingBundle;
    
    private boolean active; 

    DmtAdminDelegate(DmtAdminCore dmtAdmin, Context context, Bundle bundle) {
        this.dmtAdmin = dmtAdmin;
        this.context = context;
        this.initiatingBundle = bundle;
        
        active = true;
    }
    
    @Override
	public DmtSession getSession(String subtreeUri) throws DmtException {
        checkState();
        return dmtAdmin.getSession(subtreeUri, initiatingBundle);
    }

    @Override
	public DmtSession getSession(String subtreeUri, int lockMode)
            throws DmtException {
        checkState();
        return dmtAdmin.getSession(subtreeUri, lockMode, initiatingBundle);
    }

    @Override
	public DmtSession getSession(String principal, String subtreeUri,
            int lockMode) throws DmtException {
        checkState();
        return dmtAdmin.getSession(principal, subtreeUri, lockMode, initiatingBundle);
    }

    
    void close() {
        active = false;
    }
    
    
    private void checkState() {
        if(!active)
            throw new DmtIllegalStateException("The service can no longer be " +
                    "used, as it has been released by the caller.");
    }

	/**
	 * @return the configured timeout for a session creation in milliseconds.
	 */
	public long getSessionCreationTimeout() {
		return dmtAdmin.getSessionCreationTimeout();
	}

	/**
	 * @return the configured timeout for an invalid session in milliseconds.
	 */
	public long getSessionInactivityTimeout() {
		return dmtAdmin.getSessionInactivityTimeout();
	}
	
}
