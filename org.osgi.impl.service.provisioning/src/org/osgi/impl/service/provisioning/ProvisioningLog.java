package org.osgi.impl.service.provisioning;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Date;

/**
 * A Simple Event tracker for the ProvisioningService.  If a LogService could be present, it should
 * be linked to that.  For the reference implementation we just track things locally.
 * 
 * @author breed
 */
public class ProvisioningLog {
    Vector log = new Vector();
    
    /** Adds a message to the long and timestamps it.  */
    synchronized void log(String message) {
		String msg = new Date().toString() + ": "+ message;
        log.add(0, msg );
		System.out.println( msg );
    }
    /** Returns the full log.  Most recent messages will be first. */
    Enumeration getLog() { return log.elements(); }
}
