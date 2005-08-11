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
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.*;

/**
 * Maintains a case insensitive map. It means that the keys are 
 * case insensitive.
 */
public class CaseInsensitiveMap implements Map, Serializable {
    
    private Hashtable             table = new Hashtable();
    private DeploymentPackageImpl dp; // it has the ResourceBundle for
                                      // localization
    
    /**
     * The <code>table</code> Hashtable contains this type of objects.
     * In this way the original (not capitalized) keys are also available.
     */
    private static class Entry implements Serializable {
        private String originalKey;
        private String value;
        
        private Entry(String rawKey, String value) {
            this.originalKey = rawKey;
            this.value = value;
        }
    }

    public CaseInsensitiveMap(Map other, DeploymentPackageImpl dp) {
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
        if (!(key instanceof String) && !(key instanceof Attributes.Name))
            throw new IllegalArgumentException("Only String and java.util.jar.Attributes.Name " +
                "keys are allowed");
        if (!(value instanceof String))
            throw new IllegalArgumentException("Only String value is allowed");
        String upperKey = key.toString().toUpperCase(); 
        return table.put(upperKey, new Entry(key.toString(), (String) value));
    }
    
    public Object get(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String upperKey = ((String) key).toUpperCase(); 
        Entry entry = (Entry) table.get(upperKey);
        
        // key was not found
        if (null == entry)
            return null;
        
        // there is no resource bundle so there is no need to localize
        if (null == dp || null == dp.getResourceBundle())
            return entry.value;
        
        // localize
        if (entry.value.startsWith("%"))
            return dp.getResourceBundle().getString(entry.value.substring(1));
        
        // there is resource bundle but the there is node need for 
        // localization (there is no '%' char at the begining of the value)
        return entry.value;
    }
    
    /**
     * Return the original (not capitalized) key.  
     * @param case insensitive key
     * @return the original (not capitalized) key
     */
    public String getRawKey(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String upperKey = ((String) key).toUpperCase(); 
        Entry entry = (Entry) table.get(upperKey);
        
        // key was not found
        if (null == entry)
            return null;
        
        return entry.originalKey;
    }
    
    public Set keySet() {
        return table.keySet();
    }

    public void clear() {
        table.clear();
    }

    public boolean containsKey(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String upperKey = ((String) key).toUpperCase();
        return table.containsKey(upperKey);
    }

    public boolean containsValue(Object obj) {
        return table.containsValue(obj);
    }

    public Set entrySet() {
        return table.entrySet();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public void putAll(Map other) {
        fill(other);
    }

    public Object remove(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String upperKey = ((String) key).toUpperCase();
        return table.remove(upperKey);
    }

    public int size() {
        return table.size();
    }

    public Collection values() {
        return table.values();
    }
    
}