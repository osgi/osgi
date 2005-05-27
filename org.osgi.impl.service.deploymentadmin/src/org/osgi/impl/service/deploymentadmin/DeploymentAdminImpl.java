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
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private String                fwBundleDir;
    
    /*
     * Class to track the event admin
     */
    private class TrackerEvent extends ServiceTracker {
        public TrackerEvent() {
            super(DeploymentAdminImpl.this.context, 
                    EventAdmin.class.getName(), null);
        }
    }
    
    // persisted fields
    private Set     dps = new HashSet();		// deployment packages
    private Integer nextDpId = new Integer(1);  // id of the next DP ("system" dp == 0)
    private boolean cancelled;
    
	public void start(BundleContext context) throws Exception {
		this.context = context;
		trackEvent = new TrackerEvent();
		trackEvent.open();
		load();
        registration = context.registerService(DeploymentAdmin.class.getName(), this, null);
        logger = new Logger(context);
        
        initKeyStore();
        fwBundleDir = System.getProperty(DAConstants.FW_BUNDLES_DIR);
        if (null == fwBundleDir)
            logger.log(Logger.LOG_WARNING, "The \"" + DAConstants.FW_BUNDLES_DIR + "\" system " +
            		"property is missing.");
	}
	
    private void initKeyStore() throws Exception {
        String ksType = System.getProperty(DAConstants.KEYSTORE_TYPE);
        if (null == ksType)
            ksType = System.getProperty(DeploymentAdminPermission.KEYSTORE_TYPE);
        if (null == ksType)
            ksType = "JKS";
        String ks = System.getProperty(DAConstants.KEYSTORE_PATH);
        if (null == ks)
            ks = System.getProperty(DeploymentAdminPermission.KEYSTORE_PATH);
        if (null == ks) {
            logger.log(Logger.LOG_WARNING, "Keystore location is not defined. Set the " + 
                    DAConstants.KEYSTORE_PATH + " or the " + 
                    DeploymentAdminPermission.KEYSTORE_PATH + " system property!");
            return;
        }
        File file = new File(ks);
        if (!file.exists())
            throw new RuntimeException("Keystore is not found: " + file);
        String pwd = System.getProperty(DAConstants.KEYSTORE_PWD);
        if (null == pwd)
            pwd = System.getProperty(DeploymentAdminPermission.KEYSTORE_PWD);
        if (null == pwd)
            throw new RuntimeException("There is no keystore password set. Set the " +
                    DAConstants.KEYSTORE_PWD + " or the " + 
                    DeploymentAdminPermission.KEYSTORE_PWD + " system property!");
        keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(file), pwd.toCharArray());
    }

    public void stop(BundleContext context) throws Exception {
	    registration.unregister();
	    logger.stop();
	    trackEvent.close();
	}

    private synchronized int nextDpId() {
        int ret = nextDpId.intValue(); 
        nextDpId = new Integer(ret + 1);
        try {
            save();
        } catch (IOException e) {
            logger.log(Logger.LOG_ERROR, "Error occured during persisting " +
            		"deployment packages.");
            logger.log(e);
            throw new RuntimeException("Internal error.");
        }
        return ret;
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
            if (!checkCertificateChains(wjis.getCertChains()))
                throw new DeploymentException(DeploymentException.CODE_SIGNING_ERROR, 
                        "No certificate was found in the keystore for the deployment package");
            srcDp = new DeploymentPackageImpl(wjis.getManifest(), 
                    nextDpId(), this, wjis.getCertStringChains());
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
        try {
            session.installUpdate(wjis);
        } catch (CancelException e) {
            sendCompleteEvent(false);
            return null;
        }
        sendCompleteEvent(true);
        
        if (session.getDeploymentAction() == DeploymentSessionImpl.INSTALL) {
            dps.add(srcDp);
            return srcDp;
        }
        else { // if (session.getDeploymentAction() == DeploymentSession.UPDATE) 
            dps.remove(session.getTargetDeploymentPackage());
            dps.add(session.getSourceDeploymentPackage());
            return session.getSourceDeploymentPackage();
        }
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
        EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, dpName);
        ea.postEvent(new Event(DAConstants.TOPIC_INSTALL, ht));
    }

    private void sendUninstallEvent(String dpName) {
        EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, dpName);
        ea.postEvent(new Event(DAConstants.TOPIC_UNINSTALL, ht));
    }

    private void sendCompleteEvent(boolean succ) {
        EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, session.getSourceDeploymentPackage().getName());
        ht.put(DAConstants.EVENTPROP_SUCCESSFUL, new Boolean(succ));
        ea.postEvent(new Event(DAConstants.TOPIC_COMPLETE, ht));
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
	                targetDp, logger, context, fwBundleDir);
        }
        // found -> update
        else {
            DeploymentSessionImpl ret = new DeploymentSessionImpl(
                    new DeploymentPackageImpl(srcDp), new DeploymentPackageImpl(targetDp), 
                    logger, context, fwBundleDir);
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
                context, fwBundleDir);
	}

    private DeploymentPackageImpl findDp(DeploymentPackageImpl srcDp) {
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            if (srcDp.equalsIgnoreVersion(dp))
                return dp;
        }
        return null;
    }

    // TODO throws SecurityException
    public synchronized DeploymentPackage getDeploymentPackage(long id) {
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            if (dp.getId() == id)
                return dp;
        }
        return null;
	}
	
	public boolean cancel() {
	    if (null == session)
	        return false;
	    else  {
	        session.cancel();
	        cancelled = true;
	        return true;
	    }
	}

	public DeploymentPackage[] listDeploymentPackages() {
	    // TODO checkPermission("", DeploymentAdminPermission.ACTION_LIST_DPS);
	    DeploymentPackage[] src = (DeploymentPackage[]) dps.toArray(new DeploymentPackage[] {});
	    DeploymentPackage[] tar = new DeploymentPackage[dps.size() + 1]; 
	    System.arraycopy(src, 0, tar, 0, src.length);
	    tar[tar.length - 1] = createSystemDp();
		return tar;
	}

    private DeploymentPackage createSystemDp() {
        Set all = new HashSet();
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++)
            all.add(new BundleEntry(bundles[i]));

        Set sub = new HashSet();
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            Set s = new HashSet(dp.getBundleEntries());
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
                    oos.writeObject(nextDpId);
                    oos.writeObject(dps);
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
            nextDpId = (Integer) ois.readObject();
            dps = (Set) ois.readObject();
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

    // TODO is it correct?
	private void checkPermission(DeploymentPackageImpl dp, String action) {
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
        } catch (CancelException e) {
            sendCompleteEvent(false);
            return;
        }
        
        sendCompleteEvent(true);
        
        dps.remove(targetDp);
    }
    
    boolean uninstallForced(DeploymentPackageImpl dp) {
        sendUninstallEvent(dp.getName());
        // TODO        
        sendCompleteEvent(true);
        dps.remove(dp);
        return false;
    }
    
    BundleContext getBundleContext() {
        return context;
    }

    static String location(String symbName, String version) {
        return symbName;
    }
    
}
