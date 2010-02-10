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
package org.osgi.test.cases.webcontainer.junit;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.util.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test Import-Package manifest header processed correctly with various
 *          scenarios
 */
public class BundleImportPackageTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String[] imports, String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.IMPORT_PACKAGE, imports);
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage001() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS1, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage001_1() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.IMPORT_PACKAGE, IMPORTS1);
        options.put("web-contextPath", "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        options.remove("web-contextPath");
        options.put(WEB_CONTEXT_PATH, "/tw1");
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     * test url param can be case insensitive
     */
    public void testBundleImportPackage001_2() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put("import-package", IMPORTS1);
        options.put("Web-ContextPath", "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        options.remove("import-package");
        options.put(Constants.IMPORT_PACKAGE, IMPORTS1);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }
    
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage004() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS4, "/tw4");
        this.b = super.installWar(options, "tw4.war", true);
        super.generalHeadersTest(options, "tw4.war", true, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage005() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS4, "/tw5");
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage006() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS5, "/tw5");
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage007() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS1, "/tw1");
        try {
            this.b = super.installWar(options, "wmtw1.war", false);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        
        if (this.b != null) {
            this.b.uninstall();
        }
        
        // install bundle via regular install
        this.b = super.installBundle("wmtw1.war", false);
        super.generalHeadersTest(new HashMap<String, Object>(), "wmtw1.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage010() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS2, "/tw4");
        try {
            this.b = super.installWar(options, "wmtw4.war", true);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        
        assertFalse("should not be able to access page", super.ableAccessPath("/tw4"));

    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage011() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS4, "/tw5");
        try {
            this.b = super.installWar(options, "wmtw5.war", false);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        
        assertFalse("should not be able to access page", super.ableAccessPath("/tw5"));

    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage012() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS5, "/tw5");
        try {
            this.b = super.installWar(options, "wmtw5.war", false);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        
        assertFalse("should not be able to access page", super.ableAccessPath("/tw5"));

    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage013() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS1, "/tw5");
        try {
            this.b = super.installWar(options, "wm2tw5.war", false);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        
        assertFalse("should not be able to access page", super.ableAccessPath("/tw5"));

    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage014() throws Exception {
        final Map<String, Object> options = createOptions(IMPORTS3, "/tw5");
        this.b = super.installWar(options, "wm3tw5.war", true);
        assertTrue("service should be registered", super.checkServiceRegistered("/tw5"));
        assertTrue("should be able to access page", super.ableAccessPath("/tw5"));
    }

    /*
     * error case, when Import-Package specified by deployer has invalid version
     * 
     */
    public void testBundleImportPackageError001() throws Exception {
        // specify install options
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.IMPORT_PACKAGE, IMPORTS9);
        options.put(WEB_CONTEXT_PATH, "/tw4");
        // install the war file
        log("install war file: tw4.war at context path /tw4");
        // may not be able to installBundle correctly if version is specified
        // improperly??
        try {
            this.b = installBundle(super.getWarURL("tw4.war", options), false);
        } catch (BundleException e) {
            fail("Bundle should be installed but not resolved as Import-Package contains package that won't be resolved");
        }
        assertNotNull("Bundle b should not be null", this.b);
        assertEquals("Checking Bundle state is installed", Bundle.INSTALLED, b.getState());
        
        try {
            this.b.start();
            fail("No exception thrown, Error!");
        } catch (BundleException be) {
            // expected
        }
        // test unable to access /tw4 yet as it is not installed
        assertFalse("should not be able to access /tw4", super
                .ableAccessPath("/tw4/"));
    }

    
    /*
     * error case, when Import-Package specified by deployer has invalid version
     * 
     */
    public void testBundleImportPackageError002() throws Exception {
        // specify install options
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.IMPORT_PACKAGE, IMPORTS10);
        options.put(WEB_CONTEXT_PATH, "/tw5");
        // install the war file
        log("install war file: tw5.war at context path /tw5");
        // may not be able to installBundle correctly if version is specified
        // improperly??
        try {
            this.b = installBundle(super.getWarURL("tw5.war", options), false);
        } catch (BundleException e) {
            fail("Bundle should be installed but not resolved as Import-Package contains package that won't be resolved");
        }
        assertNotNull("Bundle b should not be null", this.b);
        assertEquals("Checking Bundle state is installed", Bundle.INSTALLED, b.getState());
        
        try {
            this.b.start();
            fail("No exception thrown, Error!");
        } catch (BundleException be) {
            // expected
        }
        
        // test unable to access /tw4 yet as it is not installed
        assertFalse("should not be able to access /tw5", super
                .ableAccessPath("/tw5/"));
    }
}
