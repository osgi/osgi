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
//import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.permissionadmin.*;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentSessionImpl implements DeploymentSession, FrameworkListener {

	static final int INSTALL   = 0;
    static final int UPDATE    = 1;
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
    
    // for refreshing packages
    private Semaphore					semPckgRefreshed;
    
    private DeploymentPackageJarInputStream.Entry actEntry;
    private WrappedResourceProcessor              actWrProc;
    
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
        
        registerFwListener();
    }

    private void registerFwListener() {
    	AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				sessionCtx.getBundleContext().
					addFrameworkListener(DeploymentSessionImpl.this);
				return null;
			}
    	});
	}

	synchronized boolean isCancelled() {
    	return cancelled;
    }
    
    synchronized void cancel() {
    	if (null != actWrProc)
	    	actWrProc.cancel();
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
                BackDoor bu = new BackDoor(sessionCtx.getBundleContext());
                Bundle b = sessionCtx.getBundleContext().getBundle(
                        be.getBundleId());
                File ret = bu.getDataFile(b);
                bu.destroy();
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
    	if (srcDp.isEmpty())
    		return UNINSTALL;
       	if (targetDp.isEmpty())
            return INSTALL;
        return UPDATE;
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
        
        WrappedResourceProcessor.setRpTracker(trackRp);
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
        
        transaction = Transaction.createTransaction("INSTALL/UPDATE " + srcDp.getName(), 
        	sessionCtx.getBundleContext(), this, sessionCtx.getLogger());
        transaction.start();
        
        // ConditionalPermissionInfo-s for customizers
        Set cpisForCusts = null;
        try {
        	// "true" since customizers also have to be stopped because they 
        	// may be updated during processBundles call
            stopBundles(true);
            Vector wrProcs = processResources(
            		wjis, //stream
            		true);//processL10Ns
            processBundles(wjis);
            cpisForCusts = setFilePermissionForCustomizers();
            startCustomizers();
            wrProcs.addAll(processResources(
            		wjis,   //stream
            		false));//processL10Ns
            Vector wrProcsD = dropResources();
            wrProcs.addAll(wrProcsD);
            prepareProcessors(wrProcs);
            commitProcessors(wrProcs);
            // TODO refreshPackages();
            dropBundles();
            startBundles();
            //refreshPackages();
        } catch (ResourceProcessorException e) {
			if (e.getCode() == ResourceProcessorException.CODE_PREPARE) {
				transaction.rollback();
				throw new DeploymentException(DeploymentException.CODE_COMMIT_ERROR);
			}
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
        if (isCancelled())
			transaction.rollback();
        
        closeTrackers();
    }
    
    /*private void refreshPackages() {
        final PackageAdmin packAdmin = (PackageAdmin) trackPackAdmin.getService();
        if (null != packAdmin) {
            semPckgRefreshed = new Semaphore();
            //semPckgRefreshed.hold();
        	AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    packAdmin.refreshPackages(null);
                    return null;
                }});
        	//semPckgRefreshed.hold();
        }
    }*/

    boolean uninstall(boolean forced) throws DeploymentException {
        this.forced = forced;
        boolean succeed = true;
        openTrackers();
        
        transaction = Transaction.createTransaction("UNINSTALL " + targetDp.getName(), 
        	sessionCtx.getBundleContext(), this, sessionCtx.getLogger());
        transaction.start();
        
        // "false" since customizers will not be updated because it is an uinstall
        stopBundles(false);
        try {
            Vector wrProcs = dropAllResources();
            prepareProcessors(wrProcs);
            commitProcessors(wrProcs);
            dropBundles();
        } catch (Exception e) {
            if (!forced) {
                transaction.rollback();
                if (e instanceof ResourceProcessorException &&
                	((ResourceProcessorException) e).getCode() == ResourceProcessorException.CODE_PREPARE)
		                throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
		                        e.getMessage(), e);
                throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, 
                		e.getMessage(), e);
            }  
            sessionCtx.getLogger().log(e);
            succeed = false;
        }
        
        if (isCancelled()) {
        	succeed = false;
        	transaction.rollback();
        }
        
        closeTrackers();
        return succeed;
    }
    
    private void prepareProcessors(Vector wrProcs) throws ResourceProcessorException {
    	for (ListIterator iter = wrProcs.listIterator(wrProcs.size()); iter.hasPrevious();) {
			WrappedResourceProcessor wrProc = 
				(WrappedResourceProcessor) iter.previous();
			wrProc.prepare();
		}
	}

    private void commitProcessors(Vector wrProcs) {
    	try {
	    	for (ListIterator iter = wrProcs.listIterator(wrProcs.size()); iter.hasPrevious();) {
	    		WrappedResourceProcessor wrProc = 
	    			(WrappedResourceProcessor) iter.previous();
	    		wrProc.commit();
	    	}
    	} catch (Exception e) {
			sessionCtx.getLogger().log(e);
		}
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

            BundleEntry be = (BundleEntry) iter.next();
            try {
                Bundle b = sessionCtx.getBundleContext().getBundle(be.getBundleId());
                if (!be.isCustomizer())
                    continue;
                startBundle(b);
                ServiceReference sref = getRegServ(b);
                String pid = (String) sref.getProperty(Constants.SERVICE_PID);
                be.setPid(pid);
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

    private void stopBundles(boolean stopCustomizers) {
        if (INSTALL == getDeploymentAction())
            return;
        
        if (isCancelled())
        	return;
        
        List list = targetDp.getBundleEntries();
        for (ListIterator iter = list.listIterator(list.size()); iter.hasPrevious();) {
        	if (isCancelled())
            	break;
            BundleEntry be = (BundleEntry) iter.previous();
            if (!stopCustomizers && be.isCustomizer())
            	continue;
            try {
	            Bundle b = sessionCtx.getBundleContext().getBundle(be.getBundleId());
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

    private Vector processResources(DeploymentPackageJarInputStream wjis, boolean processL10Ns) 
    		throws DeploymentException, IOException 
    {
    	Vector wrProcs = new Vector();
    	if (processL10Ns) {
    		actEntry = wjis.nextEntry(); //the localization files are first
    		if ((null == actEntry) || !actEntry.isL10N())
    			return wrProcs;
    	}
        while (null != actEntry && !isCancelled()) {
            if (isCancelled())
            	break;

            if (!processL10Ns && !actEntry.isResource())
                throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR, 
                        "Bundles and localization files have to precede resources in the deployment package!");
            
            if (!actEntry.isMissing()) {
                WrappedResourceProcessor wrProc = processResource(actEntry);
                if (null != wrProc)
                	wrProcs.add(wrProc);
                ResourceEntry re = srcDp.getResourceEntryByName(actEntry.getName());
                re.updateCertificates(actEntry);
            } else {
            	// ResourceEntry must be updated
            	ResourceEntry reT = targetDp.getResourceEntryByName(actEntry.getName());
            	ResourceEntry reS = srcDp.getResourceEntryByName(actEntry.getName());
            	reS.update(reT);
            }
            wjis.closeEntry();
            actEntry = wjis.nextEntry();
            if ((actEntry == null) || (processL10Ns && !actEntry.isL10N()))
            	break; //go ahead, no more localization files
        }
        return wrProcs;
    }

    /*
     * Drops resources that don't present in the source package and are not 
     * marked as missing resources
     */
    private Vector dropResources() {
    	Vector wrProcs = new Vector();
    	
        if (isCancelled())
        	return wrProcs;

        List toDrop = new LinkedList(targetDp.getResourceEntries());
        List tmpSet = srcDp.getResourceEntries();
        toDrop.removeAll(tmpSet);
        for (ListIterator iter = toDrop.listIterator(toDrop.size()); iter.hasPrevious();) {
            if (isCancelled())
            	break;

            try {
                ResourceEntry re = (ResourceEntry) iter.previous();
                targetDp.remove(re);
                
                String pid = re.getValue(DAConstants.RP_PID);
                ResourceProcessor proc = WrappedResourceProcessor.findProcessorSilent(pid);
                if (null == proc)
                    throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND,
                        "Resource processor for pid " + pid + "is not found");
                actWrProc = new WrappedResourceProcessor(
                    pid, fetchAccessControlContext(re.getCertChains()), trackRp);
                
                dropResource(re);
            } catch (Exception e) {
                // Exceptions are ignored in this phase to allow repairs 
                // to always succeed, even if the existing package is corrupted.
                sessionCtx.getLogger().log(e);
            } finally {
            	wrProcs.add(actWrProc);
            }
        }
        return wrProcs;
    }
    
    /*
     * Drops all rsources of the target DP
     */
    private Vector dropAllResources() throws Exception {
        // ensures that only one entry per RP is in the return set 
        Set pids = new HashSet();
        
        // this is the returned set
        Vector wrProcs = new Vector();
        
        if (isCancelled())
        	return wrProcs;
        
        List toDrop = targetDp.getResourceEntries();
        for (Iterator iter = toDrop.iterator(); iter.hasNext();) {
            if (isCancelled())
            	break;

            try {
	            ResourceEntry re = (ResourceEntry) iter.next();
	            String pid = re.getValue(DAConstants.RP_PID);
	            
	            if (null == WrappedResourceProcessor.findProcessorSilent(pid)) {
	            	if (isIconResource(targetDp, re.getResName())) {
	            		//if the resource is the icon we should skip its processing
	            		continue;
	            	}
	            	throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND, 
		                "Resource processor for pid " + pid + " is not found");
	            }
	            
	            // each processor is called only once
	            if (!pids.contains(pid)) {
	            	WrappedResourceProcessor wrProc = new WrappedResourceProcessor(pid, 
	            			fetchAccessControlContext(re.getCertChains()), trackRp);
	                transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, wrProc));
	                wrProc.dropAllResources();
	                pids.add(pid);
	                wrProcs.add(wrProc);
	            }
            } catch (Exception e) {
                if (forced)
                    sessionCtx.getLogger().log(e);
                else
                    throw e;
            }
        }
        return wrProcs;
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
        for (ListIterator iter = toDrop.listIterator(toDrop.size()); iter.hasPrevious();) {
            if (isCancelled())
            	break;

            try {	
                String bsn = (String) iter.previous();
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
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, actWrProc));
        try {
			actWrProc.dropped(re.getResName());
		} catch (ResourceProcessorException e) {
			throw new DeploymentException(
					DeploymentException.CODE_OTHER_ERROR, "", e);
		}
    }
    
    /*
     * Drops a bundle
     */
    private void dropBundle(BundleEntry be) throws BundleException, DeploymentException {
        final Bundle b = sessionCtx.getBundleContext().getBundle(be.getBundleId());
        if (null == b)
            throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR,
                "Bundle " + b + " was removed directly");
        
        ByteArrayOutputStream bos = saveBundle(b);
		try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws BundleException {
                    b.uninstall();
                    return null;
                }});
        } catch (PrivilegedActionException e) {
        	if (e.getException() instanceof BundleException)
        		throw (BundleException) e.getException();
        }
        targetDp.remove(be);
        
        transaction.addRecord(new TransactionRecord(Transaction.UNINSTALLBUNDLE, 
                be, sessionCtx.getBundleContext(), targetDp, 
                new ByteArrayInputStream(bos.toByteArray())));
    }

    private ByteArrayOutputStream saveBundle(final Bundle b) throws DeploymentException {
    	final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws BundleException, DeploymentException {
                	BackDoor bu = new BackDoor(sessionCtx.getBundleContext());
			        InputStream is = bu.getBundleStream(b);
			        bu.destroy();
			        try {
			            byte[] data = new byte[0x1000];
			            int i = is.read(data);
			            while (-1 != i) {
			                bos.write(data, 0, i);
			                i = is.read(data);
			            }
			        } catch (IOException e) {
						throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, "", e);
					} finally {
			        	try {
							if (null != bos)
								bos.close();
							if (null != is)
								is.close();
						} catch (IOException e) {
							throw new DeploymentException(DeploymentException.CODE_OTHER_ERROR, "", e);
						}
			        }
                    return null;
                }});
        } catch (PrivilegedActionException e) {
       		throw (DeploymentException) e.getException();
        }
        return bos;
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

    private WrappedResourceProcessor processResource(final DeploymentPackageJarInputStream.Entry entry) 
    		throws DeploymentException, IOException 
    {
        String pid = entry.getAttributes().getValue(DAConstants.RP_PID);
        
        if (null == pid)
            return null;
        
        Object dp = sessionCtx.getMappedDp(pid);
        if (null != dp && !srcDp.getName().equals(dp))
            throw new DeploymentException(DeploymentException.CODE_FOREIGN_CUSTOMIZER,
                    "PID '" + pid + "' belongs to another DP (" + dp + ")");
            
        ResourceProcessor proc = WrappedResourceProcessor.findProcessorSilent(pid);
        boolean isIcon = isIconResource(srcDp, entry.getName());
        if (null == proc && !isIcon)
            throw new DeploymentException(DeploymentException.CODE_PROCESSOR_NOT_FOUND, 
                    "Resource processor (PID=" + pid + ") for '" + entry.getName() +
                    "' is not found.");
        
        byte[] cachedResourceData = null;
        if (isIcon) {
        	ByteArrayOutputStream bos = readIntoBuffer(entry.getInputStream());
        	//we should cache the icon resource data because it might be processed lated if there is a resource processor for the icon resource
        	cachedResourceData = bos.toByteArray();
        	// store the icon in a local storage
        	String iconFile = DeploymentPackageImpl.storeIcon(entry.getName(), srcDp, new ByteArrayInputStream(cachedResourceData));
        	srcDp.setIcon(iconFile);
        	if (proc == null) {
        		//there is no resource processor - returning
        		return null; 
        	}
        }
        actWrProc = new WrappedResourceProcessor(pid, 
                fetchAccessControlContext(entry.getCertificateChainStringArrays()), trackRp);
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, actWrProc));
        try {
			actWrProc.process(entry.getName(), cachedResourceData != null? new ByteArrayInputStream(cachedResourceData) : entry.getInputStream());
        } catch (ResourceProcessorException e) {
			if (e.getCode() == ResourceProcessorException.CODE_RESOURCE_SHARING_VIOLATION)
				throw new DeploymentException(
						DeploymentException.CODE_RESOURCE_SHARING_VIOLATION, "", e);
			throw new DeploymentException(
					DeploymentException.CODE_OTHER_ERROR, "", e);
		}
        
        return actWrProc;
    }
    
    private ByteArrayOutputStream readIntoBuffer(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[0x1000];
            int i = is.read(data);
            while (-1 != i) {
                bos.write(data, 0, i);
                i = is.read(data);
            }
        } finally {
            if (null != bos)
                bos.close();
        }
        return bos;
    }

    private void processBundles(DeploymentPackageJarInputStream wjis) 
    		throws BundleException, IOException, DeploymentException 
    {
    	int counter = 0;
        while (null != actEntry && actEntry.isBundle()) 
        {
        	if (isCancelled())
            	break;
        	
            checkManifestEntryPresence(actEntry);
            
            if (!actEntry.isMissing()) {
                final Bundle b = processBundle(actEntry);
                if (null != b) {
	                BundleEntry be = srcDp.getBundleEntryByName(actEntry.getName());
	                srcDp.setBundleEntryPossition(be, counter++);
	                be.setBundleId(b.getBundleId());
	                String location = (String) AccessController.doPrivileged(new PrivilegedAction() {
						public Object run() {
							return b.getLocation();
						}});
	                be.setLocation(location);
                }
            } else {
            	// BundleEntry must be updated
            	BundleEntry beT = targetDp.getBundleEntryByName(actEntry.getName());
            	BundleEntry beS = srcDp.getBundleEntryByName(actEntry.getName());
            	beS.update(beT);
            }
            wjis.closeEntry();
            actEntry = wjis.nextEntry();
        }
    }
    
    /**
     * Checks whether the specified resource entry is an icon as specified by the manifest icon header
     * */
    private boolean isIconResource(DeploymentPackageImpl dp, String entryName) {
    	String iconHeader = dp.getHeader(DAConstants.DP_ICON);
    	if (iconHeader != null) {
    		if (iconHeader.equals(entryName) || iconHeader.equals("/"+entryName)) {
    			return true;
    		}
    	}
    	return false;
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
        BundleEntry srcBe = new BundleEntry(entry);
        BundleEntry targetBe = getTargetBundleEntry(srcBe);  
        Bundle b;
        if (null != targetBe) {
            b = updateBundle(targetBe, entry.getInputStream());
        } else {
            b = installBundle(srcBe, entry.getInputStream());
        }
        if (null != b)
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
        } catch (PrivilegedActionException e) {
            throw (BundleException) e.getException();
        }
		transaction.addRecord(new TransactionRecord(Transaction.INSTALLBUNDLE, b));
		return b;
    }
    
    private Bundle updateBundle(final BundleEntry be, final InputStream is)
			throws BundleException, DeploymentException 
    {
    	final Bundle b = sessionCtx.getBundleContext().
    		getBundle(be.getBundleId());
    	if (null == b) {
    		// TODO is it OK?
    		Bundle ret = installBundle(be, is);
    		sessionCtx.getLogger().log(Logger.LOG_WARNING, 
				"The target bundle " + be + " was not found during the update. " +
				"It was installed instead of updated.");
    		return ret;
    	}
    	
    	ByteArrayOutputStream bos = saveBundle(b);
       	try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws BundleException {
					b.update(is);
					return null;
				}
			});
        } catch (PrivilegedActionException e) {
            throw (BundleException) e.getException();
        }
        
        transaction.addRecord(new TransactionRecord(Transaction.UPDATEBUNDLE, 
            b, new ByteArrayInputStream(bos.toByteArray())));

        return b;
    }
    
    private BundleEntry getTargetBundleEntry(BundleEntry srcBe) {
    	return targetDp.getBundleEntry(srcBe.getSymbName());
    }

	public void frameworkEvent(FrameworkEvent event) {
		if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
			semPckgRefreshed.release();
		}
	}
	
}
