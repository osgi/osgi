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
import org.osgi.service.condpermadmin.BundleLocationCondition;
import org.osgi.service.condpermadmin.ConditionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.deploymentadmin.*;
import org.osgi.service.deploymentadmin.spi.DeploymentCustomizerPermission;
import org.osgi.service.deploymentadmin.spi.DeploymentSession;
import org.osgi.service.deploymentadmin.spi.ResourceProcessor;
import org.osgi.service.deploymentadmin.spi.ResourceProcessorException;
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
    
    private DeploymentSessionCtx        sessionCtx;
    private DeploymentPackageImpl       srcDp;
    private DeploymentPackageImpl       targetDp;
    private Transaction                 transaction;
    private ServiceTracker              trackRp;
    private TrackerPerm                 trackPerm;
    private TrackerCondPerm             trackCondPerm;
    private TrackerPackageAdmin         trackPackAdmin;
    private boolean                     forced;
    private boolean                     cancelled;
    
    private DeploymentPackageJarInputStream.Entry actEntry;
    private WrappedResourceProcessor              wrProc;
    
    /*
     * Class to track policy admin
     */
    private class TrackerPerm extends ServiceTracker {
        public TrackerPerm() {
            super(DeploymentSessionImpl.this.sessionCtx.getBundleContext(), 
                    "org.osgi.service.permissionadmin.PermissionAdmin", null);
        }
    }
    
    /*
     * Class to track conditiona permission admin
     */
    private class TrackerCondPerm extends ServiceTracker {
        public TrackerCondPerm() {
            super(DeploymentSessionImpl.this.sessionCtx.getBundleContext(), 
                    "org.osgi.service.condpermadmin.ConditionalPermissionAdmin", null);
        }
    }

    /*
     * Class to track package admin
     */
    private class TrackerPackageAdmin extends ServiceTracker {
        public TrackerPackageAdmin() {
            super(DeploymentSessionImpl.this.sessionCtx.getBundleContext(), 
                    "org.osgi.service.packageadmin.PackageAdmin", null);
        }
    }

    DeploymentSessionImpl(DeploymentPackageImpl srcDp, 
                          DeploymentPackageImpl targetDp,
                          DeploymentSessionCtx sessionCtx)
    {
        this.srcDp = srcDp;
        this.targetDp = targetDp;
        this.sessionCtx = sessionCtx;
        trackRp = new ServiceTracker(sessionCtx.getBundleContext(), 
                ResourceProcessor.class.getName(), null);
        trackPerm = new TrackerPerm();
        trackCondPerm = new TrackerCondPerm();
        trackPackAdmin = new TrackerPackageAdmin();
    }

    synchronized boolean isCancelled() {
    	return cancelled;
    }
    
    synchronized void cancel() {
    	wrProc.cancel();
    	cancelled = true;
    }
    
    private Set setFilePermissionForCustomizers() {
        Set cpisForCusts = new HashSet();
        
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
        	if (isCancelled())
            	break;
        	
            BundleEntry be = (BundleEntry) iter.next();
            if (be.isCustomizer())
                setFilePermissionForCustomizer(be, cpisForCusts);
        }
        
        return cpisForCusts;
    }

    private void setFilePermissionForCustomizer(BundleEntry beCust, Set cpisForCusts) {
        final String location = DeploymentAdminImpl.location(beCust.getSymbName(), 
                beCust.getVersion());
        final ConditionalPermissionAdmin cpa = (ConditionalPermissionAdmin) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return (ConditionalPermissionAdmin) trackCondPerm.getService();
            }
        });
        
        final ArrayList permInfos = new ArrayList();
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
            final BundleEntry be = (BundleEntry) iter.next();
            File f = new File(getBundleDir(be), "-"); 
            permInfos.add(new PermissionInfo(FilePermission.class.getName(), f.getAbsolutePath(), 
                    "read, write, execute, delete"));
        }
        
        ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) AccessController.doPrivileged(new PrivilegedAction(){
            public Object run() {
                return cpa.addConditionalPermissionInfo(
                    new ConditionInfo[] {
                            new ConditionInfo(BundleLocationCondition.class.getName(), new String[] {location})
                    },
                    (PermissionInfo[]) permInfos.toArray(new PermissionInfo[] {})
                );
            }});
        cpisForCusts.add(cpi);
    }

    private String getBundleDir(final BundleEntry be) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                BundleUtil bu = new BundleUtil();
                Bundle b = sessionCtx.getBundleContext().getBundle(
                        be.getBundleId());
                File ret = bu.getDataFile(b);
                return ret.getAbsolutePath();
            }
        });
    }

    private void resetFilePermissionForCustomizers(Set cpisForCusts) {
        for (Iterator iter = cpisForCusts.iterator(); iter.hasNext();) {
            final ConditionalPermissionInfo cpi = (ConditionalPermissionInfo) iter.next();
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    cpi.delete();
                    return null;
                }
            });
        }
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
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                trackCondPerm.open();
                trackPerm.open();
                trackRp.open();
                trackPackAdmin.open();
                return null;
            }});
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
            DeploymentCustomizerPermission.PRIVATEAREA);
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
        
        List bel = dp.getBundleEntries();
        for (Iterator iter = bel.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            if (be.getBundleId() == b.getBundleId()) {
                String dir = getBundleDir(be);
                final File f = new File(dir);
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        if (!f.exists())
                            f.mkdir();
                        return null;
                    }
                });
                return f;
            }
        }
        
        throw new SecurityException("Bundle: " + b + " is not part of the deployment " +
        		"package: " + dp);
    }

    void installUpdate(DeploymentPackageJarInputStream wjis) throws DeploymentException {
        openTrackers();
        
        transaction = Transaction.createTransaction(
                "INSTALL " + srcDp.getName(), this, sessionCtx.getLogger());
        transaction.start();
        
        // ConditionalPermissionInfo-s for customizers
        Set cpisForCusts = null;
        
        try {
            stopBundles();
            processBundles(wjis);
            cpisForCusts = setFilePermissionForCustomizers();
            startCustomizers();
            processResources(wjis);
            dropBundles();
            dropResources();
            //refreshPackages();
            startBundles();
        } catch (DeploymentException e) {
            transaction.rollback();
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                    e.getMessage(), e);
        } finally {
        	if (null != cpisForCusts)
        		resetFilePermissionForCustomizers(cpisForCusts);
        }
        if (!isCancelled())
			try {
				transaction.commit();
			} catch (ResourceProcessorException e) {
				throw new DeploymentException(
						DeploymentException.CODE_COMMIT_ERROR, "", e);
			}
		else
        	transaction.rollback();
        
        closeTrackers();
    }
    
    /*private void refreshPackages() {
        final PackageAdmin packAdmin = (PackageAdmin) trackPackAdmin.getService();
        if (null != packAdmin) {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    packAdmin.refreshPackages(null);
                    return null;
                }});
        }
    }*/

    boolean uninstall(boolean forced) throws DeploymentException {
        this.forced = forced;
        boolean succeed = true;
        
        openTrackers();
        
        transaction = Transaction.createTransaction(
                "UNINSTALL " + targetDp.getName(), this, sessionCtx.getLogger());
        transaction.start();
        
        try {
            stopBundles();
            startCustomizers();
            dropBundles();
            dropAllResources();
        } catch (Exception e) {
            if (!forced) {
                transaction.rollback();
                throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                        e.getMessage(), e);
            }  
            sessionCtx.getLogger().log(e);
            succeed = false;
        }
        
        if (!isCancelled())
        	try {
				transaction.commit();
			} catch (ResourceProcessorException e) {
				throw new DeploymentException(
						DeploymentException.CODE_COMMIT_ERROR, "", e);
			}
        else {
        	succeed = false;
        	transaction.rollback();
        }
        
        closeTrackers();
        return succeed;
    }
    
    private int startBundles() {
        if (isCancelled())
        	return 0;

        DeploymentPackageImpl dp = null;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UNINSTALL == getDeploymentAction())
            dp = targetDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        int numOfErrors = 0;
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            if (isCancelled())
            	break;

            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = sessionCtx.getBundleContext().getBundle(entry.getBundleId());
            if (entry.isCustomizer())
                continue;
            try {
                startBundle(b);
            }
            catch (BundleException e) {
                sessionCtx.getLogger().log(e);
                ++numOfErrors;
            }
        }
        
        return numOfErrors;
    }
    
    private void startCustomizers() throws Exception {
        if (isCancelled())
        	return;

        DeploymentPackageImpl dp;
        if (INSTALL == getDeploymentAction())
            dp = srcDp;
        else if (UPDATE == getDeploymentAction())
            dp = srcDp;
        else //DeploymentSession.UNINSTALL == getDeploymentAction()
            dp = targetDp;
        
        for (Iterator iter = dp.getBundleEntries().iterator(); iter.hasNext();) {
            if (isCancelled())
            	break;

            BundleEntry entry = (BundleEntry) iter.next();
            try {
                Bundle b = sessionCtx.getBundleContext().getBundle(entry.getBundleId());
                if (!entry.isCustomizer())
                    continue;
                startBundle(b);
                ServiceReference sref = getRegServ(b);
                String pid = (String) sref.getProperty(Constants.SERVICE_PID);
                entry.setPid(pid);
            } catch (Exception e) {
                if (forced)
                    sessionCtx.getLogger().log(e);
                else
                    throw e;
            }
        }
    }
    
    private ServiceReference getRegServ(final Bundle b) {
        ServiceReference srefs[] = (ServiceReference[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                return b.getRegisteredServices();
            }
        });
        if (null != srefs && srefs.length > 0)
            return srefs[0];
        
        throw new RuntimeException("Internal error");
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
        
        if (isCancelled())
        	return;
        
        List list = targetDp.getBundleEntries();
        for (ListIterator iter = list.listIterator(list.size()); iter.hasPrevious();) {
        	if (isCancelled())
            	break;
        	
            BundleEntry entry = (BundleEntry) iter.previous();
            try {
	            Bundle b = sessionCtx.getBundleContext().getBundle(entry.getBundleId());
	            stopBundle(b);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                sessionCtx.getLogger().log(e, Logger.LOG_WARNING);
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
        while (null != actEntry && !isCancelled()) {
            if (isCancelled())
            	break;

            if (!actEntry.isResource())
                throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR, 
                        "Bundles have to precede resources in the deployment package");
            
            if (!actEntry.isMissing()) {
                processResource(actEntry);
                ResourceEntry re = srcDp.getResourceEntryByName(actEntry.getName());
                re.updateCertificates(actEntry);
            } else
                ; // do nothing
            wjis.closeEntry();
            actEntry = wjis.nextEntry();
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
        if (isCancelled())
        	return;

        List toDrop = new LinkedList(targetDp.getResourceEntries());
        List tmpSet = srcDp.getResourceEntries();
        toDrop.removeAll(tmpSet);
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            if (isCancelled())
            	break;

            try {
                ResourceEntry re = (ResourceEntry) iter.next();
                targetDp.remove(re);
                dropResource(re);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                sessionCtx.getLogger().log(e);
            }
        }
    }
    
    /*
     * Drops all rsources of the target DP
     */
    private void dropAllResources() throws Exception {
        if (isCancelled())
        	return;

        // gathers resource processors that have alerady been called 
        Set procs = new HashSet();

        List toDrop = targetDp.getResourceEntries();
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            if (isCancelled())
            	break;

            try {
	            ResourceEntry re = (ResourceEntry) iter.next();
	            
	            String pid = re.getValue(DAConstants.RP_PID);
	            if (null == pid)
	                continue;
	            
	            ServiceReference rpRef = findProcessor(pid);
	            if (null == rpRef)
	                throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND, 
	                    "Resource processor for pid " + pid + " is not found");
	            
	            wrProc = new WrappedResourceProcessor(rpRef, 
	                fetchAccessControlContext(re.getCertChains()), trackRp);
                        
	            // each processor is called only once
	            if (!procs.contains(pid)) {
	                transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wrProc));
	                wrProc.dropAllResources();
	                procs.add(pid);
	            }
            } catch (Exception e) {
                if (forced)
                    sessionCtx.getLogger().log(e);
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
        if (isCancelled())
        	return;

        // the array contain SymbolicNames and not BundleEntries because
        // the BundleEntry.equals checks name and version but the version 
        // in the source is different from the version in the target.
        ArrayList toDrop = new ArrayList();
        for (Iterator iter = targetDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            toDrop.add(be.getSymbName());
        }

        Set tmpSet = new HashSet();
        for (Iterator iter = srcDp.getBundleEntries().iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            tmpSet.add(be.getSymbName());
        }
        
        toDrop.removeAll(tmpSet);
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            if (isCancelled())
            	break;

            try {	
                String bsn = (String) iter.next();
            	BundleEntry be = targetDp.getBundleEntry(bsn);
            	targetDp.remove(be);
                dropBundle(be);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                sessionCtx.getLogger().log(e);
            }
        }
    }

    /*
     * Drops a particluar resource
     */
    private void dropResource(ResourceEntry re) throws DeploymentException {
        String pid = re.getValue(DAConstants.RP_PID);
        ServiceReference rpRef = findProcessor(pid);
        if (null == rpRef)
            throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND,
                "Resource processor for pid " + pid + "is not found");
        wrProc = new WrappedResourceProcessor(
            rpRef, fetchAccessControlContext(re.getCertChains()), trackRp);
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wrProc));
        try {
			wrProc.dropped(re.getResName());
		} catch (ResourceProcessorException e) {
			if (e.getCode() == ResourceProcessorException.CODE_NO_SUCH_RESOURCE)
				throw new DeploymentException(
						DeploymentException.CODE_NO_SUCH_RESOURCE, "", e);
			throw new DeploymentException(
					DeploymentException.CODE_OTHER_ERROR, "", e);
		}
    }
    
    /*
     * Drops a bundle
     */
    private void dropBundle(BundleEntry be) throws BundleException, DeploymentException {
        Bundle b = sessionCtx.getBundleContext().getBundle(be.getBundleId());
        if (null == b)
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                "Bundle " + b + " was removed directly");
        transaction.addRecord(new TransactionRecord(Transaction.UNINSTALLBUNDLE, 
                b, be, targetDp));
    }

    /*
     * Finds a Resource processor to the given PID
     */
    private ServiceReference findProcessor(String pid) {
        ServiceReference[] refs = (ServiceReference[]) AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return trackRp.getServiceReferences();
                    }
                });
        if (null == refs)
            return null;
        for (int i = 0; i < refs.length; i++) {
            ServiceReference ref = refs[i];
            String s_pid = (String) ref.getProperty(Constants.SERVICE_PID);
            if (pid.equals(s_pid))
                return ref;
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
        Object dp = sessionCtx.getMappedDp(pid);
        if (null != dp && !srcDp.getName().equals(dp))
            throw new DeploymentException(DeploymentException.CODE_FOREIGN_CUSTOMIZER,
                    "PID '" + pid + "' belongs to another DP (" + dp + ")");
            
        ServiceReference rpRef = findProcessor(pid);
        if (null == rpRef)
            throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND, 
                    "Resource processor (PID=" + pid + ") for '" + entry.getName() +
                    "' is not found.");
        wrProc = new WrappedResourceProcessor(rpRef, 
                fetchAccessControlContext(entry.getCertificateChainStringArrays()), trackRp);
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wrProc));
        try {
			wrProc.process(entry.getName(), entry.getInputStream());
        } catch (ResourceProcessorException e) {
			if (e.getCode() == ResourceProcessorException.CODE_NO_SUCH_RESOURCE)
				throw new DeploymentException(
						DeploymentException.CODE_NO_SUCH_RESOURCE, "", e);
			throw new DeploymentException(
					DeploymentException.CODE_OTHER_ERROR, "", e);
		}
        if (INSTALL == getDeploymentAction())
            srcDp.setProcessorPid(entry.getName(), pid);
        else if (UPDATE == getDeploymentAction())
            targetDp.setProcessorPid(entry.getName(), pid);    
    }
    
    private void processBundles(DeploymentPackageJarInputStream wjis) 
    		throws BundleException, IOException, DeploymentException 
    {
    	actEntry = wjis.nextEntry();
        while (null != actEntry && actEntry.isBundle()) 
        {
        	if (isCancelled())
            	break;
        	
            checkManifestEntryPresence(actEntry);
            
            if (!actEntry.isMissing()) {
                Bundle b = processBundle(actEntry);
                srcDp.getBundleEntryByName(actEntry.getName()).
                	setBundleId(b.getBundleId());
            } else {
                BundleEntry beS = srcDp.getBundleEntryByName(actEntry.getName());
                BundleEntry beT = targetDp.getBundleEntryByName(actEntry.getName());
                srcDp.remove(beS);
                srcDp.add(beT);
            }
            wjis.closeEntry();
            actEntry = wjis.nextEntry();
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
        
        // bundle doesn't have BSN the BSN in the DP manifest will be used
        if (null == be.getSymbName())
        	return;
        
        if (!srcDp.contains(be))
            throw new DeploymentException(DeploymentException.CODE_BUNDLE_NAME_ERROR, 
                    "The symbolic name or version in the deployment package manifest is " +
                    		"not the same as the symbolic name and version in the bundle " +
                    		"(" + b + ")");
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
                    Bundle ret = sessionCtx.getBundleContext().installBundle(location, is);
                    return ret;
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
        final Bundle[] bundles = sessionCtx.getBundleContext().getBundles();
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

}
