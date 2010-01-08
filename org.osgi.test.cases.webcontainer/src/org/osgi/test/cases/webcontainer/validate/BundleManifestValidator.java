/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.test.cases.webcontainer.validate;

import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

import junit.framework.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

/**
 * @version $Rev$ $Date$
 * 
 *          BundleManifestValidator is used to validate the manifest is 
 *          constructed per RFC 66 spec
 */
public class BundleManifestValidator extends Assert implements Validator{

    boolean debug = false;
    Bundle b;
    Dictionary dictionary;
    Manifest manifest;
    Map deployOptions;
    private final String[] REQUIREDIMPORT = {"javax.servlet; version=2.5","javax.servlet.http; version=2.5", "javax.servlet.jsp; version=2.1", "javax.servlet.jsp.tagext; version=2.1"};
    private static final String WEBINFCLASSES = "WEB-INF/classes";
    private static final String WEBINFLIB = "WEB-INF/lib";
    
    public BundleManifestValidator(Bundle b) {
        this.b = b;
        this.dictionary = b.getHeaders();
    }
    
    public BundleManifestValidator(Bundle b, boolean debug) {
        this.b = b;
        this.dictionary = b.getHeaders();
        this.debug = debug;
    }
    
    public BundleManifestValidator(Bundle b, Map deployOptions, boolean debug) {
        this.b = b;
        this.dictionary = b.getHeaders();
        this.deployOptions = deployOptions;
        this.debug = debug;
    }
    
    
    public BundleManifestValidator(Bundle b, Manifest m, Map deployOptions, boolean debug) {
        this.b = b;
        this.dictionary = b.getHeaders();
        this.manifest = m;
        this.deployOptions = deployOptions;
        this.debug = debug;
    }
    
    public void validate() throws Exception {
        validateSymbolicName();
        validateBundleVersion();
        validateBundleManifestVersion();
        validateBundleClassPath();
        validateImportPackage();
        validateWebContextPath();
        validateOthersPreserved();
    }
    
    
    /*
     * validate the existence of Bundle-SymbolicName as it is required
     * Also validate:
     * 1. the deployer specified Bundle-SymbolicName value will be used.
     * 2. Otherwise, preserve the Bundle-SymbolicName value in the manifest file
     */
    public void validateSymbolicName() throws Exception {
        assertNotNull(this.dictionary);
        
        // test bundle manifest is constructed per user's deployment options
        log("verify Bundle-SymbolicName exists");
        if (this.debug) {
            log("SymbolicName is " + this.b.getSymbolicName());
        }              
        assertNotNull(this.b.getSymbolicName());
        
        // dSymbolicName - deployer specified Bundle-SymbolicName value
        Object dSymbolicName = this.deployOptions == null ? null : this.deployOptions.get(Constants.BUNDLE_SYMBOLICNAME);
        // mSymbolicName - manifest Bundle-SymbolicName value
        Object mSymbolicName = this.manifest == null ? null : this.manifest.getMainAttributes().getValue(new Name(Constants.BUNDLE_SYMBOLICNAME));
        if (dSymbolicName != null) {
            assertEquals((String)dSymbolicName, this.b.getSymbolicName());
        } else if (mSymbolicName != null) {
            assertEquals((String)mSymbolicName, this.b.getSymbolicName());
        } 
        
        // TODO: can we verify if the symbolic name is unique?
        // TODO: possible test case: what if a deployer or manifest specifies a non-unique symbolic name?
    }
    
    /** 
     * validate Bundle-Version is not required.  if it is not there, default to 0.0.0
     * Also validate:
     * 1. the deployer specified Bundle-Version value will be used.
     * 2. Otherwise, preserve the Bundle-Version value in the manifest file
     */
    public void validateBundleVersion() throws Exception {
        assertNotNull(this.dictionary);
        Object versionObj = this.dictionary.get(Constants.BUNDLE_VERSION);
        if (versionObj != null) {
	        String version = (String)versionObj;
	        if (this.debug) {
	            log(Constants.BUNDLE_VERSION + " is " + version);
	        }       
	
	        // validate the version
	        // this could throw IllegalArgumentException 
	        // if v is improperly formatted
	        Version v = new Version(version);
	
	        // dVersion - deployer specified Bundle-Version value
	        Object dVersion = this.deployOptions == null ? null : this.deployOptions.get(Constants.BUNDLE_VERSION);
	        // mVersion - manifest Bundle-Version value
	        Object mVersion = this.manifest == null ? null : this.manifest.getMainAttributes().getValue(Constants.BUNDLE_VERSION);
	        // let's convert them to Version object so that we can treat version 1.0 and 1.0.0 the same value
	        if (dVersion != null) {
	            assertTrue(new Version((String)dVersion).compareTo(v) == 0);
	        } else if (mVersion !=null) {
	            assertTrue(new Version((String)mVersion).compareTo(v) == 0);
	        }  
        }
    }
    
    /*
     * validate the existence of Bundle-ManifestVersion as it is required
     * and it is >=2.
     * Also validate:
     * 1. the deployer specified Bundle-ManifestVersion value will be used.
     * 2. Otherwise, preserve the Bundle-ManifestVersion value in the manifest file
     * 3. otherwise, set it to 2.
     */
    public void validateBundleManifestVersion() throws Exception {
        assertNotNull(this.dictionary);
        
        log("verify Bundle-ManifestVersion exists and >=2");
        if (this.debug) {
            log(Constants.BUNDLE_MANIFESTVERSION + " is " + (String)this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        }       
        assertNotNull((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        assertTrue((Integer.parseInt((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION))) >= 2);
        
        // dVersion - deployer specified Bundle-Version value
        Object dVersion = this.deployOptions == null ? null : this.deployOptions.get(Constants.BUNDLE_MANIFESTVERSION);
        // mVersion - manifest Bundle-Version value
        Object mVersion = this.manifest == null ? null : this.manifest.getMainAttributes().getValue(new Name(Constants.BUNDLE_MANIFESTVERSION));
        if (dVersion != null) {
            assertEquals((String)dVersion, (String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        } else if (mVersion !=null) {
            assertEquals((String)mVersion, (String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        } else {
            assertEquals("2", (String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        }
        
    }
    
    /*
     * validate the existence of Bundle-ClassPath as it is required
     * Also validate:
     * 1. initializing the first path entry to "WEB-INF/classes" if it is not there
     * 2. adding each of the libraries from WEB-INF/lib if not already present on path. 
     * 3. append any Bundle-ClassPath deploy options
     */
    public void validateBundleClassPath() throws Exception {
        assertNotNull(this.dictionary);
        
        // verify Bundle-ClassPath exists
        log("verify Bundle-ClasaPath exists");
        String actualClassPath = (String)this.dictionary.get(Constants.BUNDLE_CLASSPATH);
        if (this.debug) {
            log(Constants.BUNDLE_CLASSPATH + " is " + actualClassPath);
        }
        assertNotNull(actualClassPath);
        String[] actualClassPathArray = toArray(actualClassPath);

        // mClasspath - original manifest classpath String
        Object mClasspath = this.manifest == null ? null : this.manifest.getMainAttributes().get(new Name(Constants.BUNDLE_CLASSPATH));
        // dClasspath - deployer specified classpath String
        Object dClasspath = this.deployOptions == null ? null : this.deployOptions.get(Constants.BUNDLE_CLASSPATH);

        //assertTrue("verify WEB-INF/classes exist in the actual classpath", exist(WEBINFCLASSES, actualClassPathArray, false));
        assertEquals("verify WEB-INF/classes exist in the actual classpath and is the first entry", WEBINFCLASSES, actualClassPathArray[0]);
        // verify WEB-INF/lib jars exist in the actual classpath
        Enumeration e = this.b.findEntries(WEBINFLIB, "*.jar", false);
        int count = 0;
        while (e != null && e.hasMoreElements()) {
            URL url = (URL) e.nextElement();
            String jarPath = url.getFile();
            // strip out the first / of the jarPath if jarPath is /WEB-INF/lib/xxx.jar
            if (url.getFile().startsWith("/")) {
                jarPath = url.getFile().substring(1);
            } 
            assertTrue("verify WEB-INF/lib jars exist in the actual classpath", exist(jarPath, actualClassPathArray, false));

            count++;
        }
        
        // verify no other path gets added to the Bundle-Classpath, the following should be true unless one of the jar 
        // in the lib dir contains a Class-Path entry in the manifest.
        assertEquals("verify no other path gets added to the Bundle-Classpath", count + 1, actualClassPathArray.length);
        
        // verify no dups on the classpath
        assertTrue(!containDuplicate(actualClassPathArray));
    }
    
    /*
     * validate the existence of Import-Package as it is required
     * Also validate existence of javax.servlet, javax.servlet.http, 
     * javax.servlet.jsp and javax.servlet.jsp.tagext packages:
     * also verify deploy options should overwrite original manifest options.
     */
    public void validateImportPackage() throws Exception {
        assertNotNull(this.dictionary);
        String actualImports = (String)this.dictionary.get(Constants.IMPORT_PACKAGE);
        // verify Import-package exists
        if (this.debug) {
            log(Constants.IMPORT_PACKAGE + " is " + actualImports);
        }
        assertNotNull(actualImports);
        String[] actualImportsArray = toArray(actualImports);
        
        // mImports - original manifest Import-Package String array
        Object mImports = this.manifest == null ? null : this.manifest.getMainAttributes().get(new Name(Constants.IMPORT_PACKAGE));
        // dImports - deployer specified Import-Package String arrary
        Object dImports = this.deployOptions == null ? null : this.deployOptions.get(Constants.IMPORT_PACKAGE);
 
        // verify the existence of the servlet and jsp packages on Import-pacakage header
        // we use loose check here to allow directives
        for (int i = 0; i < this.REQUIREDIMPORT.length ; i++) {
            assertTrue(existLoose(this.REQUIREDIMPORT[i], actualImportsArray));
        }
        
        // verify dImports are added to the actualImports
        if (dImports != null) {
            String[] di = toArray((String)dImports);
            for (int i = 0; i < di.length ; i++) {
                assertTrue(exist(di[i], actualImportsArray, true));
            }
        }
        
        // verify package specified by mImports are on the actualImports
        // if there are conflicts with dImports, dImports should win
        if (mImports != null) {
            String[] mi = toArray((String)mImports);
            for (int i = 0; i< mi.length; i++) {
                boolean exist = exist(mi[i], actualImportsArray, true);
                if (!exist) {
                    // it is possible because of the conflicts with dImports
                    assertTrue(existLoose(getPackage(mi[i]), (String[])dImports));
                    assertTrue(existLoose(getPackage(mi[i]), actualImportsArray));
                }              
            }
        }
        
        // verify no dups on the Import-Package list
        assertTrue(!containDuplicate(actualImportsArray));
    }
    
    /*
     * validate the existence of Web-ContextPath as it is required
     * Also validate:
     * 1. the deployer specified Web-ContextPath value will be used.
     * 2. Otherwise, preserve the Web-ContextPath value in the manifest file
     */
     // TODO: it is still not clear if Web-ContextPath is required.
    public void validateWebContextPath() throws Exception {
        // verify Web-ContextPath exists
        log(WEB_CONTEXT_PATH + " must exist as it is required");
        if (this.debug) {
            log(WEB_CONTEXT_PATH + " is " + (String)this.dictionary.get(WEB_CONTEXT_PATH));
        }
        assertNotNull((String)this.dictionary.get(WEB_CONTEXT_PATH));
        
        // dWebContextPath - deployer specified Web-ContextPath value
        Object dWebContextPath = this.deployOptions == null ? null : this.deployOptions.get(WEB_CONTEXT_PATH);
        // mWebContextPath - manifest Web-ContextPath value
        Object mWebContextPath = this.manifest == null ? null : this.manifest.getMainAttributes().getValue(new Name(WEB_CONTEXT_PATH));
        if (dWebContextPath != null) {
            assertEquals((String) this.dictionary.get(WEB_CONTEXT_PATH), (String)dWebContextPath);
        } else if (mWebContextPath !=null) {
            assertEquals((String) this.dictionary.get(WEB_CONTEXT_PATH), (String)mWebContextPath);
        }
        // TODO: verify Web-ContextPath is unique on the server
    }
    
    /*
     * validate other values specified by the original manifest is preserved, 
     * after the war manifest processing by the url handler
     */
    public void validateOthersPreserved() throws Exception {
        // original manifest attribute
        Attributes attributes = this.manifest.getMainAttributes();

        Set keyset = (Set)attributes.keySet();
        Iterator it = keyset.iterator();
        while(it.hasNext()) {
            Name key = (Name)it.next();
            if (key.toString().equals(Constants.BUNDLE_VERSION) ||
                    key.toString().equals(Constants.BUNDLE_SYMBOLICNAME) ||
                    key.toString().equals(Constants.BUNDLE_MANIFESTVERSION) ||
                    key.toString().equals(Constants.BUNDLE_CLASSPATH) ||
                    key.toString().equals(Constants.IMPORT_PACKAGE) ||
                    key.toString().equals(Constants.EXPORT_PACKAGE) ||
                    key.toString().equals(WEB_CONTEXT_PATH) ||
                    key.toString().equals(WEB_JSP_EXTRACT_LOCATION)) {
                continue;
            } else {
                // compare the other attribute with what is in the dictionary from the bundle.getHeaders()
                log("from original manifest " + key + ": " + attributes.get(key));
                log("from bundle headers " + key + ": " + this.dictionary.get(key.toString()));
               assertEquals("checking if other attributes from original manifest is preserved", attributes.get(key), this.dictionary.get(key.toString()));
            }           
        }
    }
    
    // convert a String to a String array
    private String[] toArray(String s) {
        if (s.indexOf(",") > 0) {
            return s.split(",");
        } else {
            String[] sArray = {s};
            return sArray;
        }
    }
    
    // check if a particular classpath exist in the classpath c String array
    private boolean exist(String exist, String[] c, boolean trim) {
        boolean find = false;
        for (int j = 0; j < c.length; j++) {
            if (trim) {
                if (c[j].trim().equals(exist.trim())) {
                    find = true;
                    break;
                }
            } else {
                if (c[j].equals(exist)) {
                    find = true;
                    break;
                } 
            }
        }
        return find;
    }
    
    // check if a particular String exists in a String array
    private boolean existLoose(String exist, String[] c) {
        boolean find = false;
        for (int j = 0; j < c.length; j++) {
            ManifestPackage p1 = new ManifestPackage(exist);
            ManifestPackage p2 = new ManifestPackage(c[j]);
            if (p1.getPackageName().equalsIgnoreCase(p2.getPackageName())) {
                // check if their version match
                if (p1.getPackageVersion().compareTo(p2.getPackageVersion()) == 0) {
                    find = true;
                    break;
                }
            }
        }
        return find;
    }
    
    private void log(String s) {
        System.out.println(s);
    }
    
    private boolean containDuplicate (String[] s) {
        HashSet h = new HashSet();
        for (int i = 0; i < s.length; i++) {
            boolean success = h.add(s[i]);
            if (!success) {
                return true;
            }
        }
        return false;
    }
    /*
     * get the package name out of the import package value
     */
    private String getPackage(String p) {
        int i = p.indexOf(";");
        return i > 0 ? p.substring(0, i-1) : p;
    }
    
    /*
     * trim all spaces & double quotes off a String
     * this is needed as a user could have 
     * javax.servlet;version="2.5"
     * javax.servlet; version="2.5"
     * javax.servlet;version=2.5
     * and I think all 3 are valid
     * 
     */
    private String trimAll(String s) {
        String result = trimAll(s, " ");
        return trimAll(result, "\"");     
    }
    
    private String trimAll(String s, String splitter) {
        if (s.indexOf(splitter) > 0) {
            String[] split = s.trim().split(splitter);
            String result = "";
            for (int i = 0; i < split.length; i++) {
                result = result + split[i].trim();
            }
            return result;
        } else {
            return s.trim();
        }
        
    }

}
