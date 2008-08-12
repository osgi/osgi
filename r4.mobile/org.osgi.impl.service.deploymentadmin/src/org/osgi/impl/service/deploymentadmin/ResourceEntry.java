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
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;

import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;

/**
 * Represents a resource contained by a Deployment Package.<p> 
 * A Deployment Package maintains a list of its contained resources. 
 * The list consists of ResourceEntry. A ResourceEntry contains additional 
 * meta information about the bundle. E.g. PID.
 */
public class ResourceEntry implements Serializable {

    private String             resName;
    private CaseInsensitiveMap attrs;
    private String	           pid;
    private List   	           certChains = new LinkedList();

    public ResourceEntry(String resName, Attributes jarAttrs, DeploymentPackageImpl dp) {
        this.resName = resName;
        this.attrs = new CaseInsensitiveMap(jarAttrs, dp);
    }
    
    public boolean equals(Object o) {
    	if (null == o)
    		return false;
    	if (this == o)
    		return true;
        if (!(o instanceof ResourceEntry))
            return false;
        ResourceEntry other = (ResourceEntry) o;
        return getResName().equals(other.getResName());
    }
    
    public int hashCode() {
        return resName.hashCode();
    }
    
    public String toString() {
        return getResName();
    }
    
    public CaseInsensitiveMap getAttrs() {
        return attrs;
    }
    
    boolean isMissing() {
        String missing = (String) getAttrs().get(DAConstants.MISSING);
        if (null == missing)
            return false;
        return "true".equalsIgnoreCase(missing.trim());
    }
    
    public String getValue(String name) {
        return (String) attrs.get(name);
    }

    public String getResName() {
        return resName;
    }

    public String getPid() {
    	return (String) attrs.get(DAConstants.RP_PID);
    }

    public List getCertChains() {
        return certChains;
    }

    public void updateCertificates(Entry entry) {
        certChains = entry.getCertificateChainStringArrays();
    }

	public void update(ResourceEntry upd) {
	    this.pid = upd.pid;
	}

}
