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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.osgi.service.resolver.VersionRange;
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
    
    private String       fixPackRange; // String because VersionRange is not serializable	
    private String       dpName;
    private String       dpVersion;    // String because Version is not serializable
    private Long         id;
    
    private Map mainSection = new Hashtable();
    private Vector bundleEntries = new Vector();
    private Vector resourceEntries = new Vector();
    
    public DeploymentPackageImpl(Manifest manifest, int id, DeploymentAdminImpl da) 
    		throws DeploymentException 
    {
        this.id = new Long(id);
        this.da = da;
        
        processMainSection(manifest);
        processNameSections(manifest);
        
        DeploymentPackageVerifier.verify(this);
    }

    /*
     * Creates an empty DP
     */
    public DeploymentPackageImpl() {
        dpVersion = "0.0.0";
    }
    

    static DeploymentPackageImpl createSystemBundle(Set bundleEntries) {
        DeploymentPackageImpl dp = new DeploymentPackageImpl();
        dp.dpName = "System";
        dp.id = new Long(0);
        dp.bundleEntries = new Vector(bundleEntries);
        
        return dp;
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
        return "{" + getName() + " " + ( dpVersion == null ? null : getVersion() ) + "}";
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
        
    }
    
    private void processNameSections(Manifest manifest) throws DeploymentException {
        Map entries = manifest.getEntries();
        for (Iterator iter = entries.keySet().iterator(); iter.hasNext();) {
            String resPath = (String) iter.next();
            Attributes attrs = (Attributes) entries.get(resPath);
            String bSn = (String) attrs.getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
            String bVer = (String) attrs.getValue(DAConstants.BUNDLE_VERSION);
            String bCustStr = (String) attrs.getValue(DAConstants.CUSTOMIZER);
            String missingStr = (String) attrs.getValue(DAConstants.MISSING);
            boolean missing = (missingStr != null && "true".equalsIgnoreCase(missingStr.trim()));
            boolean isBundle = null != bSn && null != bVer; 
            boolean bCust = (bCustStr == null ? false : Boolean.valueOf(bCustStr).booleanValue());
            if (isBundle) {
                // bundle
                BundleEntry be = new BundleEntry(bSn, bVer, bCust, missing);
                bundleEntries.add(be);
            } else {
                // resource
                resourceEntries.add(new ResourceEntry(resPath, attrs));
            }
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
    
    VersionRange getFixPackRange() {
        return new VersionRange(fixPackRange);
    }
    
    Vector getResourceEntries() {
        return resourceEntries;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getBundleSymNameVersionPairs()
     */
    public String[][] getBundleSymNameVersionPairs() {
        checkStale();
        
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
    
    private void checkStale() {
        if (-1 == id.longValue())
            throw new IllegalStateException("Deployment package is stale");
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResources()
     */
    public String[] getResources() {
        checkStale();
        
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
        checkStale();
        
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
        checkStale();
        
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
        checkStale();
        
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
        checkStale();
        
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
        checkStale();
        if (0 == getId())
            throw new RuntimeException("\"System\" bundle cannot be uninstalled");
        
        da.uninstall(this);
        id = new Long(-1);
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstallForceful()
     */
    public boolean uninstallForceful() {
        checkStale();
        if (0 == getId())
            throw new RuntimeException("\"System\" bundle cannot be uninstalled");
        
        id = new Long(-1);
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

    void setDeploymentAdmin(DeploymentAdminImpl da) {
        this.da = da;
    }

    void setVersion(Version version) {
        this.dpVersion = version.toString();
    }
    
    /*
     * Checks whether the deployment package is valid.
     */
    private static class DeploymentPackageVerifier {

        public static void verify(DeploymentPackageImpl dp) throws DeploymentException {
            checkMainSection(dp);            
            checkNameSections(dp);
        }
        
        private static void checkMainSection(DeploymentPackageImpl dp)
        		throws DeploymentException
        {
            if (null == dp.dpName)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing deployment package name");
            
            if ("System".equals(dp.dpName))
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                		"The \"System\" deployment package name is reserved");
            
            if (null == dp.dpVersion)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing deployment package version in: " + dp.getName());
            try {
                Version version = new Version(dp.dpVersion);
            }
            catch (Exception e) {
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                        "Bad version in: " + dp, e);
            }
            
            if (dp.fixPack()) {
	            try {
	                VersionRange versionRange = new VersionRange(dp.fixPackRange);
	            }
	            catch (Exception e) {
	                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
	                        "Bad version range in: " + dp, e);
	            }
            }
            
            if (null == dp.dpName)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                        "Missing header in: " + dp + " header: " + DAConstants.DP_NAME);
         
            if (null == dp.dpVersion)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                        "Missing header in: " + dp + " header: " + DAConstants.DP_VERSION);        
        }
    
        private static void checkNameSections(DeploymentPackageImpl dp)
        		throws DeploymentException
        {
            for (Iterator iter = dp.bundleEntries.iterator(); iter.hasNext();) {
                BundleEntry be = (BundleEntry) iter.next();
                checkBundleEntry(dp, be);
            }
            
            for (Iterator iter = dp.resourceEntries.iterator(); iter.hasNext();) {
                ResourceEntry re = (ResourceEntry) iter.next();
                checkResourceEntry(dp, re);
            }
            
            Set s = new HashSet();
            for (Iterator iter = dp.bundleEntries.iterator(); iter.hasNext();) {
                BundleEntry be = (BundleEntry) iter.next();
                if (s.contains(be.getSymbName()))
                    throw new DeploymentException(
                            DeploymentException.CODE_BUNDLE_SHARING_VIOLATION,
                            "Bundle with symbolic name \"" + be.getSymbName() + "\" " +
                            "is allready present in the system");
                else
                    s.add(be.getSymbName());
            }
        }

        private static void checkBundleEntry(DeploymentPackageImpl dp, BundleEntry be) 
				throws DeploymentException
        {
            if (!dp.fixPack() && be.isMissing())
                	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                        DAConstants.MISSING + " header is only allowed in fix-pack (" +
                        dp + ")");
        }
        
        private static void checkResourceEntry(DeploymentPackageImpl dp, ResourceEntry re) 
        		throws DeploymentException 
        {
            if (!dp.fixPack() && re.isMissing())
            	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                    DAConstants.MISSING + " header is only allowed in fix-pack (" +
                    dp + ")");

            Hashtable attrs = re.getAttrs();
            String processor = (String) attrs.get(DAConstants.RP_PID);
            if (null == processor || "".equals(processor.trim()))
                	throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                        "Missing header in: " + dp + " header: " + DAConstants.RP_PID);
        }

    }
  
}