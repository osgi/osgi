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
package org.osgi.impl.service.deploymentadmin;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Logger {
    
    public static final int LOG_ERROR   = 1; 
    public static final int LOG_WARNING = 2;
    public static final int LOG_INFO    = 3;
    public static final int LOG_DEBUG   = 4;
    
    private static final String[] sevLevels = {
        "LOG_ERROR",    // 1
        "LOG_WARNING",  // 2
        "LOG_INFO",     // 3
        "LOG_DEBUG"     // 4
    };
    
    private ServiceTracker tracker;

    public Logger(BundleContext context) {
        tracker = new ServiceTracker(context, LogService.class.getName(), null);
        tracker.open();
    }
    
    public void stop() {
        tracker.close();
    }
    
    public synchronized void log(int severity, String log) {
        LogService service = (LogService) tracker.getService();
        if (null != service)
            service.log(severity, log);

        if (DAConstants.DEBUG)
            System.out.println(sevLevels[severity - 1] + ": " + log);
    }
    
    public synchronized void log(Exception exception) {
        log(exception, LOG_WARNING);
    }
    
    public synchronized void log(Exception exception, int level) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true);
        exception.printStackTrace(pw);
        
        LogService service = (LogService) tracker.getService();
        if (null != service)
            service.log(level, "", exception);
        
        if (DAConstants.DEBUG)
            System.out.println(sevLevels[1 - 1] + ": " + baos.toString());
    }

}
