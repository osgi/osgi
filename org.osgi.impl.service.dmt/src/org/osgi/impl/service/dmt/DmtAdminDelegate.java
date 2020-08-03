/*
 * ============================================================================
 * (c) Copyright 2004 Nokia
 * This material, including documentation and any related computer programs,
 * is protected by copyright controlled by Nokia and its licensors. 
 * All rights are reserved.
 * 
 * These materials have been contributed  to the Open Services Gateway 
 * Initiative (OSGi)as "MEMBER LICENSED MATERIALS" as defined in, and subject 
 * to the terms of, the OSGi Member Agreement specifically including, but not 
 * limited to, the license rights and warranty disclaimers as set forth in 
 * Sections 3.2 and 12.1 thereof, and the applicable Statement of Work. 
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.  
 * The above notice must be included on all copies of this document.
 * ============================================================================
 */
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
