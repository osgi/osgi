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
package org.osgi.test.cases.webcontainer;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.webcontainer.WebContainer;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test Bundle-ManifestVersion manifest header processed correctly 
 *          with various scenarios
 */
public class BundleSymbolicNameTest extends ManifestHeadersTestBundleControl {
    private static final String SYMBOLICNAME1 = "ct-testwar1";
    private static final String SYMBOLICNAME2 = "ct-testwar2asdacakjdlkjasldja;dk;k121pi2910-921-0lkajdlkajsdlsadjlaksdjskajdklsajdklasjdksakdjaksljdaksljd";
    private static final String SYMBOLICNAME3 = "ct-testwar3-----------aklsdmklajsdl kajskldjaldlasjdklajdlksa;djklajsdkljakldjskaljdkaljsdlksjadklsajdkasdj";
    private static final String SYMBOLICNAME4 = "ct-testwar4--//laksldkl; laksldk;askd;aslkd;laksdlksaldkl;laksdpqoeiewihrfrhbgsmndb123e32";
    private static final String SYMBOLICNAME5 = "ct-testwar5";
    
    public void setUp() throws Exception {
        super.setUp();
    }
    
    public void tearDown() throws Exception {
        
    }

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName001() throws Exception {
        generalSymbolicNameTest(SYMBOLICNAME1, "/tw1", "tw1.war", false);
    }
    

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName002() throws Exception {
        generalSymbolicNameTest(SYMBOLICNAME2, "/tw2", "tw2.war", false);
    }
    

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName003() throws Exception {
        generalSymbolicNameTest(SYMBOLICNAME3, "/tw3", "tw3.war", true);
    }
    

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName004() throws Exception {
        generalSymbolicNameTest(SYMBOLICNAME4, "/tw4", "tw4.war", false);
    }
    

    /*
     * verify valid deployOptions overwrite original manifest Bundle-SymbolicName
     */
    public void testBundleSymbolicName005() throws Exception {
        generalSymbolicNameTest(SYMBOLICNAME5, "/tw5", "tw5.war", false);
    }
    
    /*
     * verify null SymbolicName specified in deployOptions
     */
    public void testBundleSymbolicName006() throws Exception {
        generalSymbolicNameTest(null, "/tw1", "tw1.war", false);
    }
    
    /*
     * verify null SymbolicName specified in deployOptions
     */
    public void testBundleSymbolicName007() throws Exception {
        generalSymbolicNameTest(null, "/tw2", "tw2.war", true);
    }
    
    /*
     * verify null SymbolicName specified in deployOptions
     */
    public void testBundleSymbolicName008() throws Exception {
        generalSymbolicNameTest(null, "/tw3", "tw3.war", false);
    }
    
    /*
     * verify null SymbolicName specified in deployOptions
     */
    public void testBundleSymbolicName009() throws Exception {
        generalSymbolicNameTest(null, "/tw4", "tw4.war", true);
    }
    
    /*
     * verify null SymbolicName specified in deployOptions
     */
    public void testBundleSymbolicName010() throws Exception {
        generalSymbolicNameTest(null, "/tw5", "tw5.war", false);
    }

    /*
     * verify SymbolicName has to be unique
     */
    public void testBundleSymbolicName011() throws Exception {
        
        this.b = generalSymbolicNameTest(SYMBOLICNAME1, "/tw1", "tw1.war", true);

        log("attempt to install war file: tw3.war at context path /tw3");
        Bundle b2 = null;
        Manifest originalManifest = super.getManifest("/resources/tw3/tw3.war");
        final Map options2 = new HashMap();
        options2.put(Constants.BUNDLE_SYMBOLICNAME, SYMBOLICNAME1);
        options2.put(WebContainer.WEB_CONTEXT_PATH, "/tw3");
        try {
            b2 = installBundle(super.getWarURL("tw3.war", options2), true);
            fail("bundle install should fail as " + Constants.BUNDLE_SYMBOLICNAME + "is not unique: " + Constants.BUNDLE_SYMBOLICNAME + " = " + SYMBOLICNAME1);
        } catch (Exception e) {
            // expected
        }
        assertNull("Bundle b2 should be null", b2);

        // test unable to access /tw3 yet as it is not installed
        assertFalse("should not be able to access /tw3", super.ableAccessPath("/tw3/"));
        
        // try let the system to generate a symbolic name
        final Map options3 = new HashMap();
        options3.put(Constants.BUNDLE_SYMBOLICNAME, null);
        options3.put(WebContainer.WEB_CONTEXT_PATH, "/tw3");
        log("2nd attempt to install war file: tw3.war at context path /tw3");
        try {
            b2 = installBundle(super.getWarURL("tw3.war", options3), true);
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
            String response = super.getResponse("/tw3/");
            super.checkTW3HomeResponse(response);
        } catch (Exception e) {
            fail("should not be getting an exception here " + e.getMessage());
        }

        uninstallBundle(b2);
    }
    
    /*
     * verify SymbolicName has to be unique with 10 bundles
     */
    public void testBundleSymbolicName012() throws Exception {
        
        this.b = generalSymbolicNameTest(SYMBOLICNAME1, "/tw1", "tw1.war", true);
        Bundle b2 = generalSymbolicNameTest(SYMBOLICNAME2, "/tw2", "tw2.war", true);
        Bundle b3 = generalSymbolicNameTest(SYMBOLICNAME3, "/tw3", "tw3.war", true);
        Bundle b4 = generalSymbolicNameTest(SYMBOLICNAME4, "/tw4", "tw4.war", true);
        Bundle b5 = generalSymbolicNameTest(SYMBOLICNAME5, "/tw5", "tw5.war", true);
        Bundle b6 = generalSymbolicNameTest(null, null, "tw1.war", true);
        Bundle b7 = generalSymbolicNameTest(null, null, "tw2.war", true);
        Bundle b8 = generalSymbolicNameTest(null, null, "tw3.war", true);
        Bundle b9 = generalSymbolicNameTest(null, null, "tw4.war", true);
        Bundle b10 = generalSymbolicNameTest(null, null, "tw5.war", true);
        uninstallBundle(b2);
        uninstallBundle(b3);
        uninstallBundle(b4);
        uninstallBundle(b5);
        uninstallBundle(b6);
        uninstallBundle(b7);
        uninstallBundle(b8);
        uninstallBundle(b9);
        uninstallBundle(b10);
    }
    /*
     * generalVersionTest to be used by non-error test
     */
    private Bundle generalSymbolicNameTest(String name, String cp,
            String warName, boolean start) throws Exception {
        // specify install options
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_SYMBOLICNAME, name);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return super.generalHeadersTest(options, warName, start);
    }


    // TODO create war manifest that contains the Bundle-SymbolicName header and more
    // tests
}
