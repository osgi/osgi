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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.util.*;
import java.util.jar.*;

import org.osgi.framework.*;
import org.osgi.service.deploymentadmin.*;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;

public class DeploymentPackageImpl implements DeploymentPackage, Serializable {

    private transient DeploymentPackageCtx  dpCtx;

    private DeploymentPackageResourceBundle dprb;
    private CaseInsensitiveMap              mainSection;
    private Vector                      	bundleEntries = new Vector();
    private Vector                      	resourceEntries = new Vector();
    
    // List of String[]s
    private List certChains = new Vector();
    
    private Boolean stale = Boolean.FALSE;
    private String icon;
    
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
        dp.stale = new Boolean(true);
        
        return dp;
    }

    synchronized void update(DeploymentPackageImpl dp) {
    	this.dprb = dp.dprb;
    	this.mainSection = dp.mainSection;
    	this.bundleEntries = dp.bundleEntries;
    	this.resourceEntries = dp.resourceEntries;
    	this.certChains = dp.certChains;
    }
    
    boolean isEmpty() {
    	return getName().equals("");
    }
    
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (this == obj)
        	return true;
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
    
    public synchronized List getBundleEntries() {
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
    
    BundleEntry getBundleEntryByBundleId(long id) {
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getBundleId() == id)
                return be;
        }
        return null;
    }
    
    BundleEntry getBundleEntryByPid(String pid) {
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
    
    void setBundleEntryPossition(BundleEntry be, int pos) {
    	boolean res = bundleEntries.remove(be);
    	if (res) {
    		bundleEntries.insertElementAt(be, pos);
    	}
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
    
    public synchronized List getCertChains() {
        return certChains;
    }

    /**
     * @see DeploymentPackage#getBundleInfos()
     */
	public synchronized BundleInfo[] getBundleInfos() {
		//checkStale();

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
    public synchronized String[] getResources() {
        //checkStale();
        
    	dpCtx.checkPermission(this, DeploymentAdminPermission.METADATA);
    	
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
    public synchronized String getResourceHeader(String name, String header) {
        //checkStale();
        
    	dpCtx.checkPermission(this, DeploymentAdminPermission.METADATA);
    	
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
    public synchronized String getHeader(String name) {
        //checkStale();
    	
    	dpCtx.checkPermission(this, DeploymentAdminPermission.METADATA);
        
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
    public synchronized Version getVersion() {
        String s = (String) mainSection.get(DAConstants.DP_VERSION);
        if (null == s)
            return null;
        return new Version(s); 
    }

    /**
     * @see DeploymentPackage#getBundle(String)
     */
    public synchronized Bundle getBundle(final String symbName) {
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
    public synchronized ServiceReference getResourceProcessor(String resName) {
        checkStale();
        
        dpCtx.checkPermission(this, DeploymentAdminPermission.METADATA);
        
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
        dpCtx.checkPermission(this, DeploymentAdminPermission.UNINSTALL_FORCED);
        setStale();
        return dpCtx.uninstallForced(this);
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
    
	public synchronized List getResourceEntries() {
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
    
    public synchronized CaseInsensitiveMap getMainSection() {
        return mainSection;
    }
    
    DeploymentPackageCtx getDpCtx() {
    	return dpCtx;
    }
    

	public String getDisplayName() {
        return (String) mainSection.get(DAConstants.DP_DISPLAY_NAME);
	}

	public URL getIcon() {
		if (icon != null) {
			try {
				return new URL("file:///"+new File(icon).getAbsolutePath());
			} catch (MalformedURLException e) {
				//TODO log error?
			}
		}
		String iconHeader = (String) mainSection.get(DAConstants.DP_ICON);
		if (iconHeader != null) {
			try {
				URL url = new URL(iconHeader);
				InputStream is = url.openStream();
				String urlString = url.toString(); 
				int ind = urlString.lastIndexOf('/');
				String fileName = "icon.gif";
				if (ind > 0) {
					fileName = urlString.substring(ind+1);
				} 
				String iconLocation = storeIcon(fileName, this, is);
				if (iconLocation != null) {
					icon = iconLocation;
					return getIcon();
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public static String storeIcon(String iconFileName, DeploymentPackageImpl dp, InputStream is) {
    	String iconCacheStore = DAConstants.ICON_STORAGE_ROOT;
    	String iconFile = iconCacheStore + '/' + dp.getName() + '/' + iconFileName;
    	new File(iconFile).getParentFile().mkdirs();
    	byte[] buff = new byte[2048];
		try {
	    	OutputStream out = new FileOutputStream(iconFile);
	    	int read = is.read(buff);
	    	while (read >= 0) {
				out.write(buff, 0, read);
				read = is.read(buff);
			}
	    	out.close();
	    	is.close();
		} catch (Exception e) {
			// TODO log error
			return null;
		}
		return iconFile;
	}
	
	public static void clearCache(DeploymentPackageImpl dp) {
		String iconCacheStore = DAConstants.ICON_STORAGE_ROOT;
		String iconsDir = iconCacheStore + '/' + dp.getName();
		File iconsDirFile = new File(iconsDir);
		if (iconsDirFile.exists()) {
			deleteDir(iconsDirFile);
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.exists()) {
			if (dir.isFile())
				return dir.delete();
			else {
				String[] list = dir.list();
				if (list != null) {
					int len = list.length;
					for (int i = 0; i < len; i++)
						deleteDir(new File(dir.getAbsolutePath()
								+ File.separator + list[i]));
				}
				return dir.delete();
			}
		}
		else {
			return false;
		}
	}

}