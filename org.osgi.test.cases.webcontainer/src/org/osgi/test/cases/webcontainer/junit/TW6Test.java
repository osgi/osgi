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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test class for tw6.war which is same as tw1.war except it is signed
 */
public class TW6Test extends WebContainerTestBundleControl {
	
    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw6");

        // install + start the war file
        String loc = super.getWarURL("tw6.war", this.options);
        if (this.debug) {
            log("bundleName to be passed into installBundle is " + loc);	
        }
        this.b = installBundle(loc, true);
        
        // make sure we don't run tests until the servletcontext is registered with service registry
        boolean register = super.checkServiceRegistered(this.warContextPath);
        assertTrue("the ServletContext should be registered", register);
    }

    /*
     * set deployOptions to null to rely on the web container service to
     * generate the manifest
     */
    public void testBundleManifest() throws Exception {
		Manifest originalManifest = super.getManifest("/tw6.war");
        BundleManifestValidator validator = new BundleManifestValidator(this.b,
                originalManifest, this.options, this.debug);
        validator.validate();
    }
    
    /**
     * this test verifies the remove of the .RSA and .SF signature file by the url handler
     * @throws Exception
     */
    public void testRemoveSignedFiles() throws Exception {
        assertNotNull("/META-INF/MANIFEST.MF should be exist", this.b.getEntry("/META-INF/MANIFEST.MF"));
        assertNull("/META-INF/TEST.RSA should be removed by web url handler", this.b.getEntry("/META-INF/TEST.RSA"));
        assertNull("/META-INF/TEST.SF should be removed by web url handler", this.b.getEntry("/META-INF/TEST.SF"));
    }
    
    /**
     * this test verifies the removal of the manifest name sections that 
     * contain the resource’s check sums 
     * @throws Exception
     */
    public void testRemovedSignedPortionInManifest() throws Exception {
        URL url = this.b.getEntry("/META-INF/MANIFEST.MF");
        InputStream is = null;
        
        try {
            is = url.openStream();
            assertNotNull("is should not be null", is);
            Manifest mf = new Manifest(is);
            Attributes attrs = mf.getAttributes("SHA1-Digest");
            assertTrue(attrs == null || attrs.isEmpty());         
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /*
     * test home page
     */
    public void testBasic001() throws Exception {
        final String request = this.warContextPath + "/";
        String response = super.getResponse(request);
        super.checkTW1HomeResponse(response);
    }

    public void testBasic002() throws Exception {
        final String request = this.warContextPath + "/BasicTest";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(response, ConstantsUtil.BASICTESTWAR1);
    }

    public void testBasic003() throws Exception {
        final String request = this.warContextPath + "/error404.html";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(response, ConstantsUtil.ERROR404HTML);
    }

    public void testBasic004() throws Exception {
        final String request = this.warContextPath + "/ErrorTest?target=html";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(response, ConstantsUtil.ERROR404HTML);
    }

    public void testBasic005() throws Exception {
        final String request = this.warContextPath + "/ErrorTest";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertTrue(response.indexOf(ConstantsUtil.ERROR404JSP) > -1);
    }

    public void testBasic006() throws Exception {
        final String request = this.warContextPath + "/aaa";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            assertEquals(conn.getResponseCode(), 404);
            assertTrue(conn.getContentType().indexOf("text/html") > -1);
            String response = Dispatcher.dispatch(conn);
            if (this.debug) {
                log(response);
            }
            fail("should be getting an exception");
        } catch (IOException e) {
            // ignore - this can be expected
        } finally {
            conn.disconnect();
        }
    }

    public void testBasic007() throws Exception {
        final String request = this.warContextPath + "/image.html";
        String response = super.getResponse(request);
        // check if content of response is correct
        log("verify content of response is correct");
        assertEquals(response, ConstantsUtil.IMAGEHTML);
    }

    public void testBasic008() throws Exception {
        final String request = this.warContextPath + "/images/osgi.gif";
        final URL url = Dispatcher.createURL(request, this.server);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            if (this.debug) {
                log(conn.getContentType());
            }
            assertEquals(conn.getResponseCode(), 200);
            assertEquals(conn.getContentType(), "image/gif");
        } finally {
            conn.disconnect();
        }
    }
    
    public void testWelcomeJSP() throws Exception {
        super.checkWelcomeJSP(this.warContextPath);
    }
    
    // test unable to serve static page from WEB-INF dir
    public void testUnableServePage001() throws Exception {
        final String request = this.warContextPath + "/WEB-INF/non-serving.html";
        log("verify not able to access the page");
        assertFalse(super.ableAccessPath(request));
    }
    
    // test unable to serve static page from META-INF dir
    public void testUnableServePage002() throws Exception {
        final String request = this.warContextPath + "/META-INF/non-serving.html";
        log("verify not able to access the page");
        assertFalse(super.ableAccessPath(request));
    }
    
    // test unable to serve static page from OSGI-INF dir
    public void testUnableServePage003() throws Exception {
        final String request = this.warContextPath + "/OSGI-INF/non-serving.html";
        log("verify not able to access the page");
        assertFalse(super.ableAccessPath(request));
    }
    
    // test unable to serve static page from OSGI-OPT dir
    public void testUnableServePage004() throws Exception {
        final String request = this.warContextPath + "/OSGI-OPT/non-serving.html";
        log("verify not able to access the page");
        assertFalse(super.ableAccessPath(request));
    }
}
