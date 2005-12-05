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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Maintains a case insensitive map. It means that the keys are 
 * case insensitive. It also has localization support.
 */
public class CaseInsensitiveMap extends TreeMap {
    
    private DeploymentPackageImpl dp; // it has the ResourceBundle for
                                      // localization
    
    private static class MyComparator implements Comparator, Serializable {
		public int compare(Object left, Object right) {
			String l = left.toString();
			String r = right.toString();
			return l.compareToIgnoreCase(r);
		}
    }
    
    public CaseInsensitiveMap(Map other, DeploymentPackageImpl dp) {
    	super(new MyComparator());
        this.dp = dp;
        fill(other);
    }

    private void fill(Map map) {
        if (null == map)
            return;
        
        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            put(key, map.get(key));
        }
    }
    
    public Object put(Object key, Object value) {
    	// because the entire map has to be serializable
    	return super.put(key.toString(), value.toString());
    }

    public Object get(Object key) {
    	Object obj = super.get(key);
    	
    	if (null == obj)
    		return null;
    	
        // only Strings can be localized
    	if (!(obj instanceof String))
            return obj;
    	
        // there is no resource bundle so there is no need to localize
        if (null == dp || null == dp.getResourceBundle())
            return obj;
        
        String str = (String) obj;

        // localize
        if (str.startsWith("%"))
            return dp.getResourceBundle().getString(str.substring(1));
        
        // there is resource bundle but the there is node need for 
        // localization (there is no '%' char at the begining of the value)
        return str;
    }
    
}