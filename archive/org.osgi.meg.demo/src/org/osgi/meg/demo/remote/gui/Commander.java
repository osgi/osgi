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

import org.osgi.meg.demo.remote.*;

public class Commander implements RemoteReceiver {
    
    private RMServer      rms;
    private String        result;
    private ServerTestGUI gui;
    
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
        String exStr = "org.osgi.service.dmt.DmtException: ";
        if (result.startsWith(exStr)) {
            int exStrL = exStr.length();
            int index = result.indexOf(':', exStrL);
            String code = result.substring(exStrL, index);
            throw new CommanderException(code, result);
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
        synchronized (this) {
        	notify();
        }
    }

	public void setConnected(boolean b) {
		gui.setConnected(b);
	}

}
