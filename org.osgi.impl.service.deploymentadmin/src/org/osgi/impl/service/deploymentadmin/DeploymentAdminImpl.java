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
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.log.LogService;

public class DeploymentAdminImpl implements DeploymentAdmin, BundleActivator, Runnable {
    
	private BundleContext 		  context;
	private ServiceRegistration   registration;
    private Logger 				  logger;
    private boolean 			  busy;
    private Thread			  	  thread;
    private Transaction			  transaction;

    private static final int      ACTION_INSTALL   = 0;
    private static final int      ACTION_UPDATE    = 1;
    private static final int      ACTION_UNINSTALL = 2;
    private int 			      actAction;
    private DeploymentPackageImpl actDp;
    private WrappedJarInputStream actJis;
    
    // persisted fields
    private Set     dps = new HashSet();		// deployment packages
    private Integer nextDpId = new Integer(0);  // id of the next DP
    
	public void start(BundleContext context) throws Exception {
		this.context = context;
		load();
        registration = context.registerService(DeploymentAdmin.class.getName(), this, null);
        initLogger();
        transaction = Transaction.createTransaction(logger);
	}
	
	public void stop(BundleContext context) throws Exception {
	    registration.unregister();
	}

	private void initLogger() {
        ServiceReference ref = context.getServiceReference(LogService.class.getName());
        if (null != ref)
            logger = new Logger((LogService) context.getService(ref));
        else
            logger = new Logger();
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

    private Thread newThread() {
        return new Thread(this, "DeploymentAdmin scheduler thread");
    }

    public synchronized void uninstallDeploymentPackage(long id) {
        waitTillBusy();
        
        actDp = (DeploymentPackageImpl) getDeploymentPackage(id);
        if (null == actDp)
            //TODO DeploymentAdminException has to be propagated to the caller instead
            throw new RuntimeException("Internal error");
        
        actAction = ACTION_UNINSTALL;
        thread = newThread();
        thread.start();
    }
    
    public synchronized DeploymentPackage installDeploymentPackage(InputStream in) {
	    waitTillBusy();
        
        try {
            actJis = new WrappedJarInputStream(in);
            actDp = new DeploymentPackageImpl(actJis, context, this, logger, transaction);
        }
        catch (Exception e) {
            // TODO exception has to be propagated to the caller instead
            e.printStackTrace();
            return null;
        }
        
        checkPermission("name: "  + actDp.getName(), 
                DeploymentAdminPermission.ACTION_INSTALL_DP);

        thread = newThread();
        if (isUpdate(actDp)) {
            actAction = ACTION_UPDATE;
            
            // find the package to update
            DeploymentPackageImpl p = null;
            for (Iterator iter = dps.iterator(); iter.hasNext();) {
                p = (DeploymentPackageImpl) iter.next();
                if (actDp.equals(p))
                    break;
            }
            if (null == p)
                // TODO propagate to the caller
                throw new RuntimeException("Internal error");
            else
                actDp = p;
        } else {
            actAction = ACTION_INSTALL;
            actDp.setId(nextDpId());
        }
		
        thread.start();
        
		return actDp;
	}

	public DeploymentPackage getDeploymentPackage(long id) {
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl p = (DeploymentPackageImpl) iter.next();
            if (actDp.getId() == id)
                return p;
        }
        return null;
	}
	
	public boolean cancel() {
	    if (null == actDp)
	        return false;
	    else  {
	        transaction.cancel(); 
	        return true;
	    }
	}
	
    private synchronized void waitTillBusy() {
        while (busy) {
            try {
                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        busy = true;
    }

	public synchronized DeploymentPackage[] listDeploymentPackages() {
	    // TODO checkPermission("", DeploymentAdminPermission.ACTION_LIST_DPS);
		return (DeploymentPackage[]) dps.toArray(new DeploymentPackage[0]);
	}
	
    public String location(String symbName, String version) {
        return symbName;
    }
	
    private synchronized boolean isUpdate(DeploymentPackageImpl dp) {
        if (dp.fixPack())
            return true;
        
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl element = (DeploymentPackageImpl) iter.next();
            if (dp.getName().equals(element.getName()))
                return true;
        }
        
        return false;
    }

    // TODO eliminate this
    synchronized void onUninstallDp(DeploymentPackageImpl dp) {
	    dps.remove(dp);
	    try {
            save();
        }
        catch (IOException e) {
            logger.log(Logger.LOG_ERROR, "Error occured during persisting " +
            		"deployment packages.");
            logger.log(e);
        }
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

    public void run() {
        transaction.start(actDp);
        try {
	        switch (actAction) {
	            case ACTION_INSTALL :
	                actDp.install();
	                dps.add(actDp);
	                break;
	            case ACTION_UPDATE :
	                actDp.update(actJis);
	                break;
	            case ACTION_UNINSTALL :
	                actDp.uninstall();
	                break;
	            default :
	                break;
	        }
	        
	        save();
	        transaction.commit();
	        actDp = null;
        } catch (Exception e) {
            transaction.rollback();
            logger.log(e);
        } finally {
            if (null != actJis) {
                try {
                    actJis.close();
                }
                catch (IOException e) {
                    logger.log(e);
                }
            }
            
            busy = false;
        }
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
                dp.setContext(context);
                dp.setDeploymentAdmin(DeploymentAdminImpl.this);
                dp.startTracker();
            }
            
            return obj;
        }
	}
    
}
