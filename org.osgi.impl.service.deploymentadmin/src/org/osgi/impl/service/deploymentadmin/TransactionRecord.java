package org.osgi.impl.service.deploymentadmin;

import java.util.Iterator;
import java.util.Vector;

public class TransactionRecord {
    
    public int      code;
    public Object[] objs;

    public TransactionRecord(int code, Object[] objs) {
        this.code = code;
        this.objs = objs;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < objs.length; i++) {
            String s = breakLines(objs[i].toString(), 80);
            sb.append(s + "\n");
        }
        return "[----------------------------------------------------\n" +
        		"code: " + Transaction.transactionCodes[code] + "\n" +
        		"objs: " + sb.toString() + 
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
