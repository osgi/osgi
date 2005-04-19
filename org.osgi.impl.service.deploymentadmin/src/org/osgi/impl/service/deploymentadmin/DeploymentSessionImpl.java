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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentSession;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentSessionImpl implements DeploymentSession {
    
    private DeploymentPackageImpl       srcDp;
    private DeploymentPackageImpl       targetDp;
    private int                         action;
    private Transaction                 transaction;
    private Logger                      logger;
    private BundleContext               context;
    private TrackerRp                   trackRp;
    private TrackerPerm                 trackPerm;
    
    // getDataFile uses this. 
    private String 						fwBundleDir;
    
    /*
     * Class to track resource processors
     */
    private class TrackerRp extends ServiceTracker {
        public TrackerRp() {
            super(DeploymentSessionImpl.this.context, 
                    ResourceProcessor.class.getName(), null);
        }
    }
    
    /*
     * Class to track policy admin
     */
    private class TrackerPerm extends ServiceTracker {
        public TrackerPerm() {
            super(DeploymentSessionImpl.this.context, 
                    "org.osgi.service.permissionadmin.PermissionAdmin", null);
        }
    }

    DeploymentSessionImpl(DeploymentPackageImpl srcDp, DeploymentPackageImpl targetDp, int action,
            Logger logger, BundleContext context) 
    {
        this.srcDp = srcDp;
        this.targetDp = targetDp;
        this.action = action;
        this.logger = logger;
        this.context = context;
        trackRp = new TrackerRp();
        trackPerm = new TrackerPerm();
        
        fwBundleDir = (String) context.getBundle().getHeaders().
        	get(DAConstants.FW_BUNDLES_DIR);
        logger.log(Logger.LOG_INFO, "Framework's bundles dir: " + fwBundleDir);
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentSession#getDeploymentAction()
     */
    public int getDeploymentAction() {
        return action;
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentSession#getTargetDeploymentPackage()
     */
    public DeploymentPackage getTargetDeploymentPackage() {
        return targetDp;
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentSession#getSourceDeploymentPackage()
     */
    public DeploymentPackage getSourceDeploymentPackage() {
        return srcDp;
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentSession#getDataFile(org.osgi.framework.Bundle)
     */
    public File getDataFile(Bundle b) {
        DeploymentPackageImpl dp;
        if (DeploymentSession.INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (DeploymentSession.UPDATE == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        
        Vector bes = dp.getBundleEntries();
        for (Iterator iter = bes.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getId() == b.getBundleId()) {
                ServiceReference sref = trackPerm.getServiceReference();
                if (null != sref) {
                    PermissionAdmin pa = (PermissionAdmin) trackPerm.getService();
                    // TODO
                }
                               
                String dir = fwBundleDir + "/" + b.getBundleId() + "/data";
                return new File(dir);
            }
        }
        
        throw new SecurityException("Bundle: " + b + " is not part of the deployment " +
        		"package: " + dp);
    }

    void installUpdate(WrappedJarInputStream wjis) throws DeploymentException {
        trackRp.open();
        trackPerm.open();
        transaction = Transaction.createTransaction(this, logger);
        try {
            transaction.start();
            stopBundles();
            processBundles(wjis);
            startCustomizers();
            processResources(wjis);
            dropResources();
            dropBundles();
            startBundles();
        } catch (CancelException e) {
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                    e.getMessage(), e);
        }
        transaction.commit();
        trackRp.close();
        trackPerm.close();
    }
    
    void uninstall() throws DeploymentException {
        trackRp.open();
        trackPerm.open();
        transaction = Transaction.createTransaction(this, logger);
        try {
            transaction.start();
            stopBundles();
            startCustomizers();
            dropAllResources();
            dropBundles();
        } catch (CancelException e) {
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                    e.getMessage(), e);
        }
        transaction.commit();
        trackRp.close();
        trackPerm.close();
    }
    
    private void startBundles() throws BundleException {
        DeploymentPackageImpl dp = null;
        if (DeploymentSession.INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (DeploymentSession.UNINSTALL == getDeploymentAction())
            dp = targetDp;
        else
            dp = targetDp;
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.getId());
            if (entry.isCustomizer())
                continue;
            startBundle(b);
        }
    }
    
    private void startCustomizers() throws BundleException {
        DeploymentPackageImpl dp;
        if (DeploymentSession.INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (DeploymentSession.UPDATE == getDeploymentAction())
            dp = srcDp;
        else
            dp = targetDp;
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.getId());
            if (!entry.isCustomizer())
                continue;
            startBundle(b);
        }
    }
    
    private void startBundle(Bundle b) throws BundleException {
        if (b.getState() != Bundle.ACTIVE)
            b.start();
        transaction.addRecord(new TransactionRecord(Transaction.STARTBUNDLE, b));
    }

    private void stopBundles() throws BundleException {
        if (DeploymentSession.INSTALL == getDeploymentAction())
            return;
        
        for (Iterator iter = targetDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.getId());
            stopBundle(b);
        }
    }
    
    private void stopBundle(Bundle b) throws BundleException {
        b.stop();
        transaction.addRecord(new TransactionRecord(Transaction.STOPBUNDLE, b));
    }

    private void processResources(WrappedJarInputStream wjis) 
    		throws DeploymentException, IOException 
    {
        WrappedJarInputStream.Entry entry = wjis.nextEntry();
        while (null != entry) 
        {
            if (!entry.isResource())
                throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR, 
                        "Bundles have to precede resources in the deployment package");
            
            if (!entry.isMissing())
                processResource(entry);
            else
                ; // do nothing
            wjis.closeEntry();
            entry = wjis.nextEntry();
        }
    }

    /*
     * Drop rsources that don't present in the DP and are not 
     * marked as missing resources
     */
    private void dropResources() throws DeploymentException {
        Set toDrop = new HashSet(targetDp.getResourceEntries());
        Set tmpSet = new HashSet(srcDp.getResourceEntries());
        toDrop.removeAll(tmpSet);
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            dropResource(re);
        }
    }
    
    /*
     * Drop all rsources pf the target DP
     */
    private void dropAllResources() throws DeploymentException {
        Set toDrop = new HashSet(targetDp.getResourceEntries());
        Set procs = new HashSet();
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            String pid = re.getValue(DAConstants.RP_PID);
            ResourceProcessor proc = findProcessor(pid);
            if (!procs.contains(pid)) {
                transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, proc));
                proc.dropAllResources();
                procs.add(pid);
            }
        }
    }
    
    /*
     * Drop bundles that don't present in the DP and are not 
     * marked as missing resources
     */
    private void dropBundles() throws BundleException {
        Set toDrop = new HashSet(targetDp.getBundleEntries());
        Set tmpSet = new HashSet(srcDp.getBundleEntries());
        toDrop.removeAll(tmpSet);
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            dropBundle(be);
        }
    }

    /*
     * Drops a particluar resource
     */
    private void dropResource(ResourceEntry re) throws DeploymentException {
        ResourceProcessor proc = findProcessor(re.getValue(DAConstants.RP_PID));
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, proc));
        proc.dropped(re.getName());
    }
    
    /*
     * Drops a particluar bundle
     */
    private void dropBundle(BundleEntry be) throws BundleException {
        Bundle b = context.getBundle(be.getId());
        if (null == b) {
            // TODO ???
            return;
        }
        transaction.addRecord(new TransactionRecord(
                Transaction.UNINSTALLBUNDLE, 
                b,
                be,
                targetDp));
        
        // Bundle.uninstall() is called by the transaction instance
    }

    private ResourceProcessor findProcessor(String pid) {
        ServiceReference[] refs = trackRp.getServiceReferences();
        for (int i = 0; i < refs.length; i++) {
            ServiceReference ref = refs[i];
            String s_pid = (String) ref.getProperty(Constants.SERVICE_PID);
            if (pid.equals(s_pid))
                return (ResourceProcessor) trackRp.getService(ref);
        }
        return null;
    }
    
    private void processResource(WrappedJarInputStream.Entry entry) 
    		throws DeploymentException, IOException 
    {
        String pid = entry.getAttributes().getValue(DAConstants.RP_PID);
        ResourceProcessor proc = findProcessor(pid);
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, proc));
        proc.process(entry.getName(), entry.getInputStream());
        if (DeploymentSession.INSTALL == getDeploymentAction())
            srcDp.setProcessorPid(entry.getName(), pid);
        else if (DeploymentSession.UPDATE == getDeploymentAction())
            targetDp.setProcessorPid(entry.getName(), pid);    
    }
    
    private void processBundles(WrappedJarInputStream wjis) 
    		throws BundleException, IOException 
    {
        WrappedJarInputStream.Entry entry = wjis.nextEntry();
        while (null != entry && entry.isBundle()) 
        {
            if (!entry.isMissing()) {
                Bundle b = processBundle(entry);
                if (entry.isCustomizerBundle())
                    startBundle(b);
            }
            else
                ; // do nothing
            wjis.closeEntry();
            entry = wjis.nextEntry();
        }
    }
    
    private Bundle processBundle(WrappedJarInputStream.Entry entry) 
    		throws BundleException, IOException 
    {
        Bundle ret;
        BundleEntry be = new BundleEntry(entry);
        Vector srcEntries = srcDp.getBundleEntries();
        Vector targetEntries = targetDp.getBundleEntries();
        if (targetEntries.contains(be)) {
            ret = updateBundle(be, entry.getInputStream());
        } else {
            ret = installBundle(be, entry.getInputStream());
        }
        srcDp.updateBundleEntry(be);
        return ret;
    }

    private Bundle installBundle(BundleEntry be, InputStream is)
    		throws BundleException
    {
        Bundle b = context.installBundle(be.getSymbName(), is);
		be.setId(b.getBundleId());
		transaction.addRecord(new TransactionRecord(Transaction.INSTALLBUNDLE, b));
		return b;
    }
    
    private Bundle updateBundle(BundleEntry be, InputStream is)
			throws BundleException 
    {
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            Bundle b = bundles[i];
            if (b.getLocation().equals(be.getSymbName())) {
                b.update(is);
                transaction.addRecord(new TransactionRecord(Transaction.UPDATEBUNDLE, b));
                be.setId(b.getBundleId());
                return b;
            }
        }
        return null;
    }
    
    public void cancel() {
        transaction.cancel();
    }

}
