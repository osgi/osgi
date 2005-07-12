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

import java.io.*;
import java.security.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.deploymentadmin.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.*;
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
    private TrackerPackageAdmin         trackPackAdmin;
    private DeploymentAdminImpl         da;
    private boolean                     forced;
    
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

    /*
     * Class to track package admin
     */
    private class TrackerPackageAdmin extends ServiceTracker {
        public TrackerPackageAdmin() {
            super(DeploymentSessionImpl.this.context, 
                    "org.osgi.service.packageadmin.PackageAdmin", null);
        }
    }

    DeploymentSessionImpl(DeploymentPackageImpl srcDp, 
                          DeploymentPackageImpl targetDp, 
                          Logger logger, 
                          final BundleContext context,
                          String fwbd,
                          DeploymentAdminImpl da) 
    {
        this.srcDp = srcDp;
        this.targetDp = targetDp;
        this.logger = logger;
        this.context = context;
        trackRp = new TrackerRp();
        trackPerm = new TrackerPerm();
        trackCondPerm = new TrackerCondPerm();
        trackPackAdmin = new TrackerPackageAdmin();
        this.fwBundleDir = null == fwbd ? "" : fwbd;
        this.da = da;
        
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
        
        for (Iterator iter = srcDp.getBundleEntryIterator(); iter.hasNext();) {
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
        for (Iterator iter = srcDp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            char fs = File.separatorChar;
            String rootDir = fwBundleDir + fs + be.getBundleId() + fs + "data";
            permInfos.add(new PermissionInfo(FilePermission.class.getName(), rootDir + fs + "-", 
                    "read, write, execute, delete"));
        }
        pa.setPermissions(location, (PermissionInfo[]) permInfos.toArray(new PermissionInfo[] {}));
    }

    private void resetFilePermissionForCustomizers(Hashtable oldPerms) {
        ServiceReference sref = trackPerm.getServiceReference();
        if (null == sref)
            return;
        
        for (Iterator iter = srcDp.getBundleEntryIterator(); iter.hasNext();) {
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
        trackPackAdmin.open();
    }
    
    private void closeTrackers() {
        trackCondPerm.close();
        trackPerm.close();
        trackRp.close();
        trackPackAdmin.close();
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentSession#getDataFile(org.osgi.framework.Bundle)
     */
    public File getDataFile(Bundle b) {
        Permission perm = new DeploymentCustomizerPermission(
            "(name=" + b.getSymbolicName() + ")", 
            DeploymentCustomizerPermission.ACTION_PRIVATEAREA);
        SecurityManager sm = System.getSecurityManager();
        if (null != sm)
            sm.checkPermission(perm);
        
        DeploymentPackageImpl dp;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UPDATE == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        
        Set bes = dp.getBundleEntriesAsSet();
        for (Iterator iter = bes.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getBundleId() == b.getBundleId()) {
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
        Hashtable oldPerms = null;
        int numOfErrors = 0;
        try {
            transaction.start();
            stopBundles();
            processBundles(wjis);
            oldPerms = setFilePermissionForCustomizers();
            startCustomizers();
            processResources(wjis);
            dropResources();
            dropBundles();
            refreshPackages();
            numOfErrors = startBundles();
        } catch (CancelException e) {
            transaction.rollback();
            throw e;
        } catch (DeploymentException e) {
            transaction.rollback();
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                    e.getMessage(), e);
        } finally {
            if (null != oldPerms)
                resetFilePermissionForCustomizers(oldPerms);
        }
        transaction.commit();
        closeTrackers();
        if (numOfErrors > 0) {
            // TODO throw Exception here
        }
    }
    
    private void refreshPackages() {
        final PackageAdmin packAdmin = (PackageAdmin) trackPackAdmin.getService();
        if (null != packAdmin) {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    packAdmin.refreshPackages(null);
                    return null;
                }});
        }
    }

    boolean uninstall(boolean forced) throws DeploymentException {
        this.forced = forced;
        boolean ret = true;
        openTrackers();
        transaction = Transaction.createTransaction(this, logger);
        try {
            transaction.start();
            stopBundles();
            startCustomizers();
            dropAllResources();
            dropBundles();
        } catch (CancelException e) {
            transaction.rollback();
            throw e;
        } catch (Exception e) {
            if (!forced) {
                transaction.rollback();
                throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                        e.getMessage(), e);
            }  
            logger.log(e);
            ret = false;
        }
        transaction.commit();
        closeTrackers();
        return ret;
    }
    
    private int startBundles() {
        DeploymentPackageImpl dp = null;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UNINSTALL == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        int numOfErrors = 0;
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.getBundleId());
            if (entry.isCustomizer())
                continue;
            try {
                startBundle(b);
            }
            catch (BundleException e) {
                logger.log(e);
                ++numOfErrors;
            }
        }
        
        return numOfErrors;
    }
    
    private void startCustomizers() throws Exception {
        DeploymentPackageImpl dp;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UPDATE == getDeploymentAction())
            dp = srcDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        for (Iterator iter = dp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            try {
                Bundle b = context.getBundle(entry.getBundleId());
                if (!entry.isCustomizer())
                    continue;
                startBundle(b);
                ServiceReference sref = b.getRegisteredServices()[0];
                String pid = (String) sref.getProperty(Constants.SERVICE_PID);
                entry.setPid(pid);
            } catch (Exception e) {
                if (forced)
                    logger.log(e);
                else
                    throw e;
            }
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

    private void stopBundles() {
        if (INSTALL == getDeploymentAction())
            return;
        
        for (ListIterator iter = targetDp.getReverseBundleEntryIterator(); iter.hasPrevious();) {
            BundleEntry entry = (BundleEntry) iter.previous();
            try {
	            Bundle b = context.getBundle(entry.getBundleId());
	            stopBundle(b);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                logger.log(e);
            }
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
        DeploymentPackageJarInputStream.Entry entry = wjis.nextEntry();
        while (null != entry) {
            if (!entry.isResource())
                throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR, 
                        "Bundles have to precede resources in the deployment package");
            
            if (!entry.isMissing()) {
                processResource(entry);
                ResourceEntry re = srcDp.getResourceEntryByName(entry.getName());
                re.updateCertificates(entry);
            } else
                ; // do nothing
            wjis.closeEntry();
            entry = wjis.nextEntry();
        }
    }

    /*private boolean isLocalizationEntry(Entry entry) {
        String prefix = srcDp.getHeader(DAConstants.BUNDLE_LOCALIZATION);
        if (null == prefix || prefix.trim().equals(""))
            prefix = "OSGI-INF/l10n/bundle";
        
        return entry.getName().startsWith(prefix);
    }*/

    /*
     * Drops resources that don't present in the source package and are not 
     * marked as missing resources
     */
    private void dropResources() {
        Set toDrop = targetDp.getResourceEntriesAsSet();
        Set tmpSet = srcDp.getResourceEntriesAsSet();
        toDrop.removeAll(tmpSet);
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            try {
                ResourceEntry re = (ResourceEntry) iter.next();
                targetDp.remove(re);
                dropResource(re);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                logger.log(e);
            }
        }
    }
    
    /*
     * Drops all rsources of the target DP
     */
    private void dropAllResources() throws Exception {
        // gathers resource processors that have alerady been called 
        Set procs = new HashSet();

        Set toDrop = new HashSet(targetDp.getResourceEntriesAsSet());
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            try {
	            ResourceEntry re = (ResourceEntry) iter.next();
	            
	            String pid = re.getValue(DAConstants.RP_PID);
	            if (null == pid)
	                continue;
	            
	            ResourceProcessor proc = findProcessor(pid);
	            if (null == proc)
	                throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND, 
	                    "Resource processor for pid " + pid + " is not found");
	            
	            WrappedResourceProcessor wProc = new WrappedResourceProcessor(proc, 
	                    fetchAccessControlContext(re.getCertChains()));
	            
	            // each processor is called only once
	            if (!procs.contains(pid)) {
	                transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wProc));
	                wProc.dropAllResources();
	                procs.add(pid);
	            }
            } catch (Exception e) {
                if (forced)
                    logger.log(e);
                else
                    throw e;
            }
        }
    }
    
    /*
     * Drop bundles that don't present in the DP and are not 
     * marked as missing resources
     */
    private void dropBundles() {
        // the sets contain SymbolicNames and not BundleEntries because
        // the BundleEntry.equals checks name and version but the version 
        // in the source is different from the version in the target.
        Set toDrop = new HashSet();
        for (Iterator iter = targetDp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            toDrop.add(be.getSymbName());
        }

        Set tmpSet = new HashSet();
        for (Iterator iter = srcDp.getBundleEntryIterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            tmpSet.add(be.getSymbName());
        }
        
        toDrop.removeAll(tmpSet);
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            try {	
                String bsn = (String) iter.next();
            	BundleEntry be = targetDp.getBundleEntry(bsn);
            	targetDp.remove(be);
                dropBundle(be);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                logger.log(e);
            }
        }
    }

    /*
     * Drops a particluar resource
     */
    private void dropResource(ResourceEntry re) throws DeploymentException {
        String pid = re.getValue(DAConstants.RP_PID);
        ResourceProcessor proc = findProcessor(pid);
        if (null == proc)
            throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND,
                "Resource processor for pid " + pid + "is not found");
        WrappedResourceProcessor wProc = new WrappedResourceProcessor(
                proc, fetchAccessControlContext(re.getCertChains()));
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wProc));
        wProc.dropped(re.getResName());
    }
    
    /*
     * Drops a bundle
     */
    private void dropBundle(BundleEntry be) throws BundleException, DeploymentException {
        Bundle b = context.getBundle(be.getBundleId());
        if (null == b)
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                "Bundle " + b + " was removed directly");
        transaction.addRecord(new TransactionRecord(Transaction.UNINSTALLBUNDLE, 
                b, be, targetDp));
    }

    /*
     * Finds a Resource processor to the given PID
     */
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
        ServiceReference sref = trackCondPerm.getServiceReference();
        if (null != sref) {
            ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) 
            		trackCondPerm.getService(sref);
			ArrayList chainsAsStrings = new ArrayList();
            for (Iterator iter = certChains.iterator(); iter.hasNext();) {
                String[] chain = (String[]) iter.next();
				if (chain.length==0) continue;
				StringBuffer chainAsString = new StringBuffer(chain[0]);
				for(int i=1;i<chain.length;i++) {
					chainAsString.append(';');
					chainAsString.append(chain[i]);
				}
				chainsAsStrings.add(chainAsString.toString());
            }
			String [] signers = new String[chainsAsStrings.size()];
			signers = (String[]) chainsAsStrings.toArray(signers);
			return cpa.getAccessControlContext(signers);
        } 
        return createAccessControlContext();
    }
    
    private AccessControlContext createAccessControlContext() {
        // TODO 
        // If there is no ConditionalPermissionAdmin running Deployment Admin 
        // must construct the AccessControlContext itself, using permission 
        // information in the DMT
        return null;
    }

    private void processResource(final DeploymentPackageJarInputStream.Entry entry) 
    		throws DeploymentException, IOException 
    {
        String pid = entry.getAttributes().getValue(DAConstants.RP_PID);
        if (null == pid)
            return;
        String mDp = da.getMappedDp(pid);
        if (null != mDp && !srcDp.getName().equals(mDp))
            throw new DeploymentException(DeploymentException.CODE_FOREIGN_CUSTOMIZER,
                    "PID '" + pid + "' belongs to another DP (" + mDp + ")");
            
        ResourceProcessor proc = findProcessor(pid);
        if (null == proc)
            throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND, 
                    "Resource processor (PID=" + pid + ") for '" + entry.getName() +
                    "' is not found.");
        WrappedResourceProcessor wrProc = new WrappedResourceProcessor(
                proc, fetchAccessControlContext(entry.getCertificateChainStringArrays()));
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wrProc));
        wrProc.process(entry.getName(), entry.getInputStream());
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
            checkManifestEntryPresence(entry);
            
            if (!entry.isMissing()) {
                Bundle b = processBundle(entry);
                srcDp.getBundleEntryByName(entry.getName()).
                	setBundleId(b.getBundleId());
            } else {
                BundleEntry beS = srcDp.getBundleEntryByName(entry.getName());
                BundleEntry beT = targetDp.getBundleEntryByName(entry.getName());
                srcDp.remove(beS);
                srcDp.add(beT);
            }
            wjis.closeEntry();
            entry = wjis.nextEntry();
        }
    }
    
    private void checkManifestEntryPresence(Entry entry) throws DeploymentException {
        BundleEntry be = new BundleEntry(entry);
        if (!srcDp.contains(be))
            throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR, 
                    entry.getName() + " is in the deployment package but doesn't " +
                    "exist in the manifest");
    }

    private Bundle processBundle(DeploymentPackageJarInputStream.Entry entry) 
    		throws BundleException, IOException, DeploymentException 
    {
        BundleEntry be = new BundleEntry(entry);
        Bundle b;
        if (null != targetDp.getBundleEntry(be.getSymbName())) {
            b = updateBundle(be, entry.getInputStream());
        } else {
            b = installBundle(be, entry.getInputStream());
        }
        checkDpBundleConformity(b);
        return b;
    }
    
    private void checkDpBundleConformity(Bundle b) throws DeploymentException {
        BundleEntry be = new BundleEntry(b);
        if (!srcDp.contains(be))
            throw new DeploymentException(DeploymentException.CODE_BUNDLE_NAME_ERROR, 
                    "The symbolic name or version in the deployment package manifest is " +
                    		"not the same as the symbolic name and version in the bundle " +
                    		"(" + be + ")");
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
        be.setBundleId(b.getBundleId());
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
            	be.setBundleId(b.getBundleId());
            	return b;
            }
        }
        return null;
    }
    
    public void cancel() {
        transaction.cancel();
    }

}
