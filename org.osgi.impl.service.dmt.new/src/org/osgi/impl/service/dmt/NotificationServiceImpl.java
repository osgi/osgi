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

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import info.dmtree.DmtException;
import info.dmtree.notification.AlertItem;
import info.dmtree.notification.NotificationService;
import info.dmtree.notification.spi.RemoteAlertSender;
import info.dmtree.security.AlertPermission;

public class NotificationServiceImpl implements NotificationService {
    private Context context;

    NotificationServiceImpl(Context context) {
        this.context = context;
    }
    
    public void sendNotification(String principal, int code, String correlator,
            AlertItem[] items) throws DmtException {
        
        SecurityManager sm = System.getSecurityManager();
        if(sm != null)
            sm.checkPermission(
                    new AlertPermission(principal != null ? principal : "*"));

        
        RemoteAlertSender alertSender = getAlertSender(principal);
        if (alertSender == null) {
            if (principal == null)
                throw new DmtException((String) null,
                        DmtException.ALERT_NOT_ROUTED,
                        "Remote adapter not found or is not unique, cannot " +
                        "route alert without principal name.");
            throw new DmtException((String) null, DmtException.ALERT_NOT_ROUTED,
                    "Cannot find remote adapter that can send the alert to " +
                    "principal '" + principal + "'.");
        }
        
        try {
            // If all parameters (except principal) are 0/null, send default 
            // alert (1201) for client-initiated session initialization
            if(code == 0 && correlator == null && items == null) 
                alertSender.sendAlert(principal, 1201, null, null);
            else
                alertSender.sendAlert(principal, code, correlator, items);
        }
        catch (Exception e) {
            String message = "Error sending remote alert";
            if (principal != null)
                message = message + " to principal '" + principal + "'";
            throw new DmtException((String) null, DmtException.REMOTE_ERROR, 
                    message + ".", e);
        }
    }
    
    private RemoteAlertSender getAlertSender(String principal) {
        ServiceTracker remoteAdapterTracker = 
            context.getTracker(RemoteAlertSender.class);
       
        ServiceReference[] alertSenderRefs = 
            remoteAdapterTracker.getServiceReferences(); 
        
        if(alertSenderRefs == null)
            return null;
        
        ServiceReference bestRef = null;
        ServiceReference bestDefaultRef = null;
        
        // find the best adapter that accepts alerts for the given principal and
        // the best "default" adapter that is not associated with principals  
        for(int i = 0; i < alertSenderRefs.length; i++) {
            if(isDefaultSender(alertSenderRefs[i]))
                bestDefaultRef = betterRef(alertSenderRefs[i], bestDefaultRef);
            else if(principal != null && acceptsServerId(alertSenderRefs[i], principal))
                bestRef = betterRef(alertSenderRefs[i], bestRef);
        }
        
        if(bestRef == null) {
            if(bestDefaultRef != null)
                // use the best default sender if no principal was given, or if 
                // no alert senders accept the given principal 
                bestRef = bestDefaultRef;
            else if(principal == null && alertSenderRefs.length == 1)
                // if there is exactly one (non-default) sender, then it can be
                // used if no principal was given
                bestRef = alertSenderRefs[0];
            else 
                return null;
        }
        
        // return service object for the overall best reference
        // can still be null if service was unregistered in the meantime
        return (RemoteAlertSender) remoteAdapterTracker.getService(bestRef);
    }
    
    // precondition: reference parameter is not null
    private boolean isDefaultSender(ServiceReference ref) {
        return ref.getProperty("principals") == null;
    }
    
    // precondition: parameters are not null
    private boolean acceptsServerId(ServiceReference ref, String principal) {
        Object param = ref.getProperty("principals");
        if(param == null || !(param instanceof String[]))
            return false;

        String[] principals = (String[]) param;
        for (int i = 0; i < principals.length; i++)
            if(principal.equals(principals[i]))
                return true;
        
        return false;
    }
    
    private ServiceReference betterRef(ServiceReference ref, 
                                       ServiceReference best) {
        if(best == null)
            return ref;
       
        int refRank = getRanking(ref);
        int bestRank = getRanking(best);
        
        if(refRank != bestRank)
            return refRank > bestRank ? ref : best;
        
        return getId(ref) < getId(best) ? ref : best;
    }
    
    private int getRanking(ServiceReference ref) {
        Object property = ref.getProperty(Constants.SERVICE_RANKING);
        // a ranking of 0 must be assumed if property is missing or invalid
        if(property == null || !(property instanceof Integer))
            return 0;
        return ((Integer) property).intValue();
    }
    
    private long getId(ServiceReference ref) {
        // this property must be guaranteed to be set by the framework
        return ((Long) ref.getProperty(Constants.SERVICE_ID)).longValue();
    }
}
