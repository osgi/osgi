package org.osgi.impl.service.deploymentadmin;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.osgi.service.log.LogService;

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
    
    private LogService service;

    public Logger() {
    }
    
    public Logger(LogService service) {
        this.service = service;
        
        // TODO
        this.service = null;
    }
    
    public void log(int severity, String log) {
        if (null != service) {
            service.log(severity, log);
        } else {
            System.out.println(sevLevels[severity - 1] + ": " + log);
        }
    }
    
    public void log(Exception exception) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos, true);
        exception.printStackTrace(pw);
        
        if (null != service) {
            service.log(LOG_ERROR, baos.toString());
        } else {
            System.out.println(sevLevels[1 - 1] + ": " + baos.toString());
        }
    }

}
