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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.jar.Manifest;

import org.osgi.test.cases.webcontainer.util.WebContainerTestBundleControl;
import org.osgi.test.cases.webcontainer.util.ConstantsUtil;
import org.osgi.test.cases.webcontainer.util.Dispatcher;
import org.osgi.test.cases.webcontainer.util.validate.BundleManifestValidator;

/**
 * @version $Rev$ $Date$
 * 
 *          test class for tw1.war
 */
public class TW1Test extends WebContainerTestBundleControl {
	
    @Override
    public void setUp() throws Exception {
        super.setUp();
        super.prepare("/tw1");

        // install + start the war file
        String loc = super.getWarURL("tw1.war", this.options);
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
		Manifest originalManifest = super.getManifest("/tw1.war");
        BundleManifestValidator validator = new BundleManifestValidator(this.b,
                originalManifest, this.options, this.debug);
        validator.validate();
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
        final String request = this.warContextPath + "/404.html";
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
}
