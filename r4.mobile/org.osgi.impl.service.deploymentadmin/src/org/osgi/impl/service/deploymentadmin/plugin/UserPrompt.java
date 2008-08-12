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
package org.osgi.impl.service.deploymentadmin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.osgi.impl.service.deploymentadmin.DAConstants;

public class UserPrompt {
    
    private static boolean enabled;
    static {
        String s = System.getProperty(DAConstants.USER_PROMPT);
        if (null == s)
            enabled = false;
        enabled = Boolean.valueOf(s).booleanValue(); 
    }
    
    static String prompt(String info, String question, String def) {
        if (!enabled)
            return def;
        
        System.out.println(info);
        System.out.print(question + " [" + (null == def ? "" : def) + "]: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        try {
            line = br.readLine();
        }
        catch (IOException e) {
        }
        if (line.trim().equals(""))
            return def;
        return line;
    }

}
