package org.osgi.impl.service.deploymentadmin;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.Permission;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.deploymentadmin.DeploymentAdmin;
import org.osgi.service.deploymentadmin.DeploymentAdminPermission;
import org.osgi.service.deploymentadmin.DeploymentPackage;
import org.osgi.service.deploymentadmin.ResourceProcessor;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class DeploymentPackageImpl implements DeploymentPackage, Serializable {

    private transient BundleContext 	  context;
    private transient Logger 		      logger; 
    private transient Tracker             tracker;
    private transient DeploymentAdminImpl admin;
    private transient Transaction		  transaction;

    private transient JarInputStream      stream;
    private transient Manifest			  manifest;
    private transient boolean 			  isFixPack;

    private String 			              dpManVer;
    private String 			              dpName;
    private String 			              dpVersion;
    private String 						  customizerBundle;
    private Hashtable                     filters = new Hashtable();
    private HashSet 					  resources = new HashSet();
    // it contains BundleEntries
    private Set 						  bundles  = new HashSet();
    private int 						  id;
    
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentPackageImpl))
            return false;
        DeploymentPackageImpl other = (DeploymentPackageImpl) obj;
        return getName().equals(other.getName()) &&
               getVersion().equals(other.getVersion());
    }
    
    public int hashCode() {
        return (getName() + getVersion()).hashCode();
    }
    
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("{");
        b.append(getName() + " " + getVersion() + " ");
        b.append("filters: " + filters);
        b.append("id: " + id);
        b.append("}");
        return b.toString();
    }

    private class Tracker extends ServiceTracker {
        public Tracker() {
            super(DeploymentPackageImpl.this.context, ResourceProcessor.class
                    .getName(), null);
        }
    }
    
    void startTracker() {
        tracker = new Tracker();
        tracker.open();
    }
    
    void stopTracker() {
        if (null != tracker)
            tracker.close();
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public void setAdmin(DeploymentAdminImpl admin) {
        this.admin = admin;
    }

    
    public boolean fixPack() {
        return isFixPack;
    }
    
    public DeploymentPackageImpl(WrappedJarInputStream in, BundleContext context,
            DeploymentAdminImpl admin) throws Exception {
        this.context = context;
        this.admin = admin;
        this.stream = in;
        this.manifest = stream.getManifest();
        
        dpManVer = manifest.getMainAttributes().getValue("DeploymentPackage-ManifestVersion");
        dpName = manifest.getMainAttributes().getValue("DeploymentPackage-Name");
        dpVersion = manifest.getMainAttributes().getValue("DeploymentPackage-Version");
        isFixPack = (null != manifest.getMainAttributes().getValue("DeploymentPackage-FixPack"));
        customizerBundle = manifest.getMainAttributes().getValue("DeploymentPackage-ProcessorBundle");
        
        initLogger();
        
        startTracker();
    }
    
    private void initLogger() {
        ServiceReference ref = context.getServiceReference(LogService.class.getName());
        if (null != ref)
            logger = new Logger((LogService) context.getService(ref));
        else
            logger = new Logger();
    }
    
    protected void finalize() throws Throwable {
        stopTracker();
    }

    private Bundle installBundle(BundleEntry bundleEntry, JarInputStream jis)
            throws BundleException 
    {
        Bundle b = context.installBundle(bundleEntry.location, jis);
        BundleEntry be = new BundleEntry(bundleEntry);
        be.id = new Long(b.getBundleId());
        bundles.add(be);
        transaction.addRecord(new TransactionRecord(
                Transaction.INSTALLBUNDLE, new Object[] {b, bundles, be}));
        return b;
    }

    private void uninstallBundles(Set set) throws BundleException {
        for (Iterator iter = set.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            Bundle b = context.getBundle(be.id.longValue());
            
            // to avoid concurrent modification exception
            if (set == bundles)
                iter.remove(); 
            else 
                bundles.remove(be);
            
            transaction.addRecord(new TransactionRecord(
                    Transaction.UNINSTALLBUNDLE, new Object[] {b, bundles, be}));
        }
     }

    private Bundle updateBundle(BundleEntry bundleEntry, JarInputStream jis)
    		throws BundleException 
    {
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            Bundle b = bundles[i];
            if (b.getLocation().equals(bundleEntry.location)) {
                b.update(jis);
                transaction.addRecord(new TransactionRecord(
                        Transaction.UPDATEBUNDLE, new Object[] {b}));
                return b;
            }
        }
        return null;
    }
    
    private void startBundle(Bundle b) throws BundleException {
        b.start();
        transaction.addRecord(new TransactionRecord(
                Transaction.STARTBUNDLE, new Object[] {b}));
    }

    private void startBundles() throws BundleException {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.id.longValue());
            startBundle(b);
        }
    }
    
    private void stopAllBundles() throws BundleException {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry bentry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(bentry.id.longValue());
            b.stop();
            transaction.addRecord(new TransactionRecord(
                    Transaction.STOPBUNDLE, new Object[] {b}));
        }
    }
    
    private void stopNonCustomizerBundles() throws BundleException {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry bentry = (BundleEntry) iter.next();
            if (!isCustomizerBundle(bentry)) {
	            Bundle b = context.getBundle(bentry.id.longValue());
	            b.stop();
	            transaction.addRecord(new TransactionRecord(
	                    Transaction.STOPBUNDLE, new Object[] {b}));
            }
        }
    }
    
    // called from the Transaction.commit because RP in the processor 
    // bundle is needed until ResourceProcessor.complete
    void stopCustomizerBundle() throws BundleException {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry bentry = (BundleEntry) iter.next();
            if (isCustomizerBundle(bentry)) {
                Bundle b = context.getBundle(bentry.id.longValue());
                b.stop();
                transaction.addRecord(new TransactionRecord(
                        Transaction.STOPBUNDLE, new Object[] {b}));
            }
        }
    }

    private BundleEntry getBundleEntry(JarEntry entry) throws IOException {
        String symbName = (String) entry.getAttributes().getValue(
                "Bundle-SymbolicName");
        String version = (String) entry.getAttributes().getValue(
                "Bundle-Version");
        if (null == symbName || version == null)
            return null;
        String location = admin.location(symbName, version);
        return new BundleEntry(location, symbName, version, -1);
    }
    
    private boolean isBundle(JarEntry entry) throws IOException {
        String symbName = (String) entry.getAttributes().getValue(
        		"Bundle-SymbolicName");
        String version = (String) entry.getAttributes().getValue(
        		"Bundle-Version");
        return null != symbName && null != version;
    }
    
    private boolean isResource(JarEntry entry) throws IOException {
        return !isBundle(entry);
    }
    
    private boolean isMissingBundle(JarEntry entry) throws Exception {
        String str = (String) entry.getAttributes().getValue(
        		"MissingResource-Bundle");
        if (null != str && !fixPack())
            throw new Exception("Only fix-pack is allowed to contian " +
            		"MissingResource-Bundle manifest header");
        return null != str;
    }
    
    private boolean isMissingResource(JarEntry entry) throws Exception {
        String str = (String) entry.getAttributes().getValue(
				"MissingResource-Resource");
        if (null != str && !fixPack())
            throw new Exception("Only fix-pack is allowed to contian " +
            		"MissingResource-Resource manifest header");
        return null != str;
    }
    
    private boolean isCustomizerBundle(BundleEntry bentry) {
        if (bentry.symbName.equals(customizerBundle))
        	return true;
        else
            return false;
    }

    private void extractFilters(String str) throws InvalidSyntaxException {
        if (null == str)
            return;
        String[] filters = Splitter.split(str, ',', 0);
        for (int i = 0; i < filters.length; i++) {
            String[] filter = Splitter.split(filters[i], ';', 0);
            String procFilter = filter[0].substring(filter[0].indexOf("=") + 1);
            String resFilter = filter[1].substring(filter[1].indexOf("=") + 1)
                    .trim();

            if (!isBundleFilter(procFilter)) {
                // check whether the filter is correct
                context.createFilter(procFilter);
            }
            
            addFilter(resFilter, procFilter);
        }
    }
    
    // TODO is it needed?
    private boolean isBundleFilter(String filter) {
        return filter.startsWith("(bundle");
    }

    public long getId() {
        return id;
    }
    
    void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return dpName;
    }

    public String getVersion() {
        return dpVersion;
    }

    public Bundle[] listBundles() {
        Vector ret = new Vector(bundles.size());
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry bentry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(bentry.id.longValue());
            ret.add(b);
        }
        return (Bundle[]) ret.toArray(new Bundle[] {});
    }

    public boolean isNew(Bundle b) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isUpdated(Bundle b) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPendingRemoval(Bundle b) {
        // TODO Auto-generated method stub
        return false;
    }

    public File getDataFile(Bundle bundle) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDeploymentAdmin(DeploymentAdminImpl deploymentAdmin) {
        this.admin = deploymentAdmin;
    }

    private void transactionStart() {
        transaction = new Transaction(this, logger);
    }
    
    private void transactionCommit() {
        transaction.commit();
    }
    
    private void transactionRollback() {
        transaction.rollback();
    }

    private void transactionStep(int tranCode, Object[] params) {
        transaction.addRecord(new TransactionRecord(tranCode, params));
    }
    
    private void processResource(ServiceReference procRef, String resName,
            int operation) throws Exception 
    {
        ResourceProcessor proc = (ResourceProcessor)
        		tracker.getService(procRef);
        transactionStep(Transaction.PROCESSOR, new Object[] {proc, this,
                new Integer(operation)});
        proc.process(resName, stream);
    }
    
    private void dropResources(Set diffResources) throws Exception {
        for (Iterator iter = diffResources.iterator(); iter.hasNext();) {
            String resFilter = (String) iter.next();
            ResourceProcessor proc = (ResourceProcessor) tracker.
            		getService(findProcessor(resFilter, filters));
            transactionStep(Transaction.PROCESSOR, new Object[] {proc, this,
                    new Integer(ResourceProcessor.UPDATE)});
            proc.dropped(resFilter);
            removeFilter(resFilter);
        }
    }
    
    private void addFilter(String resFilter, String procFilter) {
        Hashtable old = new Hashtable(filters);
        filters.put(resFilter, procFilter);
        transaction.addRecord(new TransactionRecord(Transaction.FILTERS, 
                new Object[] {filters, old}));
    }
    
    private void removeFilter(String resFilter) {
        Hashtable old = new Hashtable(filters);
        filters.remove(resFilter);
        transaction.addRecord(new TransactionRecord(Transaction.FILTERS, 
                new Object[] {filters, old}));
    }
    
    private void addResource(String resName) {
        Set old = new HashSet(resources);
        resources.add(resName);
        transaction.addRecord(new TransactionRecord(Transaction.RESOURCES, 
                new Object[] {resources, old}));
    }
    
    private void removeResource(Iterator iter) {
        Set old = new HashSet(resources);
        iter.remove();
        transaction.addRecord(new TransactionRecord(Transaction.RESOURCES, 
                new Object[] {resources, old}));
    }

    // DeploymentPackage interface impl.
    public void install() throws Exception {
        transactionStart();
        extractFilters(manifest.getMainAttributes().getValue(
        		"DeploymentPackage-Processing"));
        try {
            JarEntry entry = stream.getNextJarEntry();
            while (null != entry) {
                if (isBundle(entry)) {
                    BundleEntry bentry = getBundleEntry(entry);
                    // TODO chech whether there is a bundle with the same name and version
                    // in the framework
                    // TODO chech whethet there is a bundle with the same name in the DP
                    Bundle b = installBundle(bentry, stream);
                    if (isCustomizerBundle(bentry))
                        startBundle(b);
                } else if (isResource(entry)){
                    String resName = entry.getName();
                    ServiceReference procRef = findProcessor(resName, filters);
                    if (null != procRef) {
                        processResource(procRef, resName, ResourceProcessor.INSTALL);
                        addResource(resName);
                    }
                    else {
                        // it is catched by the catch clause
                        throw new Exception("There is no resource processor for " +
                        		"resource " + resName);
                    }
                } else {
                    // TODO error
                }
                stream.closeEntry();
                entry = stream.getNextJarEntry();
            }
            startBundles();
        } catch (Exception e) {
            transactionRollback();
            throw e;
        }

        transactionCommit();
    }

    // TODO throw exceptions
    public void uninstall() {
        checkPermission(getName(), "uninstall");
        
        transactionStart();
        try {
            stopNonCustomizerBundles();
            for (Iterator iter = resources.iterator(); iter.hasNext();) {
                String resName = (String) iter.next();
                ServiceReference procRef = findProcessor(resName, filters);
                if (null != procRef) {
                    processResource(procRef, resName, ResourceProcessor.UNINSTALL);
                    removeResource(iter);
                } else {
                    // it is catched by the catch clause
                    throw new Exception("There is no resource processor for " +
                    		"resource " + resName);
                }
            }
            uninstallBundles(bundles);
        } catch (Exception e) {
            logger.log(e);
            transactionRollback();
            // TODO throw e;
        }

        transactionCommit();
        admin.onUninstallDp(this);
    }

    // TODO restart customizer first
    public void update(WrappedJarInputStream in) throws Exception {
        this.stream = in;
        this.manifest = in.getManifest();
       
        transactionStart();
        
        Hashtable oldFilters = new Hashtable(filters);
        extractFilters(manifest.getMainAttributes().getValue(
        		"DeploymentPackage-Processing"));
        
        try {
            stopAllBundles();
            JarEntry entry = stream.getNextJarEntry();
            Set bundlesNotToUninstall = new HashSet();
            Set resourcesNotToDrop = new HashSet();
            while (null != entry) {
                if (isMissingBundle(entry)) {
                    BundleEntry bentry = getBundleEntry(entry);
                    bundlesNotToUninstall.add(bentry);
                } else if (isBundle(entry)) {
                    BundleEntry bentry = getBundleEntry(entry);
                    if (bundles.contains(bentry)) {
                        updateBundle(bentry, stream);
                    } else {
                        // TODO chech whethet there is a bundle with the same name and version
                        // in the framework
                        // TODO chech whethet there is a bundle with the same name in the DP
                        installBundle(bentry, stream);
                    }
                    bundlesNotToUninstall.add(bentry);
                } else if (isMissingResource(entry)) {
                    String resName = entry.getName();
                    resourcesNotToDrop.add(resName);
                } else if (isResource(entry)) {
                    String resName = entry.getName();
                    ServiceReference procRefOld = findProcessor(resName, oldFilters);
                    ServiceReference procRefNew = findProcessor(resName, filters);
	                if (null != procRefOld) {
	                    processResource(procRefOld, resName, ResourceProcessor.UPDATE);
	                    resourcesNotToDrop.add(resName);
	                } else if (null != procRefNew) {
	                    processResource(procRefNew, resName, ResourceProcessor.UPDATE);
	                    resourcesNotToDrop.add(resName);
	                } else {
	                    // it is catched by the catch clause
	                    throw new Exception("There is no resource processor for " +
	                    		"resource " + resName);
	                }
                } else {
                    // TODO error
                }

                stream.closeEntry();
                entry = stream.getNextJarEntry();
            }
            
            Set bundlesToUninstall = new HashSet(bundles);
            bundlesToUninstall.removeAll(bundlesNotToUninstall);
            uninstallBundles(bundlesToUninstall);

            Set resourcesToDrop = new HashSet(resources);
            resourcesToDrop.removeAll(resourcesNotToDrop);
            dropResources(resourcesToDrop);
            
            DeploymentPackageVerifier.verify(this);
            startBundles();
        } catch (Exception e) {
            transactionRollback();
            throw e;
        }
        transactionCommit();
    }

    private ServiceReference findProcessor(String resName, Hashtable filters) {
        // finds the appropriate process filter
        Filter filter = null;
        for (Iterator iter = filters.keySet().iterator(); iter.hasNext();) {
            String resFilter = (String) iter.next();
            if (Matcher.match(resFilter, resName)) {
                String s = (String) filters.get(resFilter);
                if (isBundleFilter(s)) {
                    return findProcessorInBundles(s);
                }
                try {
                    filter = context.createFilter(s);
                }
                catch (InvalidSyntaxException e) {
                    // extractFilters method has allready made the check
                    throw new RuntimeException("Internal error" + e);
                }
            }
        }
        
        // no matching resource filter
        if (null == filter)
            return null;
        
        return getServiceReferences(filter);
    }
    
    private ServiceReference findProcessorInBundles(String filter) {
        String symbName = filter.substring(filter.indexOf('=') + 1, filter.length() - 1);
        ServiceReference[] refs = tracker.getServiceReferences();
        for (int i = 0; i < refs.length; i++) {
            String s = (String) refs[i].getBundle().getHeaders().
            		get("Bundle-SymbolicName");
            if (s.equals(symbName))
                return refs[i];
        }
        return null;
    }
    
    private ServiceReference getServiceReferences(Filter filter) {
        ServiceReference[] refs = tracker.getServiceReferences();
        if (null == refs)
            return null;
        ServiceReference ret = null;
       
        for (int i = 0; i < refs.length; i++) {
            Dictionary dict = getDictionary(refs[i]);
            if (filter.match(dict)) {
                if (null != ret) {
                    // TODO there are more than one matching processors
                }
                ret = refs[i];
            }
        }
        return ret;
    }

    private Dictionary getDictionary(ServiceReference reference) {
        String[] keys = reference.getPropertyKeys();
        Dictionary dict = new Hashtable();
        for (int i = 0; i < keys.length; i++) {
            Object value = reference.getProperty(keys[i]);
            dict.put(keys[i], value);
        }
        return dict;
    }
    
    private void checkPermission(String filter, String action) {
	    SecurityManager sm = System.getSecurityManager();
	    if (null == sm)
	        return;
	    Permission perm = new DeploymentAdminPermission(filter, action);
	    sm.checkPermission(perm);
	}
   
}