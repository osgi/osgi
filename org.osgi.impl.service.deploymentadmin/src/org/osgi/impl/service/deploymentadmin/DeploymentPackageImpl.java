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

import java.io.IOException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.impl.service.deploymentadmin.WrappedJarInputStream.Entry;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

public class DeploymentPackageImpl implements DeploymentPackage, Serializable {

    private transient DeploymentAdminImpl da;
    
    // TODO create a VersionRange class
    private String  fixPackRange;	
    private String  dpName;
    private String  dpVersion;
    private Integer id;
    
    private Map mainSection = new Hashtable();
    private Vector bundleEntries = new Vector();
    private Vector resourceEntries = new Vector();
    
    /*
     * to create a non-empty DP
     */ 
    public DeploymentPackageImpl(Manifest manifest, int id, DeploymentAdminImpl da) throws DeploymentException {
        this.id = new Integer(id);
        this.da = da;
        
        processMainSection(manifest);
        processNameSections(manifest);
    }

    /*
     * to create an empty DP
     */
    public DeploymentPackageImpl() {
    }
    
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentPackage))
            return false;
        DeploymentPackage other = (DeploymentPackage) obj;
        return getName().equals(other.getName()) &&
               getVersion().equals(other.getVersion());
    }
    
    boolean equalsIgnoreVersion(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentPackage))
            return false;
        DeploymentPackage other = (DeploymentPackage) obj;
        return getName().equals(other.getName());
    }
    
    public int hashCode() {
        return (getName() + getVersion()).hashCode();
    }
    
    public String toString() {
        return "{" + getName() + " " + getVersion() + "}";
    }

    private void processMainSection(Manifest manifest) throws DeploymentException {
        dpName = manifest.getMainAttributes().getValue(DAConstants.DP_NAME);
        fixPackRange = manifest.getMainAttributes().getValue(DAConstants.DP_FIXPACK);
        dpVersion = manifest.getMainAttributes().getValue(DAConstants.DP_VERSION);
        
        Attributes attrs = manifest.getMainAttributes();
        for (Iterator iter = attrs.keySet().iterator(); iter.hasNext();) {
            Attributes.Name key = (Attributes.Name) iter.next();
            Object value = attrs.getValue(key);
            mainSection.put(key.toString(), value);
        }
        
        checkMainSection();
    }
    
    private void checkMainSection() throws DeploymentException {
        if (null == dpName)
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                    "Missing header: " + DAConstants.DP_NAME);
     
        if (null == dpVersion)
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                    "Missing header: " + DAConstants.DP_VERSION);
        
        // TODO check fixpack range
    }

    private void processNameSections(Manifest manifest) throws DeploymentException {
        Map entries = manifest.getEntries();
        for (Iterator iter = entries.keySet().iterator(); iter.hasNext();) {
            String resPath = (String) iter.next();
            Attributes attrs = (Attributes) entries.get(resPath);
            String bSn = (String) attrs.getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
            String bVer = (String) attrs.getValue(DAConstants.BUNDLE_VERSION);
            String bCustStr = (String) attrs.getValue(DAConstants.CUSTOMIZER);
            boolean isBundle = null != bSn && null != bVer; 
            boolean bCust = (bCustStr == null ? false : Boolean.valueOf(bCustStr).booleanValue());
            if (isBundle) {
                // bundle
                BundleEntry be = new BundleEntry(bSn, bVer, bCust);
                bundleEntries.add(be);
            } else {
                // resource
                resourceEntries.add(new ResourceEntry(resPath, attrs));
            }
            
            checkNameSection(resPath, attrs, isBundle);
        }
    }
    
    private void checkNameSection(String resPath, Attributes attrs, boolean isBundle) 
    		throws DeploymentException 
    {
        if (fixPack()) {
            String missing = attrs.getValue(DAConstants.MISSING);
            if (null == missing)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing \"" + DAConstants.MISSING + "\" header in \"" +
                        resPath + "\" section");
        }
        
        if (!isBundle) {
            String rp = attrs.getValue(DAConstants.RP_PID);
            if (null == rp)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing \"" + DAConstants.RP_PID + "\" header in \"" +
                        resPath + "\" section");
        }
    }
    
    Vector getBundleEntries() {
        return bundleEntries;
    }
    
    void updateBundleEntry(BundleEntry be) {
        int index = bundleEntries.indexOf(be);
        BundleEntry e = (BundleEntry) bundleEntries.get(index);
        e.setId(be.getId());
        e.setSymbName(be.getSymbName());
        e.setVersion(be.getVersion());
    }
    
    void updateResourceEntry(Entry entry) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getName().equals(entry.getName()))
                re.updateCertificates(entry);
        }
    }

    
    Vector getResourceEntries() {
        return resourceEntries;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getBundleSymNameVersionPairs()
     */
    public String[][] getBundleSymNameVersionPairs() {
        String[][] ret = new String[bundleEntries.size()][2];
        int i = 0;
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            ret[i][0] = be.getSymbName();
            ret[i][1] = be.getVersion();
            ++i;    
        }
        return ret;
    }
    
    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResources()
     */
    public String[] getResources() {
        String[]ret = new String[resourceEntries.size()];
        int i = 0;
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            ret[i] = re.getName();
            ++i;    
        }
        return ret;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResourceHeader(java.lang.String, java.lang.String)
     */
    public String getResourceHeader(String name, String header) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getName().equals(name))
                return re.getValue(header);
        }
        return null;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getHeader(java.lang.String)
     */
    public String getHeader(String name) {
        return (String) mainSection.get(name);
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getId()
     */
    public long getId() {
        return null == id ? -1 : id.longValue();
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getName()
     */
    public String getName() {
        return dpName;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getVersion()
     */
    public Version getVersion() {
        return new Version(dpVersion); 
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getBundle(java.lang.String)
     */
    public Bundle getBundle(final String symbName) {
        Bundle[] bs = da.getBundleContext().getBundles();
        for (int i = 0; i < bs.length; i++) {
            final Bundle b = bs[i];
            String location = (String) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return b.getLocation();
                }});
            if (null == location)
                return null;
            if (location.equals(symbName))
                return b;
        }
        return null;
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResourceProcessor(java.lang.String)
     */
    public ServiceReference getResourceProcessor(String resName) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getName().equals(resName)) {
                	try {
                        ServiceReference[] refs = da.getBundleContext().getServiceReferences(
                                ResourceProcessor.class.getName(),
                                "(" + Constants.SERVICE_PID + "=" + re.getPid() + ")");
                        if (null != refs && refs.length != 0)
                            return refs[0];
                    }
                    catch (InvalidSyntaxException e) {
                        throw new RuntimeException("Internal error");
                    }
            }
        }
        return null;
    }

    /**
     * @throws DeploymentException
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstall()
     */
    public void uninstall() throws DeploymentException {
        da.uninstall(this);
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstallForceful()
     */
    public boolean uninstallForceful() {
        return da.uninstallForceful(this);
    }

    boolean fixPack() {
        return fixPackRange != null;
    }

    void setProcessorPid(String resName, String pid) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getName().equals(resName)) {
                re.setPid(pid);
            }
        }
    }

    public void setDeploymentAdmin(DeploymentAdminImpl da) {
        this.da = da;
    }

    public void setVersion(Version version) {
        this.dpVersion = version.toString();
    }
  
}