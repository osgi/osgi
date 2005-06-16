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
package com.nokia.test.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtSession;


public class DoIt implements BundleActivator {
    
    private DmtAdmin admin;

    public void start(BundleContext context) throws Exception {
        ServiceReference sref = context.getServiceReference(DmtAdmin.class.getName());
        admin = (DmtAdmin) context.getService(sref);
        
        new TestDesktop(admin);
        
        /*BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("pt:>");
        String command = reader.readLine();
        
        while (!"stop".equalsIgnoreCase(command)) {
            DmtSession session = null;
            try {
                session = admin.getSession(".");
                String[] cnns = session.getChildNodeNames(command);
                System.out.println(Arrays.asList(cnns));
            } finally {
                if (null != session)
                    session.close();
            }
            
            System.out.print("pt:> ");
            command = reader.readLine();
        }*/
    }

    public void stop(BundleContext context) throws Exception {

    }

}
