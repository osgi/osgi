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

public class DeploymentAdminImpl implements DeploymentAdmin, BundleActivator {
    
	private BundleContext 		context;
	private ServiceRegistration registration;
    private Logger 				logger;
    
    private Set     dps = new HashSet();	// deployment packages
    private Integer nexDpId = new Integer(0);
	
	// BundleActivator interface impl.
	public void start(BundleContext context) throws Exception {
		this.context = context;
		load();
		
        registration = context.registerService(DeploymentAdmin.class.getName(), this, null);
        
        initLogger();
	}
	
	private void initLogger() {
        ServiceReference ref = context.getServiceReference(LogService.class.getName());
        if (null != ref)
            logger = new Logger((LogService) context.getService(ref));
        else
            logger = new Logger();
    }

	// BundleActivator interface impl.
	public void stop(BundleContext context) throws Exception {
	    registration.unregister();
	}
	
    private int nexDpId() {
        int ret = nexDpId.intValue(); 
        nexDpId = new Integer(ret + 1);
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
	
	// DeploymentAdmin interface impl.
	public DeploymentPackage installDeploymentPackage(InputStream in) {
		DeploymentPackageImpl dp = null;
        WrappedJarInputStream jis = null;
        
        try {
            jis = new WrappedJarInputStream(in);
            dp = new DeploymentPackageImpl(jis, context, this);
        }
        catch (Exception e) {
            // TODO exception has to be propagated to the caller instead
            e.printStackTrace();
            return null;
        }
        
        checkPermission(dp.getName(), "installDeploymentPackage");
        
        try {
            if (isUpdate(dp)) {
                // update
                DeploymentPackageImpl p = null;
                for (Iterator iter = dps.iterator(); iter.hasNext();) {
                    p = (DeploymentPackageImpl) iter.next();
                    if (dp.equals(p)) {
                        break;
                    }
                }
                if (null == p)
                    throw new RuntimeException("Internal error");
                p.update(jis);
            } else {
                // install
                dp.install();
                dp.setId(nexDpId());
                dps.add(dp);
            }
        }
        catch (Exception e) {
            // TODO exception has to be propagated to the caller instead
            logger.log(e);
            return null;
        }

		try {
            save();
        }
        catch (IOException e) {
            logger.log(Logger.LOG_ERROR, "Error occured during persisting " +
            		"deployment packages.");
            logger.log(e);
        }
		
		return dp;
	}

    // DeploymentAdmin interface impl.
	public DeploymentPackage[] listDeploymentPackages() {
	    checkPermission("", "listDeploymentPackages");
		return (DeploymentPackage[]) dps.toArray(new DeploymentPackage[0]);
	}
	
	// DeploymentAdmin interface impl.
    public String location(String symbName, String version) {
        return symbName;
    }

	
    private boolean isUpdate(DeploymentPackageImpl dp) {
        if (dp.fixPack())
            return true;
        
        for (Iterator iter = dps.iterator(); iter.hasNext();) {
            DeploymentPackageImpl element = (DeploymentPackageImpl) iter.next();
            if (dp.getName().equals(element.getName()))
                return true;
        }
        
        return false;
    }

    void onUninstallDp(DeploymentPackageImpl dp) {
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

    private void save() throws IOException {
        File f = context.getDataFile(this.getClass().getName() + ".obj");
        if (null == f) {
            logger.log(Logger.LOG_WARNING, "Platform does not have file system support. " + 
                    "Deployment packages cannot be persisted.");
            return;
        }
        
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(nexDpId);
        oos.writeObject(dps);
        oos.close();
        fos.close();
    }

    private void load() {
        File f = context.getDataFile(this.getClass().getName() + ".obj");
        if (!f.exists())  
            return;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            DPObjectInputStream ois = new DPObjectInputStream(fis);
            nexDpId = (Integer) ois.readObject();
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
	
	private void checkPermission(String filter, String action) {
	    SecurityManager sm = System.getSecurityManager();
	    if (null == sm)
	        return;
	    Permission perm = new DeploymentAdminPermission(filter, action);
	    sm.checkPermission(perm);
	}

}
