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
package org.osgi.meg.demo.remote.gui;

import org.osgi.meg.demo.remote.CommanderException;
import org.osgi.meg.demo.remote.RMServer;
import org.osgi.meg.demo.remote.RemoteReceiver;

public class Commander implements RemoteReceiver {
    
    private RMServer           rms;
    private String             result;
    private CommanderException ex;
    private ServerTestGUI      gui;
    
    public Commander(RMServer rms, ServerTestGUI gui) {
        this.rms = rms;
        this.gui = gui;
    }

    public synchronized String command(String cmd) throws CommanderException {
    	rms.setCommand(cmd);
        try {
			wait();
		} catch (InterruptedException e) {
            return "error: " + e.getMessage();
		}
        if(ex != null) {
            ex.fillInStackTrace();
            throw ex;
        }

        return result;
    }
    
	public void onAlert(String alert) {
        if (null == alert)
            return;
		gui.alert(alert);
	}

	public void onResult(String result) {
		this.result = result;
        ex = null;
        synchronized (this) {
        	notify();
        }
    }
    
    public void onError(String exception, String code, String uri, 
            String message, String trace) {
        ex = new CommanderException(exception, code, uri, message, trace);
        synchronized (this) {
        	notify();
        }
    }

	public void setConnected(boolean b) {
		gui.setConnected(b);
	}

}
