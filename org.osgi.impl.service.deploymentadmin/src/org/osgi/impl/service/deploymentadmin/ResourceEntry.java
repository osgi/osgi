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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;

import org.osgi.impl.service.deploymentadmin.WrappedJarInputStream.Entry;

public class ResourceEntry implements Serializable {

    private String     name;
    private Hashtable  attrs = new Hashtable();
    private String	   pid;
    private List   	   certChains = new LinkedList();

    public ResourceEntry(String name, Attributes attrs) {
        this(name, attrs, null);
    }
    
    private ResourceEntry(String name, Attributes attrs, String pid) {
        this.name = name;
        extractAttrs(attrs);
        this.pid = pid;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof ResourceEntry))
            return false;
        ResourceEntry other = (ResourceEntry) o;
        return getName().equals(other.getName());
    }
    
    public int hashCode() {
        return name.hashCode();
    }
    
    public String toString() {
        return getName();
    }
    
    Hashtable getAttrs() {
        return attrs;
    }
    
    public String getValue(String name) {
        return (String) attrs.get(name);
    }

    public String getName() {
        return name;
    }
    
    private void extractAttrs(Attributes as) {
        for (Iterator iter = as.keySet().iterator(); iter.hasNext();) {
            Attributes.Name key = (Attributes.Name) iter.next();
            Object value = as.getValue(key);
            attrs.put(key.toString(), value);
        }
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public List getCertChains() {
        return certChains;
    }

    public void updateCertificates(Entry entry) {
        certChains = entry.getCertificateChains();
    }
}
