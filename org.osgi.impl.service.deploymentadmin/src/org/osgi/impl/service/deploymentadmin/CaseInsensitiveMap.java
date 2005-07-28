/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.impl.service.deploymentadmin;

import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.*;


public class CaseInsensitiveMap implements Map, Serializable {
    
    private Hashtable             table = new Hashtable();
    private DeploymentPackageImpl dp;
    
    private static class Entry implements Serializable {
        private String rawKey;
        private String value;
        
        private Entry(String rawKey, String value) {
            this.rawKey = rawKey;
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
        if (null == entry)
            return null;
        if (null == dp.getResourceBundle())
            return entry.value;
        if (entry.value.startsWith("%")) {
            return dp.getResourceBundle().getString(entry.value.substring(1));
        }
        return entry.value;
    }
    
    public String getRawKey(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException("Only String key is allowed");
        String upperKey = ((String) key).toUpperCase(); 
        Entry entry = (Entry) table.get(upperKey);
        if (null == entry)
            return null;
        return entry.rawKey;
    }
    
    public Set keySet() {
        return table.keySet();
    }

    public void clear() {
        table.clear();
    }

    public boolean containsKey(Object var0) {
        return table.containsKey(var0);
    }

    public boolean containsValue(Object var0) {
        return table.containsValue(var0);
    }

    public Set entrySet() {
        return table.entrySet();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public void putAll(Map var0) {
        fill(var0);
    }

    public Object remove(Object var0) {
        return table.remove(var0);
    }

    public int size() {
        return table.size();
    }

    public Collection values() {
        return table.values();
    }
    
}