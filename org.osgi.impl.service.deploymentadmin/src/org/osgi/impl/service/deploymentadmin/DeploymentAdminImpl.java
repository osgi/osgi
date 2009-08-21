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

import info.dmtree.notification.NotificationService;
import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.KeyStoreException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.impl.service.deploymentadmin.plugin.PluginDelivered;
import org.osgi.impl.service.deploymentadmin.plugin.PluginDeployed;
import org.osgi.impl.service.deploymentadmin.plugin.PluginDownload;
import org.osgi.impl.service.dwnl.DownloadAgent;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Implementation of the DeploymentAdmin interface. This is the 
 * entry point to the Deployment Admin Service.
 */
public class DeploymentAdminImpl implements DeploymentAdmin, BundleActivator {
    
	private BundleContext 		  context;
    
    // contains the ServiceRegistration objects for the registered 
    // DMT plugins to ease handling of them
	private HashSet               serviceRegs = new HashSet();
    
    private Logger 				  logger;
    private DeploymentSessionImpl session;
    private DAKeyStore            keystore;
    private TrackerEvent          trackEvent;         // tracks the EventAdmin
    private TrackerDownloadAgent  trackDownloadAgent; // tracks the DownloadAgent
    private TrackerNfs            trackNfs;           // tracks the NotificationService
    
    // max wait time before Deployment Admin throws exception with code 
    // DeploymentException.CODE_TIMEOUT
    private long                  sessionTimeout;
    
    private boolean				  busy;   // indicates that the Deployment Admin 
                                          // is busy

    // DMT plugins
    private PluginDownload  pluginDownload;
    private PluginDeployed  pluginDeployed;
    private PluginDelivered pluginDelivered;
    
    // persisted fields
    
    private Vector dps = new Vector(); // deployment packages
    
    // ease to find foreign customizers (when a resource processor service 
    // is a customizer from another deployment package) 
    private Hashtable mappingRpDp = new Hashtable();
    
    // Private class definitions
    ///////////////////////////////////////////////////////////////////////
    
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
     * Class to track the Download Agent
     */
    private class TrackerDownloadAgent extends ServiceTracker {
        public TrackerDownloadAgent() {
            super(DeploymentAdminImpl.this.context, 
                    DownloadAgent.class.getName(), null);
        }
    }
    
    /*
     * Class to track the NotificationService
     */
    private class TrackerNfs extends ServiceTracker {
    	public TrackerNfs() {
    		super(DeploymentAdminImpl.this.context, 
    				NotificationService.class.getName(), null);
    	}
    }
    
    // BundleActivator interface implementation
    ///////////////////////////////////////////////////////////////////////
    
	public void start(BundleContext context) throws Exception {
		this.context = context;

        // starts service trackers
		initTrackers();
		
        // registers Deployment Admin service
        registerService(new String[] { DeploymentAdmin.class.getName() }, this, null);
        
        // initialize logger
        logger = Logger.getLogger(context);

        // load persisten data (e.g. Deployment Package meta information)
        load();
        
        // initialise DMT plugins
        initDmtPlugins();

        // initialize keystore
        keystore = DAKeyStore.getKeyStore(context);
        
        // fetch session timeout system property
        String s = System.getProperty(DAConstants.SESSION_TIMEOUT);
        if (null == s)
            sessionTimeout = 1000;
        else
            sessionTimeout = Long.parseLong(s);
        
        // registers DMT plugins
        registerDmtPlugins();
	}
    
    public void stop(BundleContext context) throws Exception {
        unregisterServices();
        logger.stop();
        trackEvent.close();
        trackNfs.close();
        trackDownloadAgent.close();
    }
    
    // DeploymentAdmin interface implementation
    ///////////////////////////////////////////////////////////////////////

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentAdmin#installDeploymentPackage(java.io.InputStream)
     */
    public DeploymentPackage installDeploymentPackage(InputStream in)
            throws DeploymentException
    {
    	if (null == in)
    		throw new IllegalArgumentException("InputStream parameter cannot be null");
        waitIfBusy();
        DeploymentPackageJarInputStream wjis = null;
        DeploymentPackageImpl srcDp = null;
        boolean result = false;
        Event startEvent = null;
        try {
            // create the source DP
            wjis = new DeploymentPackageJarInputStream(in);
            srcDp = new DeploymentPackageImpl(DeploymentPackageCtx.
                    getInstance(logger, context, this), wjis.getManifest(), 
                    wjis.getCertificateChainStringArrays());
            new DeploymentPackageVerifier(srcDp).verify();
            srcDp.setResourceBundle(wjis.getResourceBundle());
            if (!checkCertificateChains(wjis.getCertificateChains()))
            	throw new DeploymentException(DeploymentException.CODE_SIGNING_ERROR, 
            	"No certificate was found in the keystore for the deployment package");
        
            session = createInstallUpdateSession(srcDp);

            // does the caller have the install permission?
            checkPermission(srcDp, DeploymentAdminPermission.INSTALL);
            startEvent = sendInstallEvent(srcDp);
        
            // checks the session
            if (equalSymbNameAndVersion())
                return session.getTargetDeploymentPackage();
            checkMissingEntities();

            // the real work take place here
            session.installUpdate(wjis);
            
            if (!session.isCancelled()) {
	            // update Deployment Package metainfo
	            updateDps();
	            // persist
	            save();
	            
	            result = true;
            } else
            	throw new DeploymentException(DeploymentException.CODE_CANCELLED);
        } catch (DeploymentException e) {
            throw e;
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            // other exceptions are wrapped into DeploymentException 
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                e.getMessage(), e);
        } finally {
            sendCompleteEvent(srcDp, startEvent, result);
            clearSession();
        }
        return srcDp;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentAdmin#getDeploymentPackage(java.lang.String)
     */
    public DeploymentPackage getDeploymentPackage(String symbName) {
        if (null == symbName)
            throw new IllegalArgumentException("Deployment package symbolic name " +
                    "cannot be null");
        
        DeploymentPackage dp = null;
        
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackage tdp = (DeploymentPackageImpl) iter.next();
            if (tdp.getName().equals(symbName)) {
                dp = tdp;
                break;
            }
        }
        
        if (null != dp)
            checkPermission((DeploymentPackageImpl) dp, DeploymentAdminPermission.LIST);
        
        return dp;
    }

    /**
     * @see DeploymentAdmin#getDeploymentPackage(Bundle)
     */
	public DeploymentPackage getDeploymentPackage(final Bundle bundle) {
        if (null == bundle)
            throw new IllegalArgumentException("The 'bundle' parameter cannot be null");
        
        DeploymentPackage dp = null;

        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            final DeploymentPackageImpl tdp = (DeploymentPackageImpl) iter.next();
            BundleEntry be = (BundleEntry) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return tdp.getBundleEntryByBundleId(bundle.getBundleId());
				}
            });
            if (null != be) {
            	dp = tdp;
            	break;
            }
        }
        
        if (null != dp)
            checkPermission((DeploymentPackageImpl) dp, DeploymentAdminPermission.LIST);
        
        return dp;
	}
    
    /**
     * @see org.osgi.service.deploymentadmin.DeploymentAdmin#cancel()
     */
    public boolean cancel() {
        if (null == session)
            return false;

        if (DeploymentSessionImpl.UNINSTALL == session.getDeploymentAction())
            checkPermission((DeploymentPackageImpl) session.getTargetDeploymentPackage(), 
                    DeploymentAdminPermission.CANCEL);
        else
            checkPermission((DeploymentPackageImpl) session.getSourceDeploymentPackage(), 
                    DeploymentAdminPermission.CANCEL);
        
        session.cancel();
        
        return true;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentAdmin#listDeploymentPackages()
     */
    public DeploymentPackage[] listDeploymentPackages() {
        Vector ret = new Vector();
        DeploymentPackageImpl[] src = (DeploymentPackageImpl[]) dps.toArray(
                new DeploymentPackageImpl[] {});
        for (int i = 0; i < src.length; i++) {
            try {
                checkPermission(src[i], DeploymentAdminPermission.LIST);
                ret.add(src[i]);
            } catch (SecurityException e) {
                // do nothing
            }
        }
        
        return (DeploymentPackage[]) ret.toArray(new DeploymentPackage[] {});
    }

    // Private methods
    ///////////////////////////////////////////////////////////////////////
	
    private void initDmtPlugins() {
        if (null == pluginDownload)
            pluginDownload  = new PluginDownload(PluginCtx.getInstance(logger, context, this));
        if (null == pluginDeployed)
            pluginDeployed  = new PluginDeployed(PluginCtx.getInstance(logger, context, this));
        if (null == pluginDelivered)
            pluginDelivered = new PluginDelivered(PluginCtx.getInstance(logger, context, this));
    }

    private void initTrackers() {
        trackEvent = new TrackerEvent();
        trackEvent.open();
        trackNfs = new TrackerNfs();
        trackNfs.open();
        trackDownloadAgent = new TrackerDownloadAgent();
        trackDownloadAgent.open();
    }

    private void registerDmtPlugins() {
        Hashtable props;
        
        String[] pluginClassNames = new String[] { 
                DataPlugin.class.getName(),
                ExecPlugin.class.getName() 
        };  
                
        props = new Hashtable();
        props.put("dataRootURIs", "./OSGi/Deployment/Download");
        props.put("execRootURIs", "./OSGi/Deployment/Download");
        registerService(pluginClassNames, pluginDownload, props);

        props = new Hashtable();
        props.put("dataRootURIs", "./OSGi/Deployment/Inventory/Deployed");
        props.put("execRootURIs", "./OSGi/Deployment/Inventory/Deployed");
        registerService(pluginClassNames, pluginDeployed, props);

        props = new Hashtable();
        props.put("dataRootURIs", "./OSGi/Deployment/Inventory/Delivered");
        props.put("execRootURIs", "./OSGi/Deployment/Inventory/Delivered");
        registerService(pluginClassNames, pluginDelivered, props);
    }
    
    private void registerService(String[] classNames, Object service, Hashtable props) {
        serviceRegs.add(context.registerService(classNames, service, props));
    }
    
    private void unregisterServices() {
        for (Iterator iter = serviceRegs.iterator(); iter.hasNext();) {
            ServiceRegistration reg = (ServiceRegistration) iter.next();
            reg.unregister();
        }
    }

    /*
     * Waits for a preset amount of time. If the deployment operation is 
     * not able to start till that time throws a DeploymentException 
     * with code DeploymentException.CODE_TIMEOUT
     */
    private synchronized void waitIfBusy() throws DeploymentException {
        if (busy) {
            try {
                wait(sessionTimeout);
            } catch (InterruptedException e) {
                logger.log(e);
            }
            if (busy) {
            	String dpName;
            	if (session.getDeploymentAction() == DeploymentSessionImpl.UNINSTALL)
            		dpName = session.getTargetDeploymentPackage().toString();
            	else
            		dpName = session.getSourceDeploymentPackage().toString();
	            throw new DeploymentException(DeploymentException.CODE_TIMEOUT,
	                "Timeout period (" + sessionTimeout + " ms) expired. " + 
	                dpName + " is being processed.");
            }
        }
        busy = true;
    }
    
    /*
     * Releases the Deployment Admin service. It is ready to carry out 
     * the subsequent request.  
     */
    private synchronized void clearSession() {
        session = null;
        busy = false;
        notify();
    }
    
    /*
     * Updates Deployment Package metainfo
     */
    private void updateDps() {
        if (session.getDeploymentAction() == DeploymentSessionImpl.INSTALL) {
        	DeploymentPackageImpl srcDp = 
        		(DeploymentPackageImpl) session.getSourceDeploymentPackage();
        	addDp(srcDp);
        } else { // if (session.getDeploymentAction() == DeploymentSessionImpl.UPDATE)
        	DeploymentPackageImpl targetDp = 
        		(DeploymentPackageImpl) session.getTargetDeploymentPackage();
        	DeploymentPackageImpl srcDp = 
        		(DeploymentPackageImpl) session.getSourceDeploymentPackage();
        	targetDp.update(srcDp);
        	updateRpDpMapping(targetDp);
        }    
    }
    
    private void addDp(DeploymentPackageImpl dp) {
    	dps.add(dp);
    	updateRpDpMapping(dp);
	}

    private void removeDp(DeploymentPackageImpl dp) {
    	dps.remove(dp);
    	dp.clearCache(dp);
    	updateRpDpMapping(dp);
    }

    /*
     * Ease to find foreign customizers (when a resource processor service 
     * is a customizer from another deployment package)  
     */
    private void updateRpDpMapping(DeploymentPackageImpl dp) {
        for (Iterator iter = mappingRpDp.keySet().iterator(); iter.hasNext();) {
            String pid = (String) iter.next();
            String dpName = (String) mappingRpDp.get(pid);
            if (dpName.equals(dp.getName())) {
                BundleEntry be = dp.getBundleEntryByPid(pid);
                if (null == be)
                    iter.remove();
            }
        }
        
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                mappingRpDp.put(be.getPid(), dp.getName());
        }
    }

    /*
     * Thorws exception if an entry is said to be missing in the manifest but 
     * such an entity doesn't exist in the target Deployment Package.
     */
    private void checkMissingEntities() throws DeploymentException {
        DeploymentPackageImpl srcDp = (DeploymentPackageImpl) session.getSourceDeploymentPackage();
        DeploymentPackageImpl tarDp = (DeploymentPackageImpl) session.getTargetDeploymentPackage();
        
        for (Iterator iter = srcDp.getResourceEntries().iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.isMissing() && !tarDp.contains(re))
                throw new DeploymentException(DeploymentException.CODE_MISSING_RESOURCE, 
                        "Resource '" + re + "' in the target Deployment Package is missing");            
        }
        
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isMissing() && !tarDp.contains(be))
                throw new DeploymentException(DeploymentException.CODE_MISSING_BUNDLE, 
                        "Bundle '" + be + "' in the target Deployment Package is missing");            
        }
    }

    /*
     * Indicates the the source and the target Deployment Package have the 
     * same name and version. In this case the Deployment Admin mustn't 
     * start the deployment action. 
     */
    private boolean equalSymbNameAndVersion() {
        DeploymentPackage sDp = session.getSourceDeploymentPackage();
        DeploymentPackage tDp = session.getTargetDeploymentPackage();
        if (sDp.getName().equals(tDp.getName()) &&
            sDp.getVersion().equals(tDp.getVersion()))
            	return true;
        return false;
    }

    /*
     * Checks that the Deployment Package signers are in the keystore.
     */
    private boolean checkCertificateChains(List certChains) throws KeyStoreException {
        // if the Deployment Package is not signed there is nothing to do
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
    
    /*
     * Check whether the Certificate is in the keystore
     */
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
                // do nothing false will be returned
            }
        }
        return false;
    }

    /*
     * Convenience method to make DeploymentAdminPermissions
     */
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

    /*
     * Convenience method to send events
     */
    private Event sendInstallEvent(DeploymentPackageImpl dp) {
    	final Hashtable ht = new Hashtable();
    	ht.put(DAConstants.EVENTPROP_DPNAME, dp.getName());
    	String displayName = dp.getDisplayName();
    	if (displayName != null) {
    		ht.put(DAConstants.EVENTPROP_DP_READABLE_NAME, displayName);
    	}
    	if (session.getDeploymentAction() == DeploymentSessionImpl.UPDATE) {
    		ht.put(DAConstants.EVENTPROP_DP_CURRENT_VERSION, session.getTargetDeploymentPackage().getVersion());
    	}
    	final EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return null;
        return (Event) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
            	Event e = new Event(DAConstants.TOPIC_INSTALL, ht);
                ea.postEvent(e);
                return e;
            }
            });
    }

    /*
     * Convenience method to send events
     */
    private Event sendUninstallEvent(DeploymentPackageImpl dp) {
    	final Hashtable ht = new Hashtable();
    	ht.put(DAConstants.EVENTPROP_DPNAME, dp.getName());
    	String displayName = dp.getDisplayName();
    	if (displayName != null) {
    		ht.put(DAConstants.EVENTPROP_DP_READABLE_NAME, displayName);
    	}
		ht.put(DAConstants.EVENTPROP_DP_CURRENT_VERSION, dp.getVersion());

        final EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return null;
        return (Event) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
            	Event e = new Event(DAConstants.TOPIC_UNINSTALL, ht);
                ea.postEvent(e);
                return e;
            }});
    }

    /*
     * Convenience method to send events
     */
    private void sendCompleteEvent(DeploymentPackageImpl dp, Event startEvent, boolean succ) {
    	if (null == startEvent)
    		return;
        final Hashtable ht = new Hashtable();
        ht.put(DAConstants.EVENTPROP_DPNAME, startEvent.getProperty(DAConstants.EVENTPROP_DPNAME));
        if (startEvent.getProperty(DAConstants.EVENTPROP_DP_READABLE_NAME) != null) {
        	ht.put(DAConstants.EVENTPROP_DP_READABLE_NAME, startEvent.getProperty(DAConstants.EVENTPROP_DP_READABLE_NAME));
        }
        if (startEvent.getTopic().equals(DAConstants.TOPIC_INSTALL)) {
        	if (startEvent.getProperty(DAConstants.EVENTPROP_DP_CURRENT_VERSION) != null)
        	ht.put(DAConstants.EVENTPROP_DP_CURRENT_VERSION, startEvent.getProperty(DAConstants.EVENTPROP_DP_CURRENT_VERSION));
        	Version version = dp.getVersion();
        	if (version != null) {
        		ht.put(DAConstants.EVENTPROP_DP_NEXT_VERSION, version);
        	}
        }
        ht.put(DAConstants.EVENTPROP_SUCCESSFUL, new Boolean(succ));
        
        final EventAdmin ea = (EventAdmin) trackEvent.getService();
        if (null == ea)
            return;
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                ea.postEvent(new Event(DAConstants.TOPIC_COMPLETE, ht));
                return null;
            }});
    }

    /*
     * Creates an install/update session
     */
    private DeploymentSessionImpl createInstallUpdateSession(
            DeploymentPackageImpl srcDp) throws DeploymentException 
    {
        // find the package among installed packages
        DeploymentPackageImpl targetDp = findDp(srcDp);
        
        // fix-pack has no target
        if (null != srcDp.getFixPackRange() && targetDp == null)
            throw new DeploymentException(DeploymentException.CODE_MISSING_FIXPACK_TARGET,
                "Target of the fix-pack is missing");
        
        // not found -> install
        
        if (null == targetDp) {
            // creates an empty dp
            targetDp = DeploymentPackageImpl.createEmpty(
                    DeploymentPackageCtx.getInstance(logger, context, this));
	        return new DeploymentSessionImpl(srcDp, targetDp, 
                    DeploymentSessionCtx.getInstance(logger, context, mappingRpDp));
        }
        
        // found -> update
        
        DeploymentSessionImpl ret = new DeploymentSessionImpl(srcDp, targetDp, 
                DeploymentSessionCtx.getInstance(logger, context, mappingRpDp));
        if (null != srcDp.getFixPackRange()) {
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
    
    /*
     * Creates uninstall session
     */
    private DeploymentSessionImpl createUninstallSession(DeploymentPackageImpl targetDp) 
			throws DeploymentException 
	{
        // creates an empty dp
        DeploymentPackageImpl srcDp = DeploymentPackageImpl.createEmpty(
                DeploymentPackageCtx.getInstance(logger, context, this));

        // find the package among installed packages
        DeploymentPackageImpl dp = findDp(targetDp);
        if (null == dp)
            throw new RuntimeException("Internal error");
        
        return new DeploymentSessionImpl(srcDp, targetDp, 
                DeploymentSessionCtx.getInstance(logger, context, mappingRpDp));
	}

    /*
     * Finds a deployment package among installed packages or return null.
     * IMPORTANT: Deployment Package version is ignored
     */
    private DeploymentPackageImpl findDp(DeploymentPackageImpl srcDp) {
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            if (srcDp.equalsIgnoreVersion(dp))
                return dp;
        }
        return null;
    }

    /*
     * Saves persistent data.
     */
    public void save() throws IOException {
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
                    oos.writeObject(pluginDownload);
                    oos.writeObject(pluginDeployed);
                    oos.writeObject(pluginDelivered);
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
    private void load() {
        File f = context.getDataFile(this.getClass().getName() + ".obj");
        if (!f.exists())  
            return;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            DAObjectInputStream ois = new DAObjectInputStream(fis);
            dps = (Vector) ois.readObject();
            mappingRpDp = (Hashtable) ois.readObject();
            pluginDownload = (PluginDownload) ois.readObject();
            pluginDeployed = (PluginDeployed) ois.readObject();
            pluginDelivered = (PluginDelivered) ois.readObject();
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
	    
	    if (dp.isEmpty())
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
    private class DAObjectInputStream extends ObjectInputStream {
        public DAObjectInputStream(FileInputStream in) throws IOException {
            super(in);
            enableResolveObject(true);
        }
	    
        protected Object resolveObject(Object obj) throws IOException {
            if (obj instanceof DeploymentPackageImpl) {
                DeploymentPackageImpl dp = (DeploymentPackageImpl) obj;
                dp.setDeploymentPackageCtx(DeploymentPackageCtx.getInstance(
                        logger, context, DeploymentAdminImpl.this));
            } else if (obj instanceof PluginDownload) {
                PluginDownload p = (PluginDownload) obj;
                p.setPluginCtx(PluginCtx.getInstance(logger, context, 
                        DeploymentAdminImpl.this));
            } else if (obj instanceof PluginDeployed) {
                PluginDeployed p = (PluginDeployed) obj;
                p.setPluginCtx(PluginCtx.getInstance(logger, context, 
                        DeploymentAdminImpl.this));
            }  
            
            return obj;
        }
	}

    /*
     * DeploymentPackageImpl calls this method to notify the Deploymnet Admin 
     * about uninstallation of a Deployment Package (DeploymentPackageImpl.uninstall()
     * was called).
     */
    void uninstall(DeploymentPackageImpl dp) throws DeploymentException {
        waitIfBusy();
        checkPermission(dp, DeploymentAdminPermission.UNINSTALL);
        boolean result = false;
        Event startEvent = null;
        try {
        	startEvent = sendUninstallEvent(dp);
            session = createUninstallSession(dp);
            session.uninstall(false);
            if (!session.isCancelled()) {
	            removeDp(dp);
	            save();
	            result = true;
            } else
            	throw new DeploymentException(DeploymentException.CODE_CANCELLED);
        } catch (DeploymentException e) {
            throw e;
        } catch (Exception e) {
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                e.getMessage(), e);
        } finally {
            sendCompleteEvent(dp, startEvent, result);
            clearSession();
        }
    }

    /*
     * DeploymentPackageImpl calls this method to notify the Deploymnet Admin 
     * about uninstallation of a Deployment Package 
     * (DeploymentPackageImpl.uninstallForced(DeploymentPackageImpl) was called).
     */
    boolean uninstallForced(DeploymentPackageImpl dp) throws DeploymentException {
        waitIfBusy();
        checkPermission(dp, DeploymentAdminPermission.UNINSTALL_FORCED);
        Event startEvent = sendUninstallEvent(dp);
        boolean result = false;
        try {
            session = createUninstallSession(dp);
            boolean r = session.uninstall(true);
            if (!session.isCancelled()) {
	            // remove Deployment Package metainfo
            	removeDp(dp);
	            // persist
	            save();
	            
	            result = r;
            } else
            	throw new DeploymentException(DeploymentException.CODE_CANCELLED);
        } catch (Exception e) {
            logger.log(e);
        } finally {
            sendCompleteEvent(dp, startEvent, result);
            clearSession();
        }
        return result;
    }

	/*
     * Says how the Deployment Admin creates location for bundles form the 
     * symbolic name and version.
     */
    static String location(String symbName, Version version) {
        return DAConstants.LOCATION_PREFIX + symbName;
    }

    Vector getDeploymentPackages() {
        return dps;
    }
    
    DownloadAgent getDownloadAgent() {
        return (DownloadAgent) trackDownloadAgent.getService();
    }

    PluginDeployed getDeployedPlugin() {
        return pluginDeployed;
    }

	public NotificationService getNotificationService() {
		return (NotificationService) trackNfs.getService();
	}

}
