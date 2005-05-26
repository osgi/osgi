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
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.DeploymentSession;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.permissionadmin.PermissionAdmin;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentSessionImpl implements DeploymentSession {
    
    /*
     * The action value INSTALL indicates this session is associated with the 
     * installation of a deployment package.  
     */
    static final int INSTALL = 0;
    
    /*
     * The action value UPDATE indicates this session is associated with the 
     * update of a deployment package.  
     */
    static final int UPDATE = 1;

    /*
     * The action value UNINSTALL indicates this session is associated with the 
     * uninstalling of a deployment package.  
     */
    static final int UNINSTALL = 2;   
    
    private DeploymentPackageImpl       srcDp;
    private DeploymentPackageImpl       targetDp;
    private Transaction                 transaction;
    private Logger                      logger;
    private BundleContext               context;
    private TrackerRp                   trackRp;
    private TrackerPerm                 trackPerm;
    private TrackerCondPerm             trackCondPerm;
    
    // getDataFile uses this. 
    private String 				        fwBundleDir;
    
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
    
    /*
     * Class to track conditiona permission admin
     */
    private class TrackerCondPerm extends ServiceTracker {
        public TrackerCondPerm() {
            super(DeploymentSessionImpl.this.context, 
                    "org.osgi.service.condpermadmin.ConditionalPermissionAdmin", null);
        }
    }

    DeploymentSessionImpl(DeploymentPackageImpl srcDp, 
                          DeploymentPackageImpl targetDp, 
                          Logger logger, 
                          final BundleContext context,
                          String fwbd) 
    {
        this.srcDp = srcDp;
        this.targetDp = targetDp;
        this.logger = logger;
        this.context = context;
        trackRp = new TrackerRp();
        trackPerm = new TrackerPerm();
        trackCondPerm = new TrackerCondPerm();
        this.fwBundleDir = null == fwbd ? "" : fwbd;
        
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                StringBuffer sb = new StringBuffer(fwBundleDir);
                char sc = (sb.toString().indexOf("/") != -1 ? '/' : '\\');
                int i = sb.toString().indexOf(sc);
                while (-1 != i) {
                    sb.setCharAt(i, File.separatorChar);
                    i = sb.toString().indexOf(sc);
                }
                fwBundleDir = sb.toString();
                return null;
            }});
    }

    private Hashtable setFilePermissionForCustomizers() {
        Hashtable oldPerms = new Hashtable();
        ServiceReference sref = trackPerm.getServiceReference();
        if (null == sref)
            return oldPerms;
        
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                setFilePermissionForCustomizer(be, oldPerms);
        }
        
        return oldPerms;
    }

    private void setFilePermissionForCustomizer(BundleEntry beCust, Hashtable oldPerms) {
        String location = DeploymentAdminImpl.location(beCust.getSymbName(), 
                beCust.getVersion());
        PermissionAdmin pa = (PermissionAdmin) trackPerm.getService();
        
        PermissionInfo[] old = pa.getPermissions(location);
        if (null != old)
            oldPerms.put(location, old);
        ArrayList permInfos = null != old ? 
                new ArrayList(Arrays.asList(old)) : new ArrayList();
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            char fs = File.separatorChar;
            String rootDir = fwBundleDir + fs + be.getId() + fs + "data";
            permInfos.add(new PermissionInfo(FilePermission.class.getName(), rootDir + fs + "-", 
                    "read, write, execute, delete"));
        }
        pa.setPermissions(location, (PermissionInfo[]) permInfos.toArray(new PermissionInfo[] {}));
    }

    private void resetFilePermissionForCustomizers(Hashtable oldPerms) {
        ServiceReference sref = trackPerm.getServiceReference();
        if (null == sref)
            return;
        
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                resetFilePermissionForCustomizer(be, oldPerms);
        }
    }

    private void resetFilePermissionForCustomizer(BundleEntry beCust, Hashtable oldPerms) {
        String location = DeploymentAdminImpl.location(beCust.getSymbName(), 
                beCust.getVersion());
        PermissionAdmin pa = (PermissionAdmin) trackPerm.getService();
        pa.setPermissions(location, null);
        pa.setPermissions(location, (PermissionInfo[]) oldPerms.get(location));
    }

    int getDeploymentAction() {
        Version verZero = new Version(0, 0, 0);
        Version verSrc = srcDp.getVersion();
        Version verTarget = targetDp.getVersion();
        if (!verZero.equals(verSrc) && !verZero.equals(verTarget))
            return UPDATE;
        if (verZero.equals(verTarget))
            return INSTALL;
        return UNINSTALL;
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
    
    private void openTrackers() {
        trackCondPerm.open();
        trackPerm.open();
        trackRp.open();
    }
    
    private void closeTrackers() {
        trackCondPerm.close();
        trackPerm.close();
        trackRp.close();
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentSession#getDataFile(org.osgi.framework.Bundle)
     */
    public File getDataFile(Bundle b) {
        DeploymentPackageImpl dp;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UPDATE == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        
        Vector bes = dp.getBundleEntries();
        for (Iterator iter = bes.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getId() == b.getBundleId()) {
                String dir = fwBundleDir + "/" + b.getBundleId() + "/data";
                return new File(dir);
            }
        }
        
        throw new SecurityException("Bundle: " + b + " is not part of the deployment " +
        		"package: " + dp);
    }

    void installUpdate(DeploymentPackageJarInputStream wjis) throws DeploymentException {
        openTrackers();
        transaction = Transaction.createTransaction(this, logger);
        try {
            transaction.start();
            stopBundles();
            processBundles(wjis);
            Hashtable oldPerms = setFilePermissionForCustomizers();
            startCustomizers();
            processResources(wjis);
            dropResources();
            resetFilePermissionForCustomizers(oldPerms);
            dropBundles();
            startBundles();
        } catch (CancelException e) {
            throw e;
        } catch (DeploymentException e) {
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                    e.getMessage(), e);
        }
        transaction.commit();
        closeTrackers();
    }
    
    void uninstall() throws DeploymentException {
        openTrackers();
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
        closeTrackers();
    }
    
    private void startBundles() throws BundleException {
        DeploymentPackageImpl dp = null;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UNINSTALL == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
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
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UPDATE == getDeploymentAction())
            dp = srcDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.getId());
            if (!entry.isCustomizer())
                continue;
            startBundle(b);
        }
    }
    
    private void startBundle(final Bundle b) throws BundleException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    if (b.getState() != Bundle.ACTIVE)
                        b.start();
                    return null;
                }});
        }
        catch (PrivilegedActionException e) {
            throw (BundleException) e.getException();
        }
        transaction.addRecord(new TransactionRecord(Transaction.STARTBUNDLE, b));
    }

    private void stopBundles() throws BundleException {
        if (INSTALL == getDeploymentAction())
            return;
        
        for (Iterator iter = targetDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.getId());
            stopBundle(b);
        }
    }
    
    private void stopBundle(final Bundle b) throws BundleException {
    	try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    b.stop();
                    return null;
                }});
        }
        catch (PrivilegedActionException e) {
            throw (BundleException) e.getException();
        }
        transaction.addRecord(new TransactionRecord(Transaction.STOPBUNDLE, b));
    }

    private void processResources(DeploymentPackageJarInputStream wjis) 
    		throws DeploymentException, IOException 
    {
        DeploymentPackageImpl dp;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UPDATE == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        
        DeploymentPackageJarInputStream.Entry entry = wjis.nextEntry();
        dp.updateResourceEntry(entry);
        while (null != entry) {
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
            targetDp.getResourceEntries().remove(re);
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
            WrappedResourceProcessor proc = new WrappedResourceProcessor(
                    findProcessor(pid), fetchAccessControlContext(re.getCertChains()));
            //ResourceProcessor proc = findProcessor(pid);
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
            targetDp.getBundleEntries().remove(be);
            dropBundle(be);
        }
    }

    /*
     * Drops a particluar resource
     */
    private void dropResource(ResourceEntry re) throws DeploymentException {
        ResourceProcessor p = findProcessor(re.getValue(DAConstants.RP_PID));
        WrappedResourceProcessor proc = new WrappedResourceProcessor(
                p, fetchAccessControlContext(re.getCertChains()));
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
    
    private AccessControlContext fetchAccessControlContext(List certChains) {
        AccessControlContext ret = null;
        ServiceReference sref = trackCondPerm.getServiceReference();
        if (null != sref) {
            ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) 
            		trackCondPerm.getService(sref);
            for (Iterator iter = certChains.iterator(); iter.hasNext();) {
                String[] chain = (String[]) iter.next();
                ret = cpa.getAccessControlContext(chain);
                if (null != ret)
                    return ret;
            }
            return ret;
        } else {
            return createAccessControlContext();
        }
    }
    
    private AccessControlContext createAccessControlContext() {
        // TODO MEG Deployment Admin must construct the AccessControlContext itself, 
        // using permission information in the DMT
        return null;
    }

    private void processResource(final DeploymentPackageJarInputStream.Entry entry) 
    		throws DeploymentException, IOException 
    {
        String pid = entry.getAttributes().getValue(DAConstants.RP_PID);
        WrappedResourceProcessor proc = new WrappedResourceProcessor(
                findProcessor(pid), fetchAccessControlContext(entry.getCertificateStringChains()));
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, proc));
        proc.process(entry.getName(), entry.getInputStream());
        if (INSTALL == getDeploymentAction())
            srcDp.setProcessorPid(entry.getName(), pid);
        else if (UPDATE == getDeploymentAction())
            targetDp.setProcessorPid(entry.getName(), pid);    
    }
    
    private void processBundles(DeploymentPackageJarInputStream wjis) 
    		throws BundleException, IOException, DeploymentException 
    {
        DeploymentPackageJarInputStream.Entry entry = wjis.nextEntry();
        while (null != entry && entry.isBundle()) 
        {
            checkEntry(entry);
            
            if (!entry.isMissing()) {
                Bundle b = processBundle(entry);
                if (entry.isCustomizerBundle())
                    startBundle(b);
            } else
                ; // do nothing
            wjis.closeEntry();
            entry = wjis.nextEntry();
        }
    }
    
    private void checkEntry(Entry entry) throws DeploymentException {
        BundleEntry be = new BundleEntry(entry);
        
        if (!srcDp.getBundleEntries().contains(be))
            throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR, 
                    entry.getName() + " is in the deployment package but doesn't " +
                    "exist in the manifest");
    }

    private Bundle processBundle(DeploymentPackageJarInputStream.Entry entry) 
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

    private Bundle installBundle(BundleEntry be, final InputStream is)
    		throws BundleException
    {
        final String location = DeploymentAdminImpl.location(be.getSymbName(), 
                be.getVersion());
        Bundle b;
        try {
            b = (Bundle) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws Exception {
                    return context.installBundle(location, is);
                }});
        }
        catch (PrivilegedActionException e) {
            throw (BundleException) e.getException();
        }
        be.setId(b.getBundleId());
		transaction.addRecord(new TransactionRecord(Transaction.INSTALLBUNDLE, b));
		return b;
    }
    
    private Bundle updateBundle(BundleEntry be, final InputStream is)
			throws BundleException 
    {
        final String location = DeploymentAdminImpl.location(be.getSymbName(),
                be.getVersion());
        final Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            final Bundle b = bundles[i];
            Boolean found;
           	try {
                found = (Boolean) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws BundleException {
                        if (b.getLocation().equals(location)) {
                            // TODO use framework backdoor for transactionality
                            b.update(is);
                            return new Boolean(true);
                        }
                        return new Boolean(false);
                    }});
            }
            catch (PrivilegedActionException e) {
                throw (BundleException) e.getException();
            }
            if (found.booleanValue()) { 
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
