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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.dmt.DmtAdmin;
import org.osgi.service.dmt.DmtDataPlugin;
import org.osgi.service.dmt.DmtExecPlugin;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentAdminImpl implements DeploymentAdmin, BundleActivator {
    
	private BundleContext 		  context;
	private ServiceRegistration   registration;
    private Logger 				  logger;
    private DeploymentSessionImpl session;
    private KeyStore              keystore;
    private TrackerEvent          trackEvent;
    private TrackerDmt            trackDmt;
    private String                fwBundleDir;
    private boolean               cancelled;
    
    // Dmt plugin registrations
    private ServiceRegistration regDmtPlugin;
    
    /*
     * Class to track the event admin
     */
    private class TrackerEvent extends ServiceTracker {
        public TrackerEvent() {
            super(DeploymentAdminImpl.this.context, 
                    EventAdmin.class.getName(), null);
        }
    }

    /*
     * Class to track the Dmt Admin
     */
    private class TrackerDmt extends ServiceTracker {
        public TrackerDmt() {
            super(DeploymentAdminImpl.this.context, 
                    DmtAdmin.class.getName(), null);
        }
    }

    // persisted fields
    private Set       dps = new HashSet();		// deployment packages
    private Hashtable mappingRpDp = new Hashtable();
    
	public void start(BundleContext context) throws Exception {
		this.context = context;
		trackEvent = new TrackerEvent();
		trackEvent.open();
		trackDmt = new TrackerDmt();
		trackDmt.open();
		load();
        registration = context.registerService(DeploymentAdmin.class.getName(), this, null);
        logger = new Logger(context);
        
        initKeyStore();
        fwBundleDir = System.getProperty(DAConstants.FW_BUNDLES_DIR);
        if (null == fwBundleDir)
            logger.log(Logger.LOG_WARNING, "The \"" + DAConstants.FW_BUNDLES_DIR + "\" system " +
            		"property is missing.");
        
        registerDmtPlugin();
	}
	
    private void registerDmtPlugin() {
        Hashtable props;
        Plugin plugin = new Plugin(this);
        
        props = new Hashtable();
        props.put(DmtDataPlugin.DATA_ROOT_URIS, "./OSGi/Deployment");
        regDmtPlugin = context.registerService(DmtDataPlugin.class.getName(), plugin, props);

        props = new Hashtable();
        props.put(DmtExecPlugin.EXEC_ROOT_URIS, "./OSGi/Deployment");
        regDmtPlugin = context.registerService(DmtDataPlugin.class.getName(), plugin, props);
        regDmtPlugin = context.registerService(DmtExecPlugin.class.getName(), plugin, props);
    }

    private void initKeyStore() throws Exception {
        String ksType = System.getProperty(DAConstants.KEYSTORE_TYPE);
        if (null == ksType)
            ksType = System.getProperty("org.osgi.service.deploymentadmin.keystore.type");
        if (null == ksType)
            ksType = "JKS";
        String ks = System.getProperty(DAConstants.KEYSTORE_PATH);
        if (null == ks)
            ks = System.getProperty("org.osgi.service.deploymentadmin.keystore.path");
        if (null == ks) {
            logger.log(Logger.LOG_WARNING, "Keystore location is not defined. Set the " + 
                    DAConstants.KEYSTORE_PATH + " or the " + 
                    "org.osgi.service.deploymentadmin.keystore.path" + " system property!");
            return;
        }
        File file = new File(ks);
        if (!file.exists())
            throw new RuntimeException("Keystore is not found: " + file);
        String pwd = System.getProperty(DAConstants.KEYSTORE_PWD);
        if (null == pwd)
            pwd = System.getProperty("org.osgi.service.deploymentadmin.keystore.pwd");
        if (null == pwd)
            throw new RuntimeException("There is no keystore password set. Set the " +
                    DAConstants.KEYSTORE_PWD + " or the " + 
                    "org.osgi.service.deploymentadmin.keystore.pwd" + " system property!");
        keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(file), pwd.toCharArray());
    }

    public void stop(BundleContext context) throws Exception {
	    registration.unregister();
	    logger.stop();
	    trackEvent.close();
	    trackDmt.close();
	    
	    regDmtPlugin.unregister();
	}

    public DeploymentPackage installDeploymentPackage(InputStream in)
    		throws DeploymentException
    {
        DeploymentPackageJarInputStream wjis;
        DeploymentPackageImpl srcDp = null;
        cancelled = false;
        
        // create the source DP
        try {
            wjis = new DeploymentPackageJarInputStream(in);
            if (!checkCertificateChains(wjis.getCertificateChains()))
                throw new DeploymentException(DeploymentException.CODE_SIGNING_ERROR, 
                        "No certificate was found in the keystore for the deployment package");
            srcDp = new DeploymentPackageImpl(wjis.getManifest(), this, 
                    wjis.getCertificateChainStringArrays());
        }
        catch (IOException e) {
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                  e.getMessage(), e);
        } catch (KeyStoreException e) {
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                  e.getMessage(), e);
        }
        
        checkPermission(srcDp, DeploymentAdminPermission.ACTION_INSTALL);
        
        sendInstallEvent(srcDp.getName());
        session = createInstallUpdateSession(srcDp);
        if (!checkSessionSilent())
            return null;
        checkSession();
        try {
            session.installUpdate(wjis);
        } catch (CancelException e) {
            sendCompleteEvent(false);
            return null;
        }
        sendCompleteEvent(true);
        
        DeploymentPackage ret;
        if (session.getDeploymentAction() == DeploymentSessionImpl.INSTALL) {
            //dps.add(srcDp);
            addDp(srcDp);
            ret = srcDp;
        }
        else { // if (session.getDeploymentAction() == DeploymentSession.UPDATE) 
            removeDp((DeploymentPackageImpl) session.getTargetDeploymentPackage());
            addDp((DeploymentPackageImpl) session.getSourceDeploymentPackage());
            ret = session.getSourceDeploymentPackage();
        }
        return ret;
    }
    
    private void addDp(DeploymentPackageImpl dp) {
        dps.add(dp);
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry	be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                mappingRpDp.put(be.getPid(), dp.getName());
        }
    }

    private void removeDp(DeploymentPackageImpl dp) {
        dps.remove(dp);
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry	be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                mappingRpDp.remove(be.getPid());
        }
    }
    
    String getMappedDp(String pid) {
        return (String) mappingRpDp.get(pid);
    }

    private void checkSession() throws DeploymentException {
        DeploymentPackageImpl srcDp = (DeploymentPackageImpl) session.getSourceDeploymentPackage();
        DeploymentPackageImpl tarDp = (DeploymentPackageImpl) session.getTargetDeploymentPackage();
        
        for (Iterator iter = srcDp.getResourceEntryIterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.isMissing() && !tarDp.contains(re))
                throw new DeploymentException(DeploymentException.CODE_MISSING_RESOURCE, 
                        "Resource '" + re + "' in the target Deployment Package is missing");            
        }
        
        for (Iterator iter = srcDp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isMissing() && !tarDp.contains(be))
                throw new DeploymentException(DeploymentException.CODE_MISSING_BUNDLE, 
                        "Bundle '" + be + "' in the target Deployment Package is missing");            
        }
    }

    private boolean checkSessionSilent() throws DeploymentException {
        DeploymentPackage sDp = session.getSourceDeploymentPackage();
        DeploymentPackage tDp = session.getTargetDeploymentPackage();
        if (sDp.getName().equals(tDp.getName()) &&
                sDp.getVersion().equals(tDp.getVersion()))
            return false;

        return true;
    }

    private boolean checkCertificateChains(List certChains) throws KeyStoreException {
        if (null == certChains || certChains.isEmpty())
            return true;
        for (Iterator iter = certChains.iterator(); iter.hasNext();) {
            Certificate[] certChain = (Certificate[]) iter.next();
            if (certChain.length > 0 &&
                // checks only the root certificates
                checkCertificate(certChain[certChain.length - 1]))
                	return true;
        }
        return false;
    }
    
    private boolean checkCertificate(Certificate cert) throws KeyStoreException {
        String alias = keystore.getCertificateAlias(cert);
        if (null != alias) {
            Certificate kCert = keystore.getCertificate(alias);
            if (null == kCert)
                return false;
            try {
                cert.verify(kCert.getPublicKey());
                return true;
            } catch (Exception e) {
                // do nothing
            }
        }
        return false;
    }

    private DeploymentAdminPermission createPermission(String dpName, 
            String[] certChain, String action) 
    {
        String target;
        if (null == certChain || 0 == certChain.length)
            target = "(name=" + dpName + ")";
        else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < certChain.length; i++)
                sb.append(certChain[i] + ";");
            sb.deleteCharAt(sb.length() - 1);
            target = "(&(name=" + dpName + ")(signer=" + sb + "))";
        }
        DeploymentAdminPermission perm = new DeploymentAdminPermission(target, action);
        return perm;
    }

    private void sendInstallEvent(String dpName) {
        final EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        final Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, dpName);
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ea.postEvent(new Event(DAConstants.TOPIC_INSTALL, ht));
                return null;
            }
            });
    }

    private void sendUninstallEvent(String dpName) {
        final EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        final Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, dpName);
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ea.postEvent(new Event(DAConstants.TOPIC_UNINSTALL, ht));
                return null;
            }});
    }

    private void sendCompleteEvent(boolean succ) {
        final EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        final Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, session.getSourceDeploymentPackage().getName());
        ht.put(DAConstants.EVENTPROP_SUCCESSFUL, new Boolean(succ));
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ea.postEvent(new Event(DAConstants.TOPIC_COMPLETE, ht));
                return null;
            }});
    }

    private DeploymentSessionImpl createInstallUpdateSession(DeploymentPackageImpl srcDp) 
    		throws DeploymentException 
    {
        // find the package among installed packages
        DeploymentPackageImpl targetDp = findDp(srcDp);
        
        // fix-pack has no target
        if (srcDp.fixPack() && targetDp == null)
            throw new DeploymentException(DeploymentException.CODE_MISSING_FIXPACK_TARGET,
                    "Target of the fix-pack is missing");
        
        // not found -> install
        if (null == targetDp) {
            // creates an empty dp
            targetDp = new DeploymentPackageImpl();
	        return new DeploymentSessionImpl(new DeploymentPackageImpl(srcDp), 
	                targetDp, logger, context, fwBundleDir, this);
        }
        // found -> update
        else {
            DeploymentSessionImpl ret = new DeploymentSessionImpl(
                    new DeploymentPackageImpl(srcDp), new DeploymentPackageImpl(targetDp), 
                    logger, context, fwBundleDir, this);
            if (srcDp.fixPack()) {
                VersionRange range = srcDp.getFixPackRange();
                Version ver = targetDp.getVersion();
                if (!range.isIncluded(ver))
                    throw new DeploymentException(DeploymentException.CODE_MISSING_FIXPACK_TARGET,
                    		"Fix pack version range (" + srcDp.getFixPackRange() + ") doesn't fit " +
                    		"to the version (" + targetDp.getVersion() + ") of the target " + 
                    		"deployment package"); 
            }
            return ret;
        }
    }
    
    private DeploymentSessionImpl createUninstallSession(DeploymentPackageImpl targetDp) 
			throws DeploymentException 
	{
        // creates an empty dp
        DeploymentPackageImpl srcDp = new DeploymentPackageImpl();

        // find the package among installed packages
        DeploymentPackageImpl dp = findDp(targetDp);
        if (null == dp)
            throw new RuntimeException("Internal error");
        
        return new DeploymentSessionImpl(srcDp, new DeploymentPackageImpl(targetDp), logger,
                context, fwBundleDir, this);
	}

    private DeploymentPackageImpl findDp(DeploymentPackageImpl srcDp) {
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            if (srcDp.equalsIgnoreVersion(dp))
                return dp;
        }
        return null;
    }

    public synchronized DeploymentPackage getDeploymentPackage(String symbName) {
        if (null == symbName)
            throw new IllegalArgumentException("Deployment package symbolic name " +
            		"cannot be null");
        
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            if (dp.getName().equals(symbName)) {
                checkPermission(dp, DeploymentAdminPermission.ACTION_LIST);
                return dp;
            }
        }
        return null;
	}
	
	public boolean cancel() {
	    if (null == session)
	        return false;
	    else  {
	        if (DeploymentSessionImpl.UNINSTALL == session.getDeploymentAction())
	            checkPermission((DeploymentPackageImpl) session.getTargetDeploymentPackage(), 
	                    DeploymentAdminPermission.ACTION_CANCEL);
	        else
	            checkPermission((DeploymentPackageImpl) session.getSourceDeploymentPackage(), 
	                    DeploymentAdminPermission.ACTION_CANCEL);
	        session.cancel();
	        cancelled = true;
	        return true;
	    }
	}

	public DeploymentPackage[] listDeploymentPackages() {
	    Vector ret = new Vector();
	    DeploymentPackageImpl[] src = (DeploymentPackageImpl[]) dps.toArray(
	            new DeploymentPackageImpl[] {});
	    for (int i = 0; i < src.length; i++) {
	        try {
	            checkPermission(src[i], DeploymentAdminPermission.ACTION_LIST);
	            ret.add(src[i]);
	        } catch (SecurityException e) {
	            // do nothing
	        }
        }
	    
	    DeploymentPackageImpl sysDp = createSystemDp();
	    try {
	        checkPermission(sysDp, DeploymentAdminPermission.ACTION_LIST);
	        ret.add(sysDp);
	    } catch (SecurityException e) {
            // do nothing
        }
	    
		return (DeploymentPackage[]) ret.toArray(new DeploymentPackage[] {});
	}

    private DeploymentPackageImpl createSystemDp() {
        Set all = new HashSet();
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++)
            all.add(new BundleEntry(bundles[i]));

        Set sub = new HashSet();
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            Set s = dp.getBundleEntriesAsSet();
            sub.addAll(s);
        }
        
        all.removeAll(sub);
        return DeploymentPackageImpl.createSystemBundle(all);
    }

    /*
     * Saves persistent data.
     */
    private synchronized void save() throws IOException {
        final File f = context.getDataFile(this.getClass().getName() + ".obj");
        if (null == f) {
            logger.log(Logger.LOG_WARNING, "Platform does not have file system support. " + 
                    "Deployment packages cannot be persisted.");
            return;
        }
        
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    FileOutputStream fos = new FileOutputStream(f);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(dps);
                    oos.writeObject(mappingRpDp);
                    oos.close();
                    fos.close();
                    return null;
                }});
        }
        catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    /*
     * Reads persistent data
     */
    private synchronized void load() {
        File f = context.getDataFile(this.getClass().getName() + ".obj");
        if (!f.exists())  
            return;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            DPObjectInputStream ois = new DPObjectInputStream(fis);
            dps = (Set) ois.readObject();
            mappingRpDp = (Hashtable) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            logger.log(Logger.LOG_WARNING, "File: " + f.getName() + " does not exist." + 
                    "Cannot load deployment packages.");
        }
        catch (Exception e) {
            logger.log(Logger.LOG_ERROR, "Error occured during loading " +
            		"deployment packages.");
            logger.log(e);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                }
                catch (IOException ee) {
                    logger.log(ee);
                }
            }
        }
    }

	void checkPermission(DeploymentPackageImpl dp, String action) {
	    SecurityManager sm = System.getSecurityManager();
	    if (null == sm)
	        return;
	    
	    if (dp.getCertChains().isEmpty()) {
	        DeploymentAdminPermission perm = 
            	createPermission(dp.getName(), null, action);
	        sm.checkPermission(perm);
	        return;
	    }
	    
	    SecurityException secExc = null;
	    for (Iterator iter = dp.getCertChains().iterator(); iter.hasNext();) {
            String[] certChain = (String[]) iter.next();
            DeploymentAdminPermission perm = 
                	createPermission(dp.getName(), certChain, action);
            try {
                sm.checkPermission(perm);
                return;
            } catch (SecurityException e) {
                secExc = e;
                continue;
            }
        }
	    throw secExc;
	}

    /*
     * Encapsulates the deserialization task of the DeploymentPackageImpl
     * objects.
     */
    private class DPObjectInputStream extends ObjectInputStream {
        public DPObjectInputStream(FileInputStream in) throws IOException {
            super(in);
            enableResolveObject(true);
        }
	    
        protected Object resolveObject(Object obj) throws IOException {
            if (obj instanceof DeploymentPackageImpl) {
                DeploymentPackageImpl dp = (DeploymentPackageImpl) obj;
                dp.setDeploymentAdmin(DeploymentAdminImpl.this);
            }
            
            return obj;
        }
	}

    void uninstall(DeploymentPackageImpl targetDp) throws DeploymentException {
        // TODO checkPermission 

        sendUninstallEvent(targetDp.getName());
        
        session = createUninstallSession(targetDp);
        try {
            session.uninstall();
            sendCompleteEvent(true);
        } catch (CancelException e) {
            sendCompleteEvent(false);
            return;
        } finally {
            session = null;
        }

        removeDp(targetDp);
    }
    
    boolean uninstallForced(DeploymentPackageImpl dp) {
        sendUninstallEvent(dp.getName());
        // TODO        
        sendCompleteEvent(true);
        //dps.remove(dp);
        removeDp(dp);
        return false;
    }
    
    BundleContext getBundleContext() {
        return context;
    }

    static String location(String symbName, Version version) {
        return "osgi-dp:" + symbName;
    }

    Set getDeploymentPackages() {
        return dps;
    }
    
    DmtAdmin getDmtAdmin() {
        return (DmtAdmin) trackDmt.getService();
    }

    BundleContext getContext() {
        return context;
    }
    
}
