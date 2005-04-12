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
import java.util.jar.Manifest;

/**
 * WrappedJarInputStream class wraps the JarInputStream implementation  
 * to override its behaviour according to the needs of the deployment 
 * packages (DPs). See nextEntry() and closeEntry() methods! 
 */
public class WrappedJarInputStream {
    
    /**
     * Extends the JarEntry functionality according to the needs of 
     * the deployment packages (DPs). It is able to recognise bundles, 
     * resources and their missing variations.
     */
    public static class Entry extends JarEntry {
        private boolean               missing;
        private Attributes            attrs;
        private ByteArrayOutputStream buffer;

        private Entry(JarEntry je, ByteArrayOutputStream buffer) throws IOException {
            super(je);
            attrs = je.getAttributes();
            this.buffer = buffer;
        }
        
        private Entry(String name, Attributes attrs) {
            super(name);
            String miss = attrs.getValue(DAConstants.MISSING);
            if (null == miss || !Boolean.valueOf(miss).booleanValue())
                throw new RuntimeException("Internal error.");
            
            this.attrs = attrs;
            missing = true;
        }
        
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(buffer.toByteArray());
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
    
    private JarInputStream jis;
    private Map            resourceNames;
    private Entry          actEntry;
        
    public WrappedJarInputStream(InputStream is) throws IOException {
	    this.jis = new JarInputStream(is);
	    resourceNames = new HashMap(getManifest().getEntries());
	}
	
	/**
	 * Gives back the next Entry in the dployment package.
	 * @return The next Entry or <code>null</code> if there is no 
	 * more entries.
	 * @throws IOException
	 */
    public Entry nextEntry() throws IOException {
        if (null != actEntry)
            return actEntry;
        
        JarEntry je = getNextJarEntry();
        if (null == je) {
            // The stream ended but we may have missing bundles/resources
            Iterator it = resourceNames.keySet().iterator();
            if (it.hasNext()) {
                String name = (String) it.next();
                actEntry = new Entry(name, (Attributes) resourceNames.get(name));
            
                // remove to ensure that the sequence of Entries ends
                it.remove();
            }
        }
        else {
            ByteArrayOutputStream bos = readIntoBuffer();
            closeEntry();

            // We have opened a JarEntries so we have to close it 
            // when nextEntry() is called next time
            actEntry = new Entry(je, bos);
            
            // remove to ensure that the sequence of Entries ends
            resourceNames.remove(je.getName());
        }
        return actEntry;
    }

    private JarEntry getNextJarEntry() throws IOException {
        return jis.getNextJarEntry();
    }

    private ByteArrayOutputStream readIntoBuffer() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            int data = jis.read();
            while (-1 != data) {
                bos.write(data);
                data = jis.read();
            }
        } finally {
            if (null != bos)
                bos.close();
        }
        return bos;
    }
	
    public void closeEntry() throws IOException {
        // nextEntry() calls the JarInputStream.closeEntry() method
        actEntry = null;
    }

    public Manifest getManifest() {
        return jis.getManifest();
    }
    
}
