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
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;

/**
 * @version $Rev$ $Date$
 * 
 *          test Import-Package manifest header processed correctly with various
 *          scenarios
 */
public class BundleImportPackageTest extends ManifestHeadersTestBundleControl {

    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage001() throws Exception {
        this.b = generalImportPackageTest(IMPORTS1, "/tw1", "tw1.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage002() throws Exception {
        this.b = generalImportPackageTest(IMPORTS2, "/tw2", "tw2.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage003() throws Exception {
        this.b = generalImportPackageTest(IMPORTS3, "/tw3", "tw3.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage004() throws Exception {
        this.b = generalImportPackageTest(IMPORTS4, "/tw4", "tw4.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage005() throws Exception {
        this.b = generalImportPackageTest(IMPORTS4, "/tw5", "tw5.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage006() throws Exception {
        this.b = generalImportPackageTest(IMPORTS5, "/tw5", "tw5.war", false);
    }

    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage007() throws Exception {
        this.b = generalImportPackageTest(IMPORTS1, "/tw1", "wmtw1.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage008() throws Exception {
        this.b = generalImportPackageTest(IMPORTS2, "/tw2", "wmtw2.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage009() throws Exception {
        this.b = generalImportPackageTest(IMPORTS3, "/tw3", "wmtw3.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage010() throws Exception {
        this.b = generalImportPackageTest(IMPORTS4, "/tw4", "wmtw4.war", true);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage011() throws Exception {
        this.b = generalImportPackageTest(IMPORTS4, "/tw5", "wmtw5.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage012() throws Exception {
        this.b = generalImportPackageTest(IMPORTS5, "/tw5", "wmtw5.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage013() throws Exception {
        this.b = generalImportPackageTest(IMPORTS1, "/tw5", "wm2tw5.war", false);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Import-Package
     */
    public void testBundleImportPackage014() throws Exception {
        this.b = generalImportPackageTest(IMPORTS3, "/tw5", "wm3tw5.war", false);
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
        assertFalse("should not be able to access /tw4", super
                .ableAccessPath("/tw4/"));
    }
    
    /*
     * generalImportPackageTest to be used by non-error test
     */
    private Bundle generalImportPackageTest(String[] imports, String cp,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.IMPORT_PACKAGE, imports);
        options.put(WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }
}
