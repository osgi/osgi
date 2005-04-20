package org.osgi.impl.service.deploymentadmin;

import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.Bundle;

public class TransactionRecord {
    
    public int                      code;
    public WrappedResourceProcessor rp;
    public Bundle			        bundle;
    public BundleEntry              be;
    public DeploymentPackageImpl    dp;
    
    public TransactionRecord(int code, 
                             WrappedResourceProcessor rp, 
                             Bundle b,
                             BundleEntry be,
                             DeploymentPackageImpl dp) 
    {
        this.code = code;
        this.rp = rp;
        this.bundle = b;
        this.be = be;
        this.dp = dp;
    }
    
    public TransactionRecord(int code, WrappedResourceProcessor rp) {
        this(code, rp, null, null, null);
    }
    
    public TransactionRecord(int code, Bundle b) {
        this(code, null, b, null, null);
    }
    
    public TransactionRecord(int code, 
                             Bundle b, 
                             BundleEntry be, 
                             DeploymentPackageImpl dp) 
    {
        this(code, null, b, be, dp);
    }
    
    public String toString() {
        return "[----------------------------------------------------\n" +
        		"code:               " + Transaction.transactionCodes[code] + "\n" +
        		"resource processor: " + rp + "\n" +
        		"bundle:             " + bundle + "\n" +
        		"----------------------------------------------------]";
    }

    private String breakLines(String str, int max) {
        String s = new String(str);
        Vector v = new Vector();
        while (s.length() > max) {
            String l = s.substring(0, Math.min(s.length(), max));
            v.add(l);
            s = s.substring(Math.min(s.length(), max));
        }
        v.add(s);
        
        StringBuffer ret = new StringBuffer();
        for (Iterator iter = v.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            ret.append(element);
        }
        return ret.toString();
    }

}
