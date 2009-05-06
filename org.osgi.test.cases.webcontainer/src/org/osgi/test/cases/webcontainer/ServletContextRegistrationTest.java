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

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.webcontainer.WebContainer;
import org.osgi.test.cases.webcontainer.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test web container extender registers servletcontext with the web application bundle
 *          with bundle-symbolicname, bundle-version and web-contextpath information
 */
public class ServletContextRegistrationTest extends
        WebContainerTestBundleControl {

    Bundle b;
    
    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        if (this.b != null && this.b.getState() != Bundle.UNINSTALLED) {
            this.b.uninstall();
        }
        this.b = null;
    }
    
    public void testSimpleServletContextReg001() throws Exception {

        this.b = registerWarBundleTest(createOptions("1.0", "ct-testwar1", "/tw1"), "tw1.war", true);
    }
    
    public void testSimpleServletContextReg002() throws Exception {

        this.b = registerWarBundleTest(createOptions("2.0", "ct-testwar2", "/tw2"), "tw2.war", false);
    }
    
    public void testSimpleServletContextReg003() throws Exception {

        this.b = registerWarBundleTest(createOptions("3.0", "ct-testwar3", "/tw3"), "tw3.war", true);
    }
    
    
    public void testSimpleServletContextReg004() throws Exception {

        this.b = registerWarBundleTest(createOptions("4.0", "ct-testwar4", "/tw4"), "tw4.war", false);
    }
    
    public void testSimpleServletContextReg005() throws Exception {

        this.b = registerWarBundleTest(createOptions("5.0", "ct-testwar5", "/tw5"), "tw5.war", true);
    }
    
    public void testSimpleServletContextReg006() throws Exception {

        this.b = registerWarBundleTest(createOptions(null, null, "/tw1"), "tw1.war", false);
    }
    
    public void testSimpleServletContextReg007() throws Exception {

        this.b = registerWarBundleTest(createOptions("1.0", "ct-testwar7", null), "tw2.war", false);
    }
    
    public void testSimpleServletContextReg008() throws Exception {

        this.b = registerWarBundleTest(createOptions(null, null, null), "tw3.war", true);
    }
    
    
    public void testSimpleServletContextReg009() throws Exception {

        this.b = registerWarBundleTest(createOptions(null, "ct-testwar9", null), "tw4.war", true);
    }
    
    public void testSimpleServletContextReg010() throws Exception {

        this.b = registerWarBundleTest(createOptions("1.0", null, "/tw5"), "tw5.war", false);
    }
    
    /*
     * test deploy a newer version of the same app
     */
    public void testMultiServletContextReg001() throws Exception {
        this.b =  registerWarBundleTest(createOptions(null, null, null), "tw1.war", true);
        Bundle b2 = registerWarBundleTest(createOptions(null, null, null), "tw1.war", true);
        Bundle b3 = registerWarBundleTest(createOptions(null, null, null), "tw1.war", true);
        uninstallBundle(b2);
        uninstallBundle(b3);
    }
    
    /*
     * test deploy a newer version of the same app
     */
    public void testMultiServletContextReg002() throws Exception {
        this.b =  registerWarBundleTest(createOptions(null, "ct-testwar1", null), "tw1.war", true);
        Bundle b2 = registerWarBundleTest(createOptions(null, "ct-testwar1", null), "tw1.war", true);
        Bundle b3 = registerWarBundleTest(createOptions(null, "ct-testwar1", null), "tw1.war", true);
        uninstallBundle(b2);
        uninstallBundle(b3);
    }
    
    
    /*
     * test deploy a newer version of the same app
     */
    public void testMultiServletContextReg003() throws Exception {
        this.b =  registerWarBundleTest(createOptions("1.0", null, null), "tw1.war", true);
        Bundle b2 = registerWarBundleTest(createOptions("1.1", null, null), "tw1.war", true);
        Bundle b3 = registerWarBundleTest(createOptions("1.2", null, null), "tw1.war", true);
        // TODO possible add additional test here when rfc 66 determines the behavior here.  should
        // the bundle-symbolcname be the same for all 3 bundles?
        uninstallBundle(b2);
        uninstallBundle(b3);
    }
    
    
    /*
     * test 10 web applications.   verify bundle-version and bundle-symbolicname combined can be
     * created uniquely to identify the bundle, when some are not specified by deploy options.
     */
    public void testMultiServletContextReg004() throws Exception {
        this.b =  registerWarBundleTest(createOptions("1.0", "ct-testwar1", "/tw1"), "tw1.war", true);
        Bundle b2 = registerWarBundleTest(createOptions("1.0", "ct-testwar2", "/tw2"), "tw2.war", true);
        Bundle b3 = registerWarBundleTest(createOptions("1.0", "ct-testwar3", "/tw3"), "tw3.war", true);
        Bundle b4 = registerWarBundleTest(createOptions("1.0", "ct-testwar4", "/tw4"), "tw4.war", true);
        Bundle b5 = registerWarBundleTest(createOptions("1.0", "ct-testwar5", "/tw5"), "tw5.war", true);
        Bundle b6 = registerWarBundleTest(createOptions(null, null, null), "tw1.war", true);
        Bundle b7 = registerWarBundleTest(createOptions("1.0", null, null), "tw2.war", true);
        Bundle b8 = registerWarBundleTest(createOptions(null, "ct-testwar3", "/tw3"), "tw3.war", true);
        Bundle b9 = registerWarBundleTest(createOptions("2.0", "ct-testwar4", null), "tw4.war", true);
        Bundle b10 = registerWarBundleTest(createOptions("1.0", null, "/tw5"), "tw5.war", true);
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
     * verify install 100 web applications
     */
    public void testWebContextPath0018() throws Exception {
        Bundle[] bundles = new Bundle[100];
        for (int i = 0; i < 100; i++) {
            bundles[i] = registerWarBundleTest(null, "tw1.war", true);
        }
        for (int i = 0; i < 100; i++) {
            uninstallBundle(bundles[i]);
        }
    }
    
    private Map createOptions(String version, String sname, String cp) {
        final Map options = new HashMap();
        options.put(Constants.BUNDLE_VERSION, version);
        options.put(Constants.BUNDLE_SYMBOLICNAME, sname);
        options.put(WebContainer.WEB_CONTEXT_PATH, cp);
        return options;
    }
    
    private Bundle registerWarBundleTest(Map options, String warName, boolean start) throws Exception {
        String cp = options.get(WebContainer.WEB_CONTEXT_PATH) == null ? null
                : (String) options.get(WebContainer.WEB_CONTEXT_PATH);
        // install the war file
        log("install and start war file: " + warName + " at contextPath " + cp);
        Bundle b = null;
        ServiceReference sr;
        ServletContext sc;
        try {
            b = installBundle(super.getWarURL("tw5.war", options), start);
        } catch (BundleException e) {
            // expected
        }
        
        // validate the bundle
        assertNotNull("Bundle b should not be null", b);
        Manifest originalManifest = super.getManifestFromWarName(warName);
        BundleManifestValidator validator = new BundleManifestValidator(b,
                originalManifest, options, this.debug);
        validator.validate();
        
        if (!start) {
            assertEquals("Bundle status should be Resolved but not Active", b
                    .getState(), Bundle.RESOLVED);
            assertFalse(
                    "Bundle not started yet - should not be able to access "
                            + cp, super.ableAccessPath(cp));
            sr = getContext().getServiceReference(ServletContext.class.getName());
            assertNull("sr should be null as the bundle has not been started yet", sr);
            b.start();
            
        }
        sr = getContext().getServiceReference(ServletContext.class.getName());
        assertNotNull(sr);
        sc = (ServletContext)getContext().getService(sr);
        assertEquals("check if servlet context path is correct", sc.getContextPath(), (String)b.getHeaders().get(WebContainer.WEB_CONTEXT_PATH));
        
        // get the service reference by Bundle-SymbolicName
        ServiceReference[] srs = getContext().getServiceReferences(ServletContext.class.getName(), "(" + Constants.BUNDLE_SYMBOLICNAME + "=" + (String)b.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME));
        assertNotNull(srs);
        assertEquals((ServletContext)getContext().getService(srs[0]), sc);
        
        // get the service reference by context-path
        srs = getContext().getServiceReferences(ServletContext.class.getName(), "(" + WebContainer.WEB_CONTEXT_PATH + "=" + (String)b.getHeaders().get(WebContainer.WEB_CONTEXT_PATH));
        assertNotNull(srs);
        assertEquals((ServletContext)getContext().getService(srs[0]), sc);
        
        // rough test able to access the app
        assertTrue("should be able to access " + cp, super.ableAccessPath(cp));

        assertEquals("Bundle status should be Active", b.getState(),
                Bundle.ACTIVE);
        try {
            String response = super.getResponse(cp);
            super.checkHomeResponse(response, warName);
        } catch (Exception e) {
            fail("should not be getting an exception here " + e.getMessage());
        }

        b.stop();
        // test unable to access pathes yet as it is not started
        assertEquals("Bundle status should be Resolved but not Active", b
                .getState(), Bundle.RESOLVED);
        assertFalse("Bundle not started yet - should not be able to access "
                + cp, super.ableAccessPath(cp));
        
        sr = getContext().getServiceReference(ServletContext.class.getName());
        assertNull("sr should be null as the bundle has been stopped", sr);

        if (start) {
            b.start();
        }
        return b;
    }
}
