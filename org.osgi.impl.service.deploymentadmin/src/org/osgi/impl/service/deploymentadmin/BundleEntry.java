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
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;

public class BundleEntry implements Serializable {
    
    private String             resName;    // name in the manifest
    private String             symbName;   // Bundle-SymbolicName
    private String             version;
    private Long               bundleId;
    private Boolean            missing;
    private String             pid;        // if it's a customizer
                                         
    private CaseInsensitiveMap attrs = new CaseInsensitiveMap();
    private List               certChains; // list of cerificate chains
                                           // (one chain is a String[]
    
    public BundleEntry(String name,
            String symbName, 
            String version, 
            long id,
            boolean missing,
            Map jarAttrs) 
	{
        this.resName = name;
		this.symbName = symbName;
		this.version = version;
		this.bundleId = new Long(id);
		this.missing = new Boolean(missing);
		this.attrs = new CaseInsensitiveMap(jarAttrs);
	}

    public BundleEntry(String name,
            		   String symbName, 
                       String version, 
                       boolean missing,
                       Attributes attrs) 
    {
        this(name, symbName, version, -1, missing, attrs);
    }
    
    public BundleEntry(BundleEntry other) {
        this(other.getResName(), other.getSymbName(), other.version,
             other.getBundleId(), other.isMissing(), other.attrs);
    }
    
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
    
    public BundleEntry(Entry entry) {
        this(entry.getName(), entry.getAttributes().getValue(DAConstants.BUNDLE_SYMBOLIC_NAME),
             entry.getAttributes().getValue(DAConstants.BUNDLE_VERSION), -1, entry.isMissing(),
             entry.getAttributes());
    }

    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof BundleEntry))
            return false;
        BundleEntry other = (BundleEntry) obj;
        
        // TODO bit ugly
        return ((null == symbName && null == other.symbName) || symbName.equals(other.symbName)) &&
               ((null == version && null == other.version) || version.equals(other.version));
    }
    
    public int hashCode() {
        return (symbName + version).hashCode();
    }
    
    public String toString() {
        return "[SymbolicName: " + symbName + " Version: " + version + "]";
    }

    // PluginDeployed uses this
    public List getCertificateChainStringArrays() {
        return certChains;
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
    
    CaseInsensitiveMap getAttrs() {
        return attrs;
    }
    
    void setAttrs(CaseInsensitiveMap attrs) {
        this.attrs = attrs;
    }

    public void setSymbName(String symbName) {
        this.symbName = symbName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getValue(String header) {
        return (String) attrs.get(header);
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

}

