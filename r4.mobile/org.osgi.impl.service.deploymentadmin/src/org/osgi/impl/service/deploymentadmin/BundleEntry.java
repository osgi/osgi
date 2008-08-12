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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.jar.Attributes;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;

/**
 * Represents an OSGi bundle contained by a Deployment Package.<p> 
 * A Deployment Package maintains a list of its contained bundles. 
 * The list consists of BundleEntries. A BundleEntry contains additional 
 * meta information about the bundle. E.g. resource name, 
 * PID (in case of customizers), etc.
 */
public class BundleEntry implements Serializable {
	
	private static final int UNDEFINED_BUNDLEID = -1;
    
    private String             resName;    // name in the manifest
    private String             symbName;   // Bundle-SymbolicName
    private String             version;    // Bundle-Version
    private Long               bundleId;   // ID returned by Bundle.getBundleId() 
    private Boolean            missing;    // is it a mising entry (see DeploymentPackage-Missing)
    private String             pid;        // if it's a customizer
    private String             location;   // the location of the bundle
    private CaseInsensitiveMap attrs;      // attributes in the manifest for the bundle
    
    private BundleEntry(String resName,
            String symbName, 
            String version, 
            long bundleId,
            boolean missing,
            Map jarAttrs,
            DeploymentPackageImpl dp) 
	{
        this.resName = resName;
		this.symbName = symbName;
		this.version = version;
		this.bundleId = new Long(bundleId);
		this.missing = new Boolean(missing);
		this.attrs = new CaseInsensitiveMap(jarAttrs, dp);
	}

    /**
     * This constructor is used when the BundleEntry is created from the 
     * corresponding manifest individual-section (name section).
     */
    public BundleEntry(String resName,
            		   String symbName, 
                       String version, 
                       boolean missing,
                       Attributes attrs, 
                       DeploymentPackageImpl dp) 
    {
        this(resName, symbName, version, UNDEFINED_BUNDLEID, missing, attrs, dp);
    }

    /**
     * This constructor is used when the BundleEntry is created from a Bundle object. 
     * It is needed when creating e.g. the "system" DP that has no manifest at all. 
     */
    public BundleEntry(final Bundle b) {
        Object[] triple = (Object[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                Object[] triple = new Object[3];
                triple[0] = (String) b.getHeaders().get(DAConstants.BUNDLE_SYMBOLIC_NAME);
                triple[1] = (String) b.getHeaders().get(DAConstants.BUNDLE_VERSION);
                triple[2] = new Long(b.getBundleId());
                return triple;
            }
        });
        this.symbName = (String) triple[0];
        this.version = (String) triple[1];
        this.bundleId = (Long) triple[2];
        missing = new Boolean(false);
    }
    
    /**
     * This constructor is used when the BundleEntry is created from a jar entry. 
     */
    public BundleEntry(Entry entry) {
        this(entry.getName(), entry.getAttributes().getValue(DAConstants.BUNDLE_SYMBOLIC_NAME),
             entry.getAttributes().getValue(DAConstants.BUNDLE_VERSION), UNDEFINED_BUNDLEID, entry.isMissing(),
             entry.getAttributes(), null);
    }

	public void update(BundleEntry upd) {
		bundleId = upd.bundleId;
		location = upd.location;
		pid = upd.pid;
	}

    /**
     * Two BundleEntries are equal if they have equal symbolic names and 
     * versions. 
     * @param obj the other BundleEntry 
     * @return <code>true<code> if the symbolic names and versions are equal
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (this == obj)
        	return true;
        if (!(obj instanceof BundleEntry))
            return false;
        BundleEntry other = (BundleEntry) obj;
        
        // sometimes the bundle symbolic name or version is null
        if (null != symbName && null != other.symbName && 
            null != version && null != other.version)
        	return symbName.equals(other.symbName) && version.equals(other.version);

        if (null != location && null != other.location)
        	return location.equals(other.location);
        
        // it cannot happen
        throw new RuntimeException("Internal error.");
    }

    /**
     * HashCode based on the symolic name and version.
     * @return hash code
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        // sometimes the bundle symbolic name or version is null
        if (null != symbName && null != version)
        	return (symbName + version).hashCode();

        if (null != location)
        	return location.hashCode();
        
        // it cannot happen
        throw new RuntimeException("Internal error.");
    }
    
    public String toString() {
        return "[BundleEntry SymbolicName: " + symbName + " Version: " + 
        		version + " BundleId: " + bundleId + "]";
    }

    public boolean isCustomizer() {
        String s = (String) attrs.get(DAConstants.CUSTOMIZER);
        if (null == s)
            return false;
        return Boolean.valueOf(s).booleanValue();
    }

    public boolean isMissing() {
        return missing.booleanValue();
    }

    public String getSymbName() {
        return symbName;
    }

    public Version getVersion() {
        return new Version(version);
    }

    public String getResName() {
        return resName;
    }
    
    public CaseInsensitiveMap getAttrs() {
        return attrs;
    }
    
    public long getBundleId() {
        if (null == bundleId)
            throw new RuntimeException("Internal error");
        return bundleId.longValue();
    }
    
    public void setBundleId(long bundleId) {
        this.bundleId = new Long(bundleId);
    }
    
    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setLocation(String location) {
    	this.location = location;
    }
    
    public String getLocation() {
    	return location;
    }

}

