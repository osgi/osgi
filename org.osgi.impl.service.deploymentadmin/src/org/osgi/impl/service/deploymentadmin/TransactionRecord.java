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

import java.io.ByteArrayInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class TransactionRecord {
    
    public int                      code;
    public WrappedResourceProcessor wrProc;
    public Bundle			        bundle;
    public BundleContext			context;
    public BundleEntry              be;
    public DeploymentPackageImpl    dp;
    public ByteArrayInputStream     bis;
    
    public TransactionRecord(int code, 
                             WrappedResourceProcessor wrProc, 
                             Bundle b,
                             BundleContext context,
                             BundleEntry be,
                             DeploymentPackageImpl dp,
                             ByteArrayInputStream bis) 
    {
        this.code = code;
        this.wrProc = wrProc;
        this.bundle = b;
        this.be = be;
        this.dp = dp;
        this.bis = bis;
    }
    
    public TransactionRecord(int code, WrappedResourceProcessor rp) {
        this(code, rp, null, null, null, null, null);
    }
    
    public TransactionRecord(int code, Bundle b) {
        this(code, null, b, null, null, null, null);
    }
    
    public TransactionRecord(int code, 
                             Bundle b, 
                             BundleEntry be, 
                             DeploymentPackageImpl dp) 
    {
        this(code, null, b, null, be, dp, null);
    }
    
    public TransactionRecord(int code, 
    		BundleEntry be, 
    		BundleContext context,
    		DeploymentPackageImpl dp,
    		ByteArrayInputStream bis) 
    {
    	this(code, null, null, context, be, dp, bis);
    }

    public TransactionRecord(int code, 
    		Bundle bundle, 
    		ByteArrayInputStream bis) 
    {
    	this(code, null, bundle, null, null, null, bis);
    }
    
    public String toString() {
        return "[----------------------------------------------------\n" +
        		"code:               " + Transaction.transactionCodes[code] + "\n" +
        		"resource processor: " + wrProc + "\n" +
        		"bundle:             " + bundle + "\n" +
        		"----------------------------------------------------]\n";
    }

}
