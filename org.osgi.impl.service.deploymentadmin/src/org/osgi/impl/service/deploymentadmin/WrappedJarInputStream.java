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

public class WrappedJarInputStream extends JarInputStream {
    
    class Entry {
        private String 	   name;
        private JarEntry   jarEntry;
        private Attributes attributes;
        
        public Entry(JarEntry jarEntry) throws IOException {
            this.jarEntry = jarEntry;
            this.name = jarEntry.getName();
            this.attributes = jarEntry.getAttributes();
        }
        
        public Entry(String name, Attributes attributes) {
            this.jarEntry = null;
            this.attributes = attributes;
            this.name = name;
        }
        
        public boolean isBundle() {
            if (null == jarEntry)
                return false;
            String symbName = attributes.getValue("Bundle-SymbolicName");
            String version = attributes.getValue("Bundle-Version");
            return null != symbName && null != version;
        }
        
        public boolean isMissingBundle() {
            if (null == jarEntry)
                return false;
            String str = attributes.getValue("MissingResource-Bundle");
            return null != str;
        }

        public boolean isMissingResource() {
            if (isBundle())
                return false;
            String str = attributes.getValue("MissingResource-Resource");
            return null != str;
        }

        public boolean isResource() {
            return !isBundle();
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public JarEntry getJarEntry() {
            return jarEntry;
        }

        public String getName() {
            return name;
        }
    }
    
    private Map			   resourceNames;
    private boolean		   hasToClose = false;
    
	public WrappedJarInputStream(InputStream is) throws IOException {
		super(is);
		resourceNames = new HashMap(getManifest().getEntries());
	}

	public WrappedJarInputStream(InputStream is, boolean verify)
			throws IOException {
		super(is, verify);
		resourceNames = new HashMap(getManifest().getEntries());
	}
	
    public Entry nextEntry() throws IOException {
        Entry entry;
        if (hasToClose)
            closeEntry();
        JarEntry je = getNextJarEntry();
        if (null == je) {
            Iterator it = resourceNames.keySet().iterator();
            if (!it.hasNext())
                entry = null;
            else {
                String key = (String) it.next();
                entry = new Entry(key, (Attributes) resourceNames.get(key));
                it.remove();
            }
        }
        else {
            hasToClose = true;
            entry = new Entry(je);
            resourceNames.remove(je.getName());
        }
        return entry;
    }

	public void close() throws IOException {
		// does nothing
		// because OSGi BundleContext.installBundle(String location, InputStream
		// in)
		// method closes the "in" InputStream and this behaviour is not allowed
		// in JarInputStream
	}
	
	void realClose() throws IOException {
	    close();
	}
	
    public void closeEntry() throws IOException {
        // nextEntry() does everything
    }
}
