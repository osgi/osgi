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
import java.util.HashSet;
import java.util.Map;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import junit.framework.Assert;

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
    private static final String[] REQUIREDIMPORT = {"javax.servlet; version=2.5","javax.servlet.http; version=2.5", "javax.servlet.jsp; version=2.1", "javax.servlet.jsp.tagext; version=2.1"};
    private static final String WEBINFCLASSES = "WEB-INF/classes";
    
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
        validateBundleManifestVersion();
        validateBundleManifestVersion();
        validateBundleClassPath();
        validateImportPackage();
        validateExportPackage();
        validateWebContextPath();
        validateJSPExtractLocation();
    }
    
    
    /*
     * validate the existence of Bundle-SymbolicName as it is required
     * Also validate:
     * 1. the deployer specified Bundle-SymbolicName value will be used.
     * 2. Otherwise, preserve the Bundle-SymbolicName value in the manifest file
     */
    private void validateSymbolicName() throws Exception {
        assertNotNull(this.dictionary);
        
        // test bundle manifest is constructed per user's deployment options
        log("verify Bundle-SymbolicName exists");
        if (this.debug) {
            log("SymbolicName is " + this.b.getSymbolicName());
        }              
        assertNotNull(this.b.getSymbolicName());
        
        // dSymbolicName - deployer specified Bundle-SymbolicName value
        String dSymbolicName = this.deployOptions == null ? null : (String)this.deployOptions.get(Constants.BUNDLE_SYMBOLICNAME);
        // mSymbolicName - manifest Bundle-SymbolicName value
        String mSymbolicName = this.manifest == null ? null : this.manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
        if (dSymbolicName != null) {
            assertEquals(this.b.getSymbolicName(), dSymbolicName);
        } else if (mSymbolicName != null) {
            assertEquals(this.b.getSymbolicName(), mSymbolicName);
        } 
        
        // TODO: can we verify if the symbolic name is unique?
        // TODO: possible test case: what if a deployer or manifest specifies a non-unique symbolic name?
    }
    
    /*
     * validate the existence of Bundle-Version as it is required
     * Also validate:
     * 1. the deployer specified Bundle-Version value will be used.
     * 2. Otherwise, preserve the Bundle-Version value in the manifest file
     */
    private void validateBundleManifestVersion() throws Exception {
        assertNotNull(this.dictionary);
        
        log("verify Bundle-ManifestVersion exists and >=2");
        if (this.debug) {
            log(Constants.BUNDLE_MANIFESTVERSION + " is " + (String)this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        }       
        assertNotNull((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION));
        assertTrue((Integer.parseInt((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION))) >= 2);
        
        // dVersion - deployer specified Bundle-Version value
        String dVersion = this.deployOptions == null ? null : (String)this.deployOptions.get(Constants.BUNDLE_MANIFESTVERSION);
        // mVersion - manifest Bundle-Version value
        String mVersion = this.manifest == null ? null : (String)this.manifest.getMainAttributes().getValue(Constants.BUNDLE_MANIFESTVERSION);
        if (dVersion != null) {
            assertEquals((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION), dVersion);
        } else if (mVersion !=null) {
            assertEquals((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION), mVersion);
        } else {
            assertEquals((String) this.dictionary.get(Constants.BUNDLE_MANIFESTVERSION), "2");
        }
        
    }
    
    /*
     * validate the existence of Bundle-ClassPath as it is required
     * Also validate:
     * 1. initializing the first path entry to "WEB-INF/classes" if it is not there
     * 2. adding each of the libraries from “WEB-INF/lib” if not already present on path. 
     * 3. append any Bundle-ClassPath deploy options
     */
    private void validateBundleClassPath() throws Exception {
        assertNotNull(this.dictionary);
        
        // verify Bundle-Classpath exists
        log("verify Bundle-Classpath exists");
        String[] actualClasspath = (String[])this.dictionary.get(Constants.BUNDLE_CLASSPATH);
        if (this.debug) {
            log(Constants.BUNDLE_CLASSPATH + " is " + actualClasspath);
        }
        assertNotNull(actualClasspath);
        
        // mClasspath - original manifest classpath String array
        String[] mClasspath = this.manifest == null ? null : (String[])this.manifest.getMainAttributes().get(Constants.BUNDLE_CLASSPATH);
        // dClasspath - deployer specified classpath String array
        String[] dClasspath = this.deployOptions == null ? null : (String[])this.deployOptions.get(Constants.BUNDLE_CLASSPATH);

        if (mClasspath == null) {
            // verify initializing the first path entry to "WEB-INF/classes" 
            assertEquals(actualClasspath[0], WEBINFCLASSES);
        } else if (!exist(WEBINFCLASSES, mClasspath, false)){
            assertTrue(exist(WEBINFCLASSES, actualClasspath, false));
        } else {
            assertTrue(exist(WEBINFCLASSES, actualClasspath, false));
        }
        
        // verify each of the libraries from WEB-INF/lib is on the classpath
        URL url = this.b.getEntry(WEBINFCLASSES);
        //TODO: find out files from this lib dir and check if the files are added to classpath
        
        // verify deployer specified classpath is added on the classpath
        if (dClasspath != null) {
            for (int i = 0; i < dClasspath.length; i++) {
                assertTrue(exist(dClasspath[i], actualClasspath, false));
            }
        }
        
        // verify no dups on the classpath
        assertTrue(!containDuplicate(actualClasspath));
    }
    
    /*
     * validate the existence of Import-Package as it is required
     * Also validate existence of javax.servlet, javax.servlet.http, 
     * javax.servlet.jsp and javax.servlet.jsp.tagext packages:
     * also verify deploy options should overwrite original manifest options.
     */
    private void validateImportPackage() throws Exception {
        assertNotNull(this.dictionary);
        String[] actualImports = (String[])this.dictionary.get(Constants.IMPORT_PACKAGE);
        // verify Import-package exists
        if (this.debug) {
            log(Constants.IMPORT_PACKAGE + " is " + actualImports);
        }
        assertNotNull(actualImports);
        
        // mImports - original manifest Import-Package String array
        String[] mImports = this.manifest == null ? null : (String[])this.manifest.getMainAttributes().get(Constants.IMPORT_PACKAGE);
        // dImports - deployer specified Import-Package String arrary
        String[] dImports = this.deployOptions == null ? null : (String[])this.deployOptions.get(Constants.IMPORT_PACKAGE);
 
        // verify the existence of the servlet and jsp packages on Import-pacakage header
        // we use loose check here to allow directives
        for (int i = 0; i < this.REQUIREDIMPORT.length ; i++) {
            assertTrue(existLoose(this.REQUIREDIMPORT[i], actualImports));
        }
        
        // verify dImports are added to the actualImports
        if (dImports != null) {
            for (int i = 0; i < dImports.length ; i++) {
                assertTrue(exist(dImports[i], actualImports, true));
            }
        }
        
        // verify package specified by mImports are on the actualImports
        // if there are conflicts with dImports, dImports should win
        if (mImports != null) {
            for (int i = 0; i< mImports.length; i++) {
                boolean exist = exist(mImports[i], actualImports, true);
                if (!exist) {
                    // it is possible because of the conflicts with dImports
                    assertTrue(existLoose(getPackage(mImports[i]), dImports));
                    assertTrue(existLoose(getPackage(mImports[i]), actualImports));
                }              
            }
        }
        
        // verify no dups on the Import-Package list
        assertTrue(!containDuplicate(actualImports));
    }
    
    /*
     * Export-Package is optional
     * verify deploy options should overwrite original manifest options.
     */
    private void validateExportPackage() throws Exception {
        assertNotNull(this.dictionary);
        String[] actualExports = (String[])this.dictionary.get(Constants.EXPORT_PACKAGE);
        // verify Import-package exists
        if (this.debug) {
            log(Constants.EXPORT_PACKAGE + " is " + actualExports);
        }
        // assertNotNull(actualExports);
        
        // mExports - original manifest Export-Package String array
        String[] mExports = this.manifest == null ? null : (String[])this.manifest.getMainAttributes().get(Constants.EXPORT_PACKAGE);
        // dExports - deployer specified Export-Package String array
        String[] dExports = this.deployOptions == null ? null : (String[])this.deployOptions.get(Constants.EXPORT_PACKAGE);
        
        // verify dImports are added to the actualImports
        if (dExports != null) {
            for (int i = 0; i < dExports.length ; i++) {
                assertTrue(exist(dExports[i], actualExports, true));
            }
        }
        
        // verify package specified by mExports are on the actualExports
        // if there are conflicts with dExports, dExports should win
        if (mExports != null) {
            for (int i = 0; i< mExports.length; i++) {
                boolean exist = exist(mExports[i], actualExports, true);
                if (!exist) {
                    // it is possible because of the conflicts with dExports
                    assertTrue(existLoose(getPackage(mExports[i]), dExports));
                    assertTrue(existLoose(getPackage(mExports[i]), actualExports));
                }              
            }
        }
        
        // verify no dups on the Import-Package list
        assertTrue(!containDuplicate(actualExports));
    }
    
    /*
     * validate the existence of Web-ContextPath as it is required
     * Also validate:
     * 1. the deployer specified Web-ContextPath value will be used.
     * 2. Otherwise, preserve the Web-ContextPath value in the manifest file
     */
    private void validateWebContextPath() throws Exception {
        // verify Web-ContextPath exists
        // TODO replace "Web-ContextPath" with the Constants defined from RFC 66 API WebContainer.WEB_CONTEXT_PATH
        log("verify Web-ContextPath exists as it is required");
        if (this.debug) {
            log("Web-ContextPath is " + (String)this.dictionary.get("Web-ContextPath"));
        }
        assertNotNull((String)this.dictionary.get("Web-ContextPath"));
        
        // dWebContextPath - deployer specified Web-ContextPath value
        String dWebContextPath = this.deployOptions == null ? null : (String)this.deployOptions.get("Web-ContextPath");
        // mWebContextPath - manifest Web-ContextPath value
        String mWebContextPath = this.manifest == null ? null : (String)this.manifest.getMainAttributes().getValue("Web-ContextPath");
        if (dWebContextPath != null) {
            assertEquals((String) this.dictionary.get("Web-ContextPath"), dWebContextPath);
        } else if (mWebContextPath !=null) {
            assertEquals((String) this.dictionary.get("Web-ContextPath"), mWebContextPath);
        }
        // TODO: verify Web-ContextPath is unique on the server
    }
    
    /*
     * validate Web-JSPExtractLocation is optional
     * Also validate:
     * 1. the deployer specified Web-JSPExtractLocation value will be used.
     * 2. Otherwise, use the Web-JSPExtractLocation value in the manifest file
     */
    private void validateJSPExtractLocation() throws Exception {
        // verify Web-ContextPath exists
        // TODO replace "Web-JSPExtractLocation" with the Constants defined from RFC 66 API WebContainer.JSPExtractLocation.WEB_JSP_EXTRACT_LOCATION 
        log("verify Web-ContextPath exists as it is required");
        if (this.debug) {
            log("Web-JSPExtractLocation is " + (String)this.dictionary.get("Web-JSPExtractLocation"));
        }
        
        // dWebContextPath - deployer specified Web-ContextPath value
        String dJSPExtractLocation = this.deployOptions == null ? null : (String)this.deployOptions.get("Web-JSPExtractLocation");
        // mWebContextPath - manifest Web-ContextPath value
        String mJSPExtractLocation = this.manifest == null ? null : (String)this.manifest.getMainAttributes().getValue("Web-JSPExtractLocation");
        if (dJSPExtractLocation != null) {
            assertEquals((String) this.dictionary.get("Web-JSPExtractLocation"), dJSPExtractLocation);
        } else if (mJSPExtractLocation !=null) {
            assertEquals((String) this.dictionary.get("Web-JSPExtractLocation"), mJSPExtractLocation);
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
            if (c[j].trim().indexOf(exist.trim()) > -1) {
                find = true;
                break;
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

}
