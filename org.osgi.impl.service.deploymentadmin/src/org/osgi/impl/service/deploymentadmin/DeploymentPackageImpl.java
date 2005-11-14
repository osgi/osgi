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
import java.util.LinkedList;
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
import org.osgi.service.deploymentadmin.BundleInfo;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;

public class DeploymentPackageImpl implements DeploymentPackage, Serializable {

    private transient DeploymentPackageCtx  dpCtx;

    private DeploymentPackageResourceBundle dprb;
    private CaseInsensitiveMap              mainSection;
    private LinkedList                      bundleEntries = new LinkedList();
    private LinkedList                      resourceEntries = new LinkedList();
    
    // List of String[]s
    private List certChains = new Vector();
    
    private Boolean stale = Boolean.FALSE;
    
    private DeploymentPackageImpl() {
    }
    
    DeploymentPackageImpl(DeploymentPackageCtx dpCtx, Manifest manifest,
            List certChains) throws DeploymentException 
    {
        if (null == dpCtx)
            throw new IllegalArgumentException("Internal error");
        this.dpCtx = dpCtx;

        if (null == manifest)
            mainSection = new CaseInsensitiveMap(null, this);
        else 
            mainSection = new CaseInsensitiveMap(manifest.getMainAttributes(), this);
        processNameSections(manifest);

        if (null != certChains)
            this.certChains = certChains;
        
        new DeploymentPackageVerifier().verify(this);
    }

    /*
     * Creates an empty DP
     */
    static DeploymentPackageImpl createEmpty(DeploymentPackageCtx dpCtx) {
        if (null == dpCtx)
            throw new IllegalArgumentException("Internal error");
        
        DeploymentPackageImpl dp = new DeploymentPackageImpl();
        dp.dpCtx = dpCtx;
        dp.mainSection = new CaseInsensitiveMap(null, dp);
        dp.mainSection.put(DAConstants.DP_NAME, "");
        dp.mainSection.put(DAConstants.DP_VERSION, "0.0.0");
        
        return dp;
    }

    /*
     * Creates the Systemp DP
     */
    /*static DeploymentPackageImpl createSystem(DeploymentAdminImpl da, Set bundleEntries) {
        if (null == da)
            throw new IllegalArgumentException("Internal error");
        
        DeploymentPackageImpl dp = new DeploymentPackageImpl();
        dp.mainSection = new CaseInsensitiveMap(null, dp);
        dp.mainSection.put(DAConstants.DP_NAME, "system");
        dp.mainSection.put(DAConstants.DP_VERSION, "0.0.0");
        dp.bundleEntries = new LinkedList(bundleEntries);
        
        return dp;
    }*/

    /*
     * Creates the Systemp DP
     */
    static DeploymentPackageImpl createOriginalSystemDp(DeploymentAdminImpl da, Set bundleEntries) {
        if (null == da)
            throw new IllegalArgumentException("Internal error");
        
        DeploymentPackageImpl dp = new DeploymentPackageImpl();
        dp.mainSection = new CaseInsensitiveMap(null, dp);
        dp.mainSection.put("Manifest-Version", "1.0");
        dp.mainSection.put(DAConstants.DP_NAME, "system");
        dp.mainSection.put(DAConstants.DP_VERSION, "0.0.0");
        dp.bundleEntries = new LinkedList(bundleEntries);
        
        return dp;
    }

    /*boolean isEmpty() {
        return getName().equals("") && getVersion().equals(new Version("0.0.0"));
    }*/
    
    /*boolean isSystem() {
        return getName().equals("system") && getVersion().equals(new Version("0.0.0"));
    }*/
    
    boolean isSystem() {
        return getName().equals("system");
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
    
    boolean equalsIgnoreVersion(DeploymentPackage other) {
        if (null == other)
            return false;
        return getName().equals(other.getName());
    }
    
    public int hashCode() {
        return (getName() + getVersion()).hashCode();
    }
    
    public String toString() {
        return "[Deployment Package: " + getName() + " " + getVersion() + "]";
    }

    private void processNameSections(Manifest manifest) throws DeploymentException {
        if (null == manifest)
            return;
        
        Map entries = manifest.getEntries();
        for (Iterator iter = entries.keySet().iterator(); iter.hasNext();) {
            String resPath = (String) iter.next();
            Attributes attrs = (Attributes) entries.get(resPath);
            String bSn = attrs.getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
            String bVer = attrs.getValue(DAConstants.BUNDLE_VERSION);
            String missingStr = attrs.getValue(DAConstants.MISSING);
            boolean missing = (missingStr != null && "true".equalsIgnoreCase(missingStr.trim()));
            boolean isBundle = null != bSn && null != bVer; 
            if (isBundle) {
                // bundle
                BundleEntry be = new BundleEntry(resPath, bSn, bVer, missing, attrs, this);
                bundleEntries.add(be);
            } else {
                // resource
                resourceEntries.add(new ResourceEntry(resPath, attrs, this));
            }
        }
    }
    
    public List getBundleEntries() {
        return bundleEntries;
    }

    boolean contains(BundleEntry be) {
        return bundleEntries.contains(be);
    }
    
    void add(BundleEntry be) {
        bundleEntries.add(be);
    }

    void remove(BundleEntry be) {
        bundleEntries.remove(be);
    }
    
    public BundleEntry getBundleEntryByBundleId(long id) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getBundleId() == id)
                return be;
        }
        return null;
    }
    
    public BundleEntry getBundleEntryByPid(String pid) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (null == be.getPid())
                continue;
            if (be.getPid().equals(pid))
                return be;
        }
        return null;
    }
    
    BundleEntry getBundleEntryByName(String name) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getResName().equals(name))
                return be;
        }
        return null;
    }
    
    BundleEntry getBundleEntry(String symbName, Version version) throws DeploymentException {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getSymbName().equals(symbName) && be.getVersion().equals(version))
                return be;
        }
        return null;
    }
    
    BundleEntry getBundleEntry(String symbName) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getSymbName().equals(symbName))
                return be;
        }
        return null;
    }

    VersionRange getFixPackRange() {
        String s = (String) mainSection.get(DAConstants.DP_FIXPACK);
        if (null == s)
            return null;
        return new VersionRange(s);
    }
    
    ResourceEntry getResourceEntryByName(String name) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry entry = (ResourceEntry) iter.next();
            if (entry.getResName().equals(name))
                return entry;
        }
        return null;
    }
    
    public List getCertChains() {
        return certChains;
    }

    /**
     * @see DeploymentPackage#getBundleInfos()
     */
	public BundleInfo[] getBundleInfos() {
		checkStale();

		dpCtx.checkPermission(this, DeploymentAdminPermission.METADATA);
		
		BundleInfo[] ret = new BundleInfo[bundleEntries.size()];
        int i = 0;
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            ret[i] = new BundleInfoImpl(be.getSymbName(), be.getVersion());
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
            ret[i] = be.getResName();
            ++i;    
        }
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            ret[i] = re.getResName();
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
            if (re.getResName().equals(name))
                return re.getValue(header);
        }
        
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getResName().equals(name))
                return (String) be.getAttrs().get(header);
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

    public synchronized boolean isStale() {
        return stale.booleanValue();
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
     * @see DeploymentPackage#getBundle(String)
     */
    public Bundle getBundle(final String symbName) {
        checkStale();
        
        dpCtx.checkPermission(this, DeploymentAdminPermission.METADATA);
        
        Bundle[] bs = dpCtx.getBundleContext().getBundles();
        for (int i = 0; i < bs.length; i++) {
            final Bundle b = bs[i];
            String location = (String) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return b.getLocation();
                }});
            if (null == location)
                continue;
            if (location.equals(DeploymentAdminImpl.location(symbName, null)))
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
            if (re.getResName().equals(resName)) {
                	try {
                        ServiceReference[] refs = dpCtx.getBundleContext().getServiceReferences(
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
        if (isSystem())
            throw new RuntimeException("\"System\" deployment package cannot be uninstalled");
        
        dpCtx.checkPermission(this, DeploymentAdminPermission.UNINSTALL);
        
        dpCtx.uninstall(this);
        
        setStale();
    }

    /**
     * @return
     * @throws DeploymentException 
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstallForced()
     */
    public boolean uninstallForced() throws DeploymentException {
        checkStale();
        if (isSystem())
            throw new RuntimeException("\"System\" deployment package cannot be uninstalled");
        
        dpCtx.checkPermission(this, DeploymentAdminPermission.UNINSTALL_FORCED);
        
        setStale();
        
        return dpCtx.uninstallForced(this);
    }

    void setProcessorPid(String resName, String pid) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getResName().equals(resName)) {
                re.setPid(pid);
            }
        }
    }

    void setDeploymentPackageCtx(DeploymentPackageCtx dpCtx) {
        this.dpCtx = dpCtx;
    }

    void setVersion(Version version) {
        mainSection.put(DAConstants.DP_VERSION, version.toString());
    }
    
    synchronized void setStale() {
        stale = Boolean.TRUE;
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
            
            if ("system".equals(dp.getName()))
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                		"The \"System\" deployment package name is reserved");
            
            if (null == dp.getVersion())
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing deployment package version in: " + dp.getName());
            
            try {
                String s = (String) dp.mainSection.get(DAConstants.DP_VERSION);
                new Version(s);
            }
            catch (Exception e) {
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER,
                        "Bad version in: " + dp, e);
            }
            
            if (null != dp.getFixPackRange()) {
	            try {
	                String s = (String) dp.mainSection.get(DAConstants.DP_FIXPACK);
	                new VersionRange(s);
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
                s.add(be.getSymbName());
            }
        }

        private void checkBundleEntry(DeploymentPackageImpl dp, BundleEntry be) 
				throws DeploymentException
        {
            checkResourceName(be.getResName());
            
            if (null == dp.getFixPackRange() && be.isMissing())
                	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                        DAConstants.MISSING + " header is only allowed in fix-pack (" +
                        dp + ")");
            
            Set set = dpCtx.getDeploymentPackages();
            for (Iterator iter = set.iterator(); iter.hasNext();) {
                DeploymentPackageImpl eDp = (DeploymentPackageImpl) iter.next();
                if (dp.getName().equals(eDp.getName()))
                    continue;
                for (Iterator iter2 = eDp.getBundleEntries().iterator(); iter2.hasNext();) {
                    BundleEntry	eBe	= (BundleEntry) iter2.next();
                    if (be.getSymbName().equals(eBe.getSymbName()))
                        throw new DeploymentException(DeploymentException.CODE_BUNDLE_SHARING_VIOLATION, 
                                "Bundle " + be + " is already installed by the package " + eDp);
                }
            }
        }
        
        private void checkResourceName(String name) throws DeploymentException {
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if (ch >= 'a' && ch <= 'z')
                    continue;
                if (ch >= 'A' && ch <= 'Z')
                    continue;
                if (ch >= '0' && ch <= '9')
                    continue;
                if (ch == '_' || ch == '.' || ch == '-' || ch == '/')
                    continue;
                throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                        "Character '" + ch + "' is not allowed in resource names");
            }
        }

        private void checkResourceEntry(DeploymentPackageImpl dp, ResourceEntry re) 
        		throws DeploymentException 
        {
            if (null == dp.getFixPackRange() && re.isMissing())
            	throw new DeploymentException(DeploymentException.CODE_BAD_HEADER, 
                    DAConstants.MISSING + " header is only allowed in fix-pack (" +
                    dp + ")");
        }

    }

    public List getResourceEntries() {
        return resourceEntries;
    }

    void remove(ResourceEntry re) {
        resourceEntries.remove(re);
    }

    boolean contains(ResourceEntry re) {
        return resourceEntries.contains(re);
    }

    void setResourceBundle(DeploymentPackageResourceBundle dprb) {
        this.dprb = dprb;  
    }
    
    DeploymentPackageResourceBundle getResourceBundle() {
        return dprb;  
    }
    
    public CaseInsensitiveMap getMainSection() {
        return mainSection;
    }

}