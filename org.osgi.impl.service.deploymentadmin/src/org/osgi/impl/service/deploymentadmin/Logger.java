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
        if (null != service && !DAConstants.DEBUG) {
            service.log(severity, log);
        } else {
            System.out.println(sevLevels[severity - 1] + ": " + log);
        }
    }
    
    public synchronized void log(Exception exception) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true);
        exception.printStackTrace(pw);
        
        LogService service = (LogService) tracker.getService();
        if (null != service && !DAConstants.DEBUG) {
            service.log(LOG_ERROR, baos.toString());
        } else {
            System.out.println(sevLevels[1 - 1] + ": " + baos.toString());
        }
    }

}
