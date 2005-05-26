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
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.osgi.service.deploymentadmin.DeploymentException;

/**
 * DeploymentPackageJarInputStream class wraps the JarInputStream implementation  
 * to override its behaviour according to the needs of the deployment 
 * packages (DPs). See nextEntry(), closeEntry() and getNextJarEntry() 
 * methods! 
 */
public class DeploymentPackageJarInputStream {
    
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
            missing = false;
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
        
        /*
         * Return list of Certificate[]-s. One list element is one 
         * certificate chain.
         */
        private List getCertificateChains() {
            List ret = new LinkedList();
            Certificate[] certs = getCertificates();
            
            if (null == certs || certs.length == 0)
                return ret;
            
            int i = 0;
            while (i < certs.length) {
    	        ArrayList e = new ArrayList();
    	        X509Certificate cPrev = null;
    	        X509Certificate cAct = (X509Certificate) certs[i];
    	        while ( cPrev == null || cPrev.getIssuerDN().equals(cAct.getSubjectDN()) ) {
    	            e.add(cAct);
    	            cPrev = cAct;
    	            ++i;
    	            if (i >= certs.length)
    	                break;
    	            cAct = (X509Certificate) certs[i];
    	        }
    	        ret.add(e.toArray(new X509Certificate[] {}));
            }
            
            return ret;
        }

        /*
         * Returns a the list of cerificate chains. One chain is a 
         * String[].
         */
        public List getCertificateStringChains() {
            List l = getCertificateChains();
            List list = new LinkedList();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                X509Certificate[] cs = (X509Certificate[]) iter.next();
                List buffer = new Vector();
                for (int i = 0; i < cs.length; i++)
                    buffer.add(cs[i].getSubjectDN().toString());
                list.add(buffer.toArray(new String[] {}));
            }
            return list;
        }
    }
    
    private JarInputStream jis;
    private Map            resourceNames;
    private Entry          actEntry;
    private boolean        fixPack;
    private LinkedList     buffer = new LinkedList();
    private List           certChains;
    private List           certStringChains;
        
    public DeploymentPackageJarInputStream(InputStream is) 
    		throws IOException, DeploymentException 
    {
	    this.jis = new JarInputStream(is);
	    resourceNames = new HashMap(getManifest().getEntries());
	    fixPack = (null != 
	        	jis.getManifest().getMainAttributes().getValue(DAConstants.DP_FIXPACK));
	    fillBuffer();
	}
    
    public List getCertChains() {
        return certChains;
    }
    
    public List getCertStringChains() {
        return certStringChains;
    }
	
    private void fillBuffer() throws IOException, DeploymentException {
        Entry entry = readNextEntry();
        if (null != entry) {
            buffer.addLast(entry);
            certChains = entry.getCertificateChains();
            certStringChains = entry.getCertificateStringChains();
        }

        /*Entry entry = readNextEntry();
        while (null != entry && isUninterested(entry)) {
            buffer.addLast(entry);
            entry = readNextEntry();
        }
        if (null != entry)
            certChains = entry.getCertificateChains();*/
    }

    /**
	 * Gives back the next Entry in the dployment package.
	 * @return The next Entry or <code>null</code> if there is no 
	 * more entries.
	 * @throws IOException
	 * @throws DeploymentException
	 */
    public Entry nextEntry() throws IOException, DeploymentException {
        if (buffer.isEmpty())
            return readNextEntry();
        else
            return (Entry) buffer.removeFirst();
    }
    
    private Entry readNextEntry() throws IOException, DeploymentException {
        if (null != actEntry)
            return actEntry;
        
        JarEntry je = getNextJarEntry();
        if (null == je) {
            // The stream ended but we may have missing bundles/resources
            Iterator it = resourceNames.keySet().iterator();
            if (it.hasNext()) {
                String name = (String) it.next();
                if (!fixPack)
                    throw new DeploymentException(DeploymentException.CODE_ORDER_ERROR,
                            "There is no data in the stream for \"Name\"-section: " +
                            name);
                actEntry = new Entry(name, (Attributes) resourceNames.get(name));
                
                // remove to ensure that the sequence of Entries ends
                it.remove();
            }
        }
        else {
            if (null == je.getAttributes())
                throw new DeploymentException(DeploymentException.CODE_MISSING_HEADER,
                        "There is no \"Name\"-section for JarEntry: " + je);
            
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

    /*
     * Skips uninterested JarEntries (directories, .sf files, etc.)
     */
    private JarEntry getNextJarEntry() throws IOException {
        JarEntry je = jis.getNextJarEntry();
        while (null != je && isUninterested(je))
            je = jis.getNextJarEntry();
        
        return je; 
    }

    private boolean isUninterested(JarEntry je) throws IOException {
        if (je.isDirectory())
            return true;
        if (je.getName().toLowerCase().startsWith("meta-inf/")) {
            // TODO it is not too nice
            String name = je.getName().toLowerCase();
            if (name.endsWith(".dsa")) {
                ByteArrayOutputStream bos = readIntoBuffer();
                closeEntry();
                String key = name.substring("meta-inf/".length(), name.indexOf(".dsa"));
            }
            return true;
        }
        return false;
    }

    private ByteArrayOutputStream readIntoBuffer() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[0x1000];
            int i = jis.read(data);
            while (-1 != i) {
                bos.write(data, 0, i);
                i = jis.read(data);
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
