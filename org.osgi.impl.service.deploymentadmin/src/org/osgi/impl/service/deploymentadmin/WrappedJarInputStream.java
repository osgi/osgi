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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * WrappedJarInputStream class wraps the JarInputStream implementation  
 * to override its behaviour according to the needs of the deployment 
 * packages (DPs). See nextEntry(), close() and realClose() methods! 
 */
public class WrappedJarInputStream extends JarInputStream {
    
    /**
     * Extends the JarEntry functionality according to the needs of 
     * the deployment packages (DPs). It is able to sign bundles, 
     * resources and their missing variations.
     */
    public class Entry extends JarEntry {
        private boolean    missing;
        private Attributes attrs;

        private Entry(JarEntry je) throws IOException {
            super(je);
            attrs = je.getAttributes();
        }
        
        private Entry(String name, Attributes attrs) {
            super(name);
            String miss = attrs.getValue(DAConstants.MISSING);
            if (null == miss || !Boolean.valueOf(miss).booleanValue())
                throw new RuntimeException("Internal error.");
            
            this.attrs = attrs;
            missing = true;
        }
        
        public boolean isBundle() {
            String symbName = attrs.getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
            String version = attrs.getValue(DAConstants.BUNDLE_VERSION);
            return null != symbName && null != version;
        }
        
        public boolean isCustomizerBundle() {
            String cust = attrs.getValue(DAConstants.CUSTOMIZER);
            boolean isCust = Boolean.valueOf(cust).booleanValue();
            return isBundle() && isCust;
        }

        public boolean isResource() {
            return !isBundle();
        }
        
        public boolean isMissing() {
            return missing;
        }

        public Attributes getAttributes() {
            return attrs;
        }
    }
    
    private Map			   resourceNames;
    private boolean		   hasToClose = false;
    private Entry          actEntry;
    
	public WrappedJarInputStream(InputStream is) throws IOException {
	    this(is, false);
	}

	public WrappedJarInputStream(InputStream is, boolean verify)
			throws IOException {
		super(is, verify);
		resourceNames = new HashMap(getManifest().getEntries());
	}
	
	/**
	 * Gives back the next Entry in the dployment package.
	 * The entry can be:<p> bundle<p> missing bundle<p> resource<p>
	 * missing resource 
	 * @return The next Entry or <code>null</code> if there is no 
	 * more entries.
	 * @throws IOException
	 */
    public Entry nextEntry() throws IOException {
        // if the actual entry has not been closed with CloseEntry()  
        // we do not move towards
        if (null != actEntry)
            return actEntry;
        
        JarEntry je = getNextJarEntry();
        if (null == je) {
            // The stream ended but we may have missing bundles/resources
            Iterator it = resourceNames.keySet().iterator();
            if (!it.hasNext())
                // The stream ended and we have no more missing bundles/resources 
                actEntry = null;
            else {
                String name = (String) it.next();
                actEntry = new Entry(name, (Attributes) resourceNames.get(name));
            
                // remove to ensure that the sequence of Entries ends
                it.remove();
            }
        }
        else {
            // We have opened a JarEntries so we have to close it 
            // when nextEntry() is called next time
            actEntry = new Entry(je);
            
            // remove to ensure that the sequence of Entries ends
            resourceNames.remove(je.getName());
        }
        return actEntry;
    }

	public void close() throws IOException {
		// Does nothing because e.g. OSGi 
	    // BundleContext.installBundle(String location, InputStream in)
		// method closes the entire JarInputStream but only the actual 
	    // JarEntry has to be closed. To really close the stream call 
	    // the realClose() method.
	}
	
	// See the close() method!
	void realClose() throws IOException {
	    close();
	}
	
    public void closeEntry() throws IOException {
        super.closeEntry();
        actEntry = null;
    }
}
