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

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.deploymentadmin.DeploymentException;
import org.osgi.service.deploymentadmin.DeploymentPackage;

public class DeploymentPackageImpl implements DeploymentPackage, Serializable {

    private transient BundleContext 	  context;
    private transient Logger 		      logger; 
    
    // TODO create a VersionRange class
    private String  fixPackRange;	
    private String  dpName;
    private String  dpVersion;
    private Integer id;
    
    private Map mainSection = new Hashtable();
    private Vector bundleEntries = new Vector();
    private Vector resourceEntries = new Vector();
    
    /*
     * to create a non-empty DP
     */ 
    public DeploymentPackageImpl(Manifest manifest, int id) throws DeploymentException {
        this.id = new Integer(id);
        
        processMainSection(manifest);
        processNameSections(manifest);
    }

    /*
     * to create an empty DP
     */
    public DeploymentPackageImpl() {
    }
    
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentPackage))
            return false;
        DeploymentPackage other = (DeploymentPackage) obj;
        return getName().equals(other.getName()) &&
               getVersion().equals(other.getVersion());
    }
    
    public int hashCode() {
        return (getName() + getVersion()).hashCode();
    }
    
    public String toString() {
        return "{" + getName() + " " + getVersion() + "}";
    }

    private void processMainSection(Manifest manifest) throws DeploymentException {
        dpName = manifest.getMainAttributes().getValue(DAConstants.DP_NAME);
        fixPackRange = manifest.getMainAttributes().getValue(DAConstants.DP_FIXPACK);
        dpVersion = manifest.getMainAttributes().getValue(DAConstants.DP_VERSION);
        
        Attributes attrs = manifest.getMainAttributes();
        for (Iterator iter = attrs.keySet().iterator(); iter.hasNext();) {
            Attributes.Name key = (Attributes.Name) iter.next();
            Object value = attrs.getValue(key);
            mainSection.put(key.toString(), value);
        }
        
        checkMainSection();
    }
    
    private void checkMainSection() throws DeploymentException {
        if (null == dpName)
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                    "Missing header: " + DAConstants.DP_NAME);
     
        if (null == dpVersion)
            throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER, 
                    "Missing header: " + DAConstants.DP_VERSION);
        
        // TODO check fixpack range
    }

    private void processNameSections(Manifest manifest) throws DeploymentException {
        Map entries = manifest.getEntries();
        for (Iterator iter = entries.keySet().iterator(); iter.hasNext();) {
            String resPath = (String) iter.next();
            Attributes attrs = (Attributes) entries.get(resPath);
            String bSn = (String) attrs.getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
            String bVer = (String) attrs.getValue(DAConstants.BUNDLE_VERSION);
            String bCustStr = (String) attrs.getValue(DAConstants.CUSTOMIZER);
            boolean isBundle = null != bSn && null != bVer; 
            boolean bCust = (bCustStr == null ? false : Boolean.valueOf(bCustStr).booleanValue());
            if (isBundle) {
                // bundle
                BundleEntry be = new BundleEntry(bSn, bVer, bCust);
                bundleEntries.add(be);
            } else {
                // resource
                resourceEntries.add(new ResourceEntry(resPath, attrs));
            }
            
            checkNameSection(resPath, attrs, isBundle);
        }
    }
    
    private void checkNameSection(String resPath, Attributes attrs, boolean isBundle) 
    		throws DeploymentException 
    {
        if (fixPack()) {
            String missing = attrs.getValue(DAConstants.MISSING);
            if (null == missing)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing \"" + DAConstants.MISSING + "\" header in \"" +
                        resPath + "\" section");
        }
        
        if (!isBundle) {
            String rp = attrs.getValue(DAConstants.RP_PID);
            if (null == rp)
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "Missing \"" + DAConstants.RP_PID + "\" header in \"" +
                        resPath + "\" section");
        }
    }
    
    Vector getBundleEntries() {
        return bundleEntries;
    }
    
    void updateBundleEntry(BundleEntry be) {
        int index = bundleEntries.indexOf(be);
        BundleEntry e = (BundleEntry) bundleEntries.get(index);
        e.setId(be.getId());
        e.setSymbName(be.getSymbName());
        e.setVersion(be.getVersion());
    }
    
    Vector getResourceEntries() {
        return resourceEntries;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getBundleSymNameVersionPairs()
     */
    public String[][] getBundleSymNameVersionPairs() {
        String[][] ret = new String[bundleEntries.size()][2];
        int i = 0;
        for (Iterator iter = bundleEntries.iterator(); iter.hasNext();) {
            BundleEntry be = (BundleEntry) iter.next();
            ret[i][0] = be.getSymbName();
            ret[i][1] = be.getVersion();
            ++i;    
        }
        return ret;
    }
    
    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResources()
     */
    public String[] getResources() {
        String[]ret = new String[resourceEntries.size()];
        int i = 0;
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            ret[i] = re.getName();
            ++i;    
        }
        return ret;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResourceHeader(java.lang.String, java.lang.String)
     */
    public String getResourceHeader(String name, String header) {
        for (Iterator iter = resourceEntries.iterator(); iter.hasNext();) {
            ResourceEntry re = (ResourceEntry) iter.next();
            if (re.getName().equals(name))
                return re.getValue(header);
        }
        return null;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getHeader(java.lang.String)
     */
    public String getHeader(String name) {
        return (String) mainSection.get(name);
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getId()
     */
    public long getId() {
        return null == id ? -1 : id.longValue();
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getName()
     */
    public String getName() {
        return dpName;
    }

    /**
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getVersion()
     */
    public Version getVersion() {
        return new Version(dpVersion); 
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getBundle(java.lang.String)
     */
    public Bundle getBundle(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResourceProcessor(java.lang.String)
     */
    public ServiceReference getResourceProcessor(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @throws DeploymentException
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstall()
     */
    public void uninstall() throws DeploymentException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#uninstallForceful()
     */
    public boolean uninstallForceful() {
        // TODO Auto-generated method stub
        return false;
    }

    boolean fixPack() {
        return fixPackRange != null;
    }
    
    /********************************************************************/
    /********************************************************************/
    /********************************************************************/
    /********************************************************************/

    // Class to track resource processors
    /*private class Tracker extends ServiceTracker {
        public Tracker() {
            super(DeploymentPackageImpl.this.context, 
                    ResourceProcessor.class.getName(), null);
        }
    }
    
    DeploymentPackageImpl(Manifest manifest, 
                          BundleContext context, 
                          DeploymentAdminImpl admin,
                          Logger logger,
                          Transaction transaction) 
    {
        this.context = context;
        this.admin = admin;
        this.logger = logger;
        this.transaction = transaction;
        
        dpManVer = manifest.getMainAttributes().getValue("DeploymentPackage-ManifestVersion");
        dpName = manifest.getMainAttributes().getValue("DeploymentPackage-Name");
        isFixPack = (null != manifest.getMainAttributes().getValue("DeploymentPackage-FixPack"));
        customizerBundle = manifest.getMainAttributes().getValue("DeploymentPackage-ProcessorBundle");
        dpVersion = new Version(manifest.getMainAttributes().getValue("DeploymentPackage-Version"));
        
        processNameSections(manifest);
    }
    

    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof DeploymentPackage))
            return false;
        DeploymentPackage other = (DeploymentPackage) obj;
        return getName().equals(other.getName()) &&
               getVersion().equals(other.getVersion());
    }
    
    public int hashCode() {
        return (getName() + getVersion()).hashCode();
    }
    
    public String toString() {
        return "{" + getName() + " " + getVersion() + "}";
    }
    
    void startTracker() {
        if (null == tracker)
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

    public String[][] getBundleSymNameVersionPairs() {
        return null;
    }

    /**
     * @param arg0
     * @return
     * @see org.osgi.service.deploymentadmin.DeploymentPackage#getResourceProcessor(java.lang.String)
     */
    /*public ServiceReference getResourceProcessor(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }*/

    /*private Bundle installBundle(final BundleEntry bundleEntry, final JarInputStream jis)
            throws BundleException 
    {
        Bundle b = context.installBundle(bundleEntry.location, jis);
        BundleEntry be = new BundleEntry(bundleEntry);
        be.id = new Long(b.getBundleId());
        bundles.add(be);
        newBundles.add(be);
        transaction.addRecord(new TransactionRecord(
                Transaction.INSTALLBUNDLE, new Object[] {b, bundles, be}));
        return b;
    }*/

    /*private void uninstallBundles(Set set) throws BundleException {
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
     }*/

    /*private Bundle updateBundle(BundleEntry bundleEntry, JarInputStream jis)
    		throws BundleException 
    {
        Bundle[] bundles = context.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            Bundle b = bundles[i];
            if (b.getLocation().equals(bundleEntry.location)) {
                b.update(jis);
                updatedBundles.add(bundleEntry);
                transaction.addRecord(new TransactionRecord(
                        Transaction.UPDATEBUNDLE, new Object[] {b}));
                return b;
            }
        }
        return null;
    }*/
    
    /*private void startBundle(Bundle b) throws BundleException {
        if (b.getState() != Bundle.ACTIVE)
            b.start();
        transaction.addRecord(new TransactionRecord(
                Transaction.STARTBUNDLE, new Object[] {b}));
    }*/

    /*private void startBundles() throws BundleException {
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry entry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(entry.id.longValue());
            if (!isCustomizerBundle(entry))
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
    }*/
    
    // called from the Transaction.commit because RP in the processor 
    // bundle is needed until ResourceProcessor.complete
    /*void stopCustomizerBundle() throws BundleException {
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
    }*/

    /*private boolean isCustomizerBundle(BundleEntry bentry) {
        if (bentry.symbName.equals(customizerBundle))
        	return true;
        else
            return false;
    }*/

    /*private void extractFilters(String str) throws InvalidSyntaxException {
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
    }/*
    
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

    /*public Bundle[] listBundles() {
        Vector ret = new Vector(bundles.size());
        for (Iterator iter = bundles.iterator(); iter.hasNext();) {
            BundleEntry bentry = (BundleEntry) iter.next();
            Bundle b = context.getBundle(bentry.id.longValue());
            ret.add(b);
        }
        return (Bundle[]) ret.toArray(new Bundle[] {});
    }*/

    /*public void setDeploymentAdmin(DeploymentAdminImpl deploymentAdmin) {
        this.admin = deploymentAdmin;
    }

    private void processResource(ServiceReference procRef, String resName,
            int operation) throws Exception 
    {
        ResourceProcessor proc = (ResourceProcessor)
        		tracker.getService(procRef);
        transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, 
                new Object[] {proc, this, new Integer(operation)}));
        proc.process(resName, stream);
    }
    
    private void dropResources(Set diffResources) throws Exception {
        for (Iterator iter = diffResources.iterator(); iter.hasNext();) {
            String resFilter = (String) iter.next();
            ResourceProcessor proc = (ResourceProcessor) tracker.
            		getService(findProcessor(resFilter, filters));
            transaction.addRecord(new TransactionRecord(Transaction.PROCESSOR, 
                    new Object[] {proc, this, new Integer(DeploymentSession.UPDATE)}));
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
    void install() throws Exception {
        newBundles = new HashSet();
        
        extractFilters(manifest.getMainAttributes().getValue(
        		"DeploymentPackage-Processing"));

    	WrappedJarInputStream.Entry entry = stream.nextEntry();
        while (null != entry) {
            if (entry.isBundle()) {
                BundleEntry bentry = getBundleEntry(entry.getJarEntry());
                // TODO chech whether there is a bundle with the same name and version
                // in the framework
                // TODO chech whethet there is a bundle with the same name in the DP
                Bundle b = installBundle(bentry, stream);
                if (isCustomizerBundle(bentry))
                    startBundle(b);
            } else if (entry.isResource()){
                String resName = entry.getName();
                ServiceReference procRef = findProcessor(resName, filters);
                if (null != procRef) {
                    processResource(procRef, resName, DeploymentSession.INSTALL);
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
            entry = stream.nextEntry();;
        }
        startBundles();
    }

    // TODO throw exceptions
    public void uninstall() {
        //checkPermission(getName(), DeploymentAdminPermission.ACTION_UNINSTALL_DP);
        
        try {
            stopNonCustomizerBundles();
            for (Iterator iter = resources.iterator(); iter.hasNext();) {
                String resName = (String) iter.next();
                ServiceReference procRef = findProcessor(resName, filters);
                if (null != procRef) {
                    processResource(procRef, resName, DeploymentSession.UNINSTALL);
                    removeResource(iter);
                } else {
                    // it is catched by the catch clause
                    throw new Exception("There is no resource processor for " +
                    		"resource " + resName);
                }
            }
            pendingBundles = bundles;
            dropResources(resources);
            uninstallBundles(bundles);
        } catch (Exception e) {
            logger.log(e);
            // TODO throw e;
        }

        // TODO eliminate this
        admin.onUninstallDp(this);
    }

    // TODO restart customizer first
    void update(WrappedJarInputStream in) throws Exception {
        this.stream = in;
        this.manifest = in.getManifest();
        newBundles = new HashSet();
        updatedBundles = new HashSet();
       
        Hashtable oldFilters = new Hashtable(filters);
        extractFilters(manifest.getMainAttributes().getValue(
        		"DeploymentPackage-Processing"));
        
        try {
            stopAllBundles();
            WrappedJarInputStream.Entry entry = stream.nextEntry();
            Set bundlesNotToUninstall = new HashSet();
            Set resourcesNotToDrop = new HashSet();
            while (null != entry) {
                if (entry.isMissingBundle()) {
                    BundleEntry bentry = getBundleEntry(entry.getJarEntry());
                    bundlesNotToUninstall.add(bentry);
                } else if (entry.isBundle()) {
                    BundleEntry bentry = getBundleEntry(entry.getJarEntry());
                    if (bundles.contains(bentry)) {
                        updateBundle(bentry, stream);
                    } else {
                        // TODO chech whether there is a bundle with the same name and version
                        // in the framework
                        // TODO chech whether there is a bundle with the same name in the DP
                        installBundle(bentry, stream);
                    }
                    bundlesNotToUninstall.add(bentry);
                } else if (entry.isMissingResource()) {
                    String resName = entry.getName();
                    resourcesNotToDrop.add(resName);
                } else if (entry.isResource()) {
                    String resName = entry.getName();
                    ServiceReference procRefOld = findProcessor(resName, oldFilters);
                    ServiceReference procRefNew = findProcessor(resName, filters);
	                if (null != procRefOld) {
	                    processResource(procRefOld, resName, DeploymentSession.UPDATE);
	                    resourcesNotToDrop.add(resName);
	                } else if (null != procRefNew) {
	                    processResource(procRefNew, resName, DeploymentSession.UPDATE);
	                    resourcesNotToDrop.add(resName);
	                } else {
	                    // it is catched by the catch clause
	                    throw new Exception("There is no resource processor for " +
	                    		"resource " + resName);
	                }
                } else {
                    // TODO error
                }

                entry = stream.nextEntry();
            }
            
            pendingBundles = new HashSet(bundles);
            pendingBundles.removeAll(bundlesNotToUninstall);
            uninstallBundles(pendingBundles);

            Set resourcesToDrop = new HashSet(resources);
            resourcesToDrop.removeAll(resourcesNotToDrop);
            dropResources(resourcesToDrop);
            
            DeploymentPackageVerifier.verify(this);
            startBundles();
        } catch (Exception e) {
            throw e;
        }
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
	}*/
   
}