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
import java.util.Iterator;
import java.util.List;
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
import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;

public class DeploymentPackageImpl implements DeploymentPackage, Serializable {

    private transient DeploymentAdminImpl da;
    
    private Long               id;
    private CaseInsensitiveMap mainSection     = new CaseInsensitiveMap();
    private Vector             bundleEntries   = new Vector();
    private Vector             resourceEntries = new Vector();
    private List               certChains      = new Vector();
    
    public DeploymentPackageImpl(Manifest manifest, int id, DeploymentAdminImpl da,
            List certChains) throws DeploymentException 
    {
        this.id = new Long(id);
        this.da = da;
        if (null != certChains)
            this.certChains = certChains;
        
        processMainSection(manifest);
        processNameSections(manifest);
        
        new DeploymentPackageVerifier().verify(this);
    }

    /*
     * Copy constructor
     */
    public DeploymentPackageImpl(DeploymentPackageImpl other) {
        this.id = other.id;
        this.da = other.da;
        this.certChains = other.certChains;
        this.mainSection = new CaseInsensitiveMap(other.mainSection);
        bundleEntries = (Vector) other.bundleEntries.clone();
        resourceEntries = (Vector) other.resourceEntries.clone();
    }

    /*
     * Creates an empty DP
     */
    public DeploymentPackageImpl() {
        mainSection.put(DAConstants.DP_NAME, "");
        mainSection.put(DAConstants.DP_VERSION, "0.0.0");
    }

    static DeploymentPackageImpl createSystemBundle(Set bundleEntries) {
        DeploymentPackageImpl dp = new DeploymentPackageImpl();
        dp.mainSection.put(DAConstants.DP_NAME, "System");
        dp.mainSection.put(DAConstants.DP_VERSION, "0.0.0");
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
        return "{" + getName() + " " + ( getVersion() == null ? null : getVersion() ) + "}";
    }

    private void processMainSection(Manifest manifest) throws DeploymentException {
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
                BundleEntry be = new BundleEntry(resPath, bSn, bVer, bCust, missing, attrs);
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
    
    BundleEntry getBundleEntryByName(String name) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getName().equals(name))
                return be;
        }
        return null;
    }
    
    BundleEntry getBundleEntry(String symbName, Version version) throws DeploymentException {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            boolean b1 = be.getSymbName().equals(symbName);
            boolean b2  = be.getVersion().equals(version);
            if (be.getSymbName().equals(symbName) && be.getVersion().equals(version))
                return be;
        }
        return null;
    }
    
    void updateBundleEntry(Bundle b) throws DeploymentException {
        BundleEntry newBe = new BundleEntry(b);
        BundleEntry be = getBundleEntry(newBe.getSymbName(), newBe.getVersion());
        if (null == be)
            throw new RuntimeException("Internal error");
        be.update(b);
    }


    BundleEntry getBundleEntry(String symbName) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getSymbName().equals(symbName))
                return be;
        }
        return null;
    }

    void updateResourceEntry(Entry entry) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getName().equals(entry.getName())) {
                re.updateCertificates(entry);
                re.setAttrs(entry.getAttributes());
            }
        }
    }
    
    VersionRange getFixPackRange() {
        return new VersionRange((String) mainSection.get(DAConstants.DP_FIXPACK));
    }
    
    Vector getResourceEntries() {
        return resourceEntries;
    }
    
    List getCertChains() {
        return certChains;
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
            ret[i][1] = be.getVersion().toString();
            ++i;    
        }
        return ret;
    }
    
    private void checkStale() {
        if (isStale())
            throw new IllegalStateException("Deployment package is stale");
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResources()
     */
    public String[] getResources() {
        checkStale();
        
        String[]ret = new String[resourceEntries.size() + bundleEntries.size()];
        int i = 0;
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            ret[i] = be.getName();
            ++i;    
        }
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
        
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getName().equals(name))
                return be.getValue(header);
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

    public boolean isStale() {
        long l = (null == id ? -1 : id.longValue());
        return l == -1;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getName()
     */
    public String getName() {
        return (String) mainSection.get(DAConstants.DP_NAME);
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getVersion()
     */
    public Version getVersion() {
        String s = (String) mainSection.get(DAConstants.DP_VERSION);
        if (null == s)
            return null;
        return new Version(s); 
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
        if (0 == id.longValue())
            throw new RuntimeException("\"System\" bundle cannot be uninstalled");
        
        da.checkPermission(this, DeploymentAdminPermission.ACTION_UNINSTALL);
        
        da.uninstall(this);
        id = new Long(-1);
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstallForced()
     */
    public boolean uninstallForced() {
        checkStale();
        if (0 == id.longValue())
            throw new RuntimeException("\"System\" bundle cannot be uninstalled");
        
        da.checkPermission(this, DeploymentAdminPermission.ACTION_UNINSTALL_FORCED);
        
        id = new Long(-1);
        return da.uninstallForced(this);
    }

    boolean fixPack() {
        return null != mainSection.get(DAConstants.DP_FIXPACK);
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
        mainSection.put(DAConstants.DP_VERSION, version.toString());
    }
    
    /*
     * Checks whether the deployment package is valid.
     */
    private class DeploymentPackageVerifier {
        
        public void verify(DeploymentPackageImpl dp) throws DeploymentException {
            checkMainSection(dp);            
            checkNameSections(dp);
        }
        
        private void checkMainSection(DeploymentPackageImpl dp)
        		throws DeploymentException
        {
            if (null == dp.getName())
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing deployment package name");
            
            if ("System".equals(dp.getName()))
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                		"The \"System\" deployment package name is reserved");
            
            if (null == dp.getVersion())
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing deployment package version in: " + dp.getName());
            try {
                String s = (String) dp.mainSection.get(DAConstants.DP_VERSION);
                Version version = new Version(s);
            }
            catch (Exception e) {
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                        "Bad version in: " + dp, e);
            }
            
            if (dp.fixPack()) {
	            try {
	                String s = (String) dp.mainSection.get(DAConstants.DP_FIXPACK);
	                VersionRange versionRange = new VersionRange(s);
	            }
	            catch (Exception e) {
	                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
	                        "Bad version range in: " + dp, e);
	            }
            }
            
            if (null == dp.getName())
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                        "Missing header in: " + dp + " header: " + DAConstants.DP_NAME);
         
            if (null == dp.getVersion())
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                        "Missing header in: " + dp + " header: " + DAConstants.DP_VERSION);        
        }
    
        private void checkNameSections(DeploymentPackageImpl dp)
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
            
            // this check only inside one dp
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

        private void checkBundleEntry(DeploymentPackageImpl dp, BundleEntry be) 
				throws DeploymentException
        {
            checkResourceName(be.getName());
            
            if (!dp.fixPack() && be.isMissing())
                	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                        DAConstants.MISSING + " header is only allowed in fix-pack (" +
                        dp + ")");
            
            Set set = da.getDeploymentPackages();
            for (Iterator iter = set.iterator(); iter.hasNext();) {
                DeploymentPackageImpl eDp = (DeploymentPackageImpl) iter.next();
                if (dp.getName().equals(eDp.getName()))
                    continue;
                for (Iterator iterator = eDp.getBundleEntries().iterator(); iterator.hasNext();) {
                    BundleEntry	eBe	= (BundleEntry) iterator.next();
                    if (be.getSymbName().equals(eBe.getSymbName()))
                        throw new DeploymentException(DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, 
                                "Bundle " + be + " is already installed");
                }
            }
        }
        
        private void checkResourceName(String name) throws DeploymentException {
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (ch >= 'a' && ch <= 'z')
                    return;
                if (ch >= 'A' && ch <= 'Z')
                    return;
                if (ch >= '0' && ch <= '9')
                    return;
                if (ch == '_' || ch == '.' || ch == '-' || ch == '/')
                    return;
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                        "Character '" + ch + "' is not allowed in resource names");
            }
        }

        private void checkResourceEntry(DeploymentPackageImpl dp, ResourceEntry re) 
        		throws DeploymentException 
        {
            if (!dp.fixPack() && re.isMissing())
            	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                    DAConstants.MISSING + " header is only allowed in fix-pack (" +
                    dp + ")");
        }

    }
  
}