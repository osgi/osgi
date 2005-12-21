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
import java.io.Serializable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class TransactionRecord implements Serializable {
    
	public int                      code;
    public WrappedResourceProcessor wrProc;
    public long                     bundleId;
    public BundleEntry              be;
    public DeploymentPackageImpl    dp;
    public ByteArrayInputStream     bis;
    
    public TransactionRecord(int code, 
                             WrappedResourceProcessor wrProc, 
                             long bundleId,
                             BundleContext context,
                             BundleEntry be,
                             DeploymentPackageImpl dp,
                             ByteArrayInputStream bis) 
    {
        this.code = code;
        this.wrProc = wrProc;
        this.bundleId = bundleId;
        this.be = be;
        this.dp = dp;
        this.bis = bis;
    }
    
    public TransactionRecord(int code, WrappedResourceProcessor rp) {
        this(code, rp, -1, null, null, null, null);
    }
    
    public TransactionRecord(int code, Bundle b) {
        this(code, null, b.getBundleId(), null, null, null, null);
    }
    
    public TransactionRecord(int code, 
                             Bundle b, 
                             BundleEntry be, 
                             DeploymentPackageImpl dp) 
    {
        this(code, null, -1, null, be, dp, null);
    }
    
    public TransactionRecord(int code, 
    		BundleEntry be, 
    		BundleContext context,
    		DeploymentPackageImpl dp,
    		ByteArrayInputStream bis) 
    {
    	this(code, null, -1, context, be, dp, bis);
    }

    public TransactionRecord(int code, 
    		Bundle bundle, 
    		ByteArrayInputStream bis) 
    {
    	this(code, null, -1, null, null, null, bis);
    }
    
    public String toString() {
        return "[----------------------------------------------------\n" +
        		"code:               " + Transaction.transactionCodes[code] + "\n" +
        		"resource processor: " + wrProc + "\n" +
        		"bundleId:             " + bundleId + "\n" +
        		"----------------------------------------------------]\n";
    }

}
