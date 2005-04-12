package org.osgi.impl.service.deploymentadmin;

import java.util.Iterator;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.service.deploymentadmin.ResourceProcessor;

public class TransactionRecord {
    
    public int               code;
    public ResourceProcessor rp;
    public Bundle			 bundle;
    
    public TransactionRecord(int code, ResourceProcessor rp, Bundle b) {
        this.code = code;
        this.rp = rp;
        this.bundle = b;
    }
    
    public TransactionRecord(int code, ResourceProcessor rp) {
        this(code, rp, null);
    }
    
    public TransactionRecord(int code, Bundle b) {
        this(code, null, b);
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
