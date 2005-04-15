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
import java.security.Permission;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentSession;

public class DeploymentAdminImpl implements DeploymentAdmin, BundleActivator {
    
	private BundleContext 		  context;
	private ServiceRegistration   registration;
    private Logger 				  logger;
    private DeploymentSessionImpl session;
    
    // persisted fields
    private Set     dps = new HashSet();		// deployment packages
    private Integer nextDpId = new Integer(0);  // id of the next DP
    private boolean cancelled;
    
	public void start(BundleContext context) throws Exception {
		this.context = context;
		load();
        registration = context.registerService(DeploymentAdmin.class.getName(), this, null);
        logger = new Logger(context);
	}
	
	public void stop(BundleContext context) throws Exception {
	    registration.unregister();
	    logger.stop();
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
        WrappedJarInputStream wjis;
        DeploymentPackageImpl srcDp = null;
        cancelled = false;
        
        // create source DP
        try {
            wjis = new WrappedJarInputStream(in);
            srcDp = new DeploymentPackageImpl(wjis.getManifest(), nextDpId(), this);
        } catch (Exception e) {
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                    e.getMessage(), e);
        }
        
        checkPermission("name: "  + srcDp.getName(), 
                DeploymentAdminPermission.ACTION_INSTALL);
        session = createInstallSession(srcDp);
        try {
            session.go(wjis);
        } catch (CancelException e) {
            return null;
        }
        
        if (session.getDeploymentAction() == DeploymentSession.INSTALL)
            dps.add(srcDp);
        else if (session.getDeploymentAction() == DeploymentSession.UPDATE)
            ; // do nothing
        
        return srcDp;
	}

    private DeploymentSessionImpl createInstallSession(DeploymentPackageImpl dp) 
    		throws DeploymentException 
    {
        // find the package among installed packages
        DeploymentPackageImpl targetDp = findDp(dp);
        
        // not found -> install
        if (null == targetDp) {
            // creates an empty dp
            targetDp = new DeploymentPackageImpl();
	        return new DeploymentSessionImpl(dp, 
	                targetDp, DeploymentSession.INSTALL, logger, context);
        }
        // found -> update
        else {
            return new DeploymentSessionImpl(dp, targetDp,
                    DeploymentSession.UPDATE, logger, context);
        }
    }
    
    private DeploymentSessionImpl createUninstallSession(DeploymentPackageImpl dp) 
			throws DeploymentException 
	{
        // creates an empty dp
        DeploymentPackageImpl srcDp = new DeploymentPackageImpl();

        // find the package among installed packages
        DeploymentPackageImpl targetDp = findDp(dp);
        
        return new DeploymentSessionImpl(srcDp, 
                targetDp, DeploymentSession.UNINSTALL, logger, context);
	}

    private DeploymentPackageImpl findDp(DeploymentPackageImpl srcDp) {
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl dp = (DeploymentPackageImpl) iter.next();
            if (srcDp.equals(dp))
                return dp;
        }
        return null;
    }

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
		return (DeploymentPackage[]) dps.toArray(new DeploymentPackage[] {});
	}

    /*
     * Saves persistent data.
     */
    private synchronized void save() throws IOException {
        File f = context.getDataFile(this.getClass().getName() + ".obj");
        if (null == f) {
            logger.log(Logger.LOG_WARNING, "Platform does not have file system support. " + 
                    "Deployment packages cannot be persisted.");
            return;
        }
        
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(nextDpId);
        oos.writeObject(dps);
        oos.close();
        fos.close();
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

	private void checkPermission(String name, String action) {
	    SecurityManager sm = System.getSecurityManager();
	    if (null == sm)
	        return;
	    Permission perm = new DeploymentAdminPermission(name, action);
	    sm.checkPermission(perm);
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

    void uninstall(DeploymentPackageImpl dp) throws DeploymentException {
        // TODO checkPermission 

        session = createUninstallSession(dp);
        try {
            session.go(null);
        } catch (CancelException e) {
            return;
        }
        
        dps.remove(dp);
    }
    
    boolean uninstallForceful(DeploymentPackageImpl dp) {
        // TODO        
        dps.remove(dp);
        return false;
    }
    
    BundleContext getBundleContext() {
        return context;
    }
    
}
