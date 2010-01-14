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
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.ManifestHeadersTestBundleControl;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-ManifestVersion manifest header processed correctly 
 *          with various scenarios
 */
public class BundleSymbolicNameTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String name, String version, String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(Constants.BUNDLE_SYMBOLICNAME, name);
        if (version != null) {
            options.put(Constants.BUNDLE_VERSION, version);
        }
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName001() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME1, null, "/tw1");
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName004() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME4, null, "/tw4");
        this.b = super.installWar(options, "tw4.war", false);
        super.generalHeadersTest(options, "tw4.war", false, this.b);
    }
    

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName005() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME5, null, "/tw5");
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }
    
    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName006() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME1, null, "/tw1");
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(options, "wmtw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName009() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME4, null, "/tw4");
        this.b = super.installWar(options, "wmtw4.war", false);
        super.generalHeadersTest(options, "wmtw4.war", false, this.b);
    }
    

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName010() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME5, null, "/tw5");
        this.b = super.installWar(options, "wmtw5.war", false);
        super.generalHeadersTest(options, "wmtw5.war", false, this.b);
    }

    /*
     * verify Bundle-SymbolicName and Bundle-Version has to be unique
     */
    public void testBundleSymbolicNameError001() throws Exception {
        final Map<String, Object> options = createOptions(SYMBOLICNAME1, VERSION10, "/tw1");
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);

        log("attempt to install war file: tw4.war at context path /tw4");
        Bundle b2 = null;
        
        try {
    		Manifest originalManifest = super.getManifest("/tw4.war");
            final Map<String, Object> options2 = createOptions(SYMBOLICNAME1, VERSION10, "/tw4");
            try {
                b2 = installBundle(super.getWarURL("tw4.war", options2), false);
                fail("bundle install should fail as " + Constants.BUNDLE_SYMBOLICNAME + "is not unique: " + Constants.BUNDLE_SYMBOLICNAME + " = " + SYMBOLICNAME1);
            } catch (BundleException be) {
                // expected
            }
            assertNull("Bundle b2 should be null", b2);
    
            // test unable to access /tw3 yet as it is not installed
            assertFalse("should not be able to access /tw4", super.ableAccessPath("/tw4/"));
            
            // try let the system to generate a symbolic name
            final Map<String, Object> options3 = createOptions(null, VERSION10, "/tw4");
            log("2nd attempt to install war file: tw4.war at context path /tw4");
            try {
                b2 = installBundle(super.getWarURL("tw4.war", options3), true);
            } catch (Exception e) {
                fail("bundle install should succeed. " + e.getCause());
            }
            assertNotNull("Bundle b should not be null", b2);
            
            // check manifest generated correctly
            BundleManifestValidator validator = new BundleManifestValidator(b,
                    originalManifest, options, this.debug);
            try {
                validator.validate();
            } catch (IllegalArgumentException e) {
                fail("version format is valid - should not be getting an IllegalArgumentException"
                        + e.getCause());
            } catch (Exception e) {
                fail("should not get any exception during validation "
                        + e.getCause());
            }
    
            // test able to access /tw3
            try {
                String response = super.getResponse("/tw4/");
                super.checkTW4HomeResponse(response);
            } catch (Exception e) {
                fail("should not be getting an exception here " + e.getMessage());
            }
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }      
    }
    
    /*
     * verify SymbolicName has to be unique with 6 bundles
     */
    public void testMultipleBundleSymbolicName001() throws Exception {
        
        Map<String, Object> options = createOptions(SYMBOLICNAME1, null, "/tw1");
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);
        
        Bundle[] bundles = new Bundle[5];
        try {
            
            options = createOptions(SYMBOLICNAME4, null, "/tw4");
            bundles[0] = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, bundles[0]);
            
            options = createOptions(SYMBOLICNAME5, null, "/tw5");
            bundles[1] = super.installWar(options, "tw5.war", true);
            super.generalHeadersTest(options, "tw5.war", true, bundles[1]);
            
            options = createOptions(null, null, "/wmtw1");
            bundles[2] = super.installWar(options, "wmtw1.war", true);
            super.generalHeadersTest(options, "wmtw1.war", true, bundles[2]);

            options = createOptions(null, null, "/wmtw4");
            bundles[3] = super.installWar(options, "wmtw4.war", true);
            super.generalHeadersTest(options, "wmtw4.war", true, bundles[3]);
            
            
            options = createOptions(null, null, "/wmtw5");
            bundles[4] = super.installWar(options, "wmtw5.war", true);
            super.generalHeadersTest(options, "wmtw5.war", true, bundles[4]);
        } finally {
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }
}
