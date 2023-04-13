/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.webcontainer.junit;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.test.cases.webcontainer.util.ManifestHeadersTestBundleControl;
import org.osgi.test.support.OSGiTestCaseProperties;
import org.osgi.test.support.sleep.Sleep;

/**
 * @version $Rev$ $Date$
 *
 *          test Web-ContextPath manifest header processed correctly with various
 *          scenarios
 */
public class BundleWebContextPathTest extends ManifestHeadersTestBundleControl {

    private Map<String, Object> createOptions(String cp) {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put(WEB_CONTEXT_PATH, cp);
        return options;
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath001() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     * test case insensitive
     */
    public void testWebContextPath001_1() throws Exception {
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put("WEB-CONTEXTPATH", WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", false);
        options.remove("WEB-CONTEXTPATH");
        options.put(WEB_CONTEXT_PATH, WEBCONTEXTPATH1);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath004() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "tw4.war", true);
        super.generalHeadersTest(options, "tw4.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath005() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
        this.b = super.installWar(options, "tw5.war", false);
        super.generalHeadersTest(options, "tw5.war", false, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath006() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
        this.b = super.installWar(options, "wmtw1.war", false);
        super.generalHeadersTest(options, "wmtw1.war", false, this.b);
    }

    /*
     * verify install a WAB directly without using webbundle url handler
     */
    public void testWebContextPath006_2() throws Exception {
        // for a wab, we don't need to install it via web bundle URL
        this.b = super.installBundle("wmtw1.war", true);
        super.generalHeadersTest(createOptions(null), "wmtw1.war", true, this.b);
    }

    /*
     * verify install a WAB directly and passing in other web bundle url params
     * and this should result bundle exception
     */
    public void testWebContextPath006_3() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
        options.put(Constants.BUNDLE_SYMBOLICNAME, "test passing in new symbolic name");
        try {
            this.b = super.installWar(options, "wmtw1.war", false);
            fail("should not be able to install the bundle correctly as Bundle-SymbolicName is not a valid param for WAB.  url params: " + options.toString());
        } catch (BundleException be) {
            // expected
        }
    }

    /*
     * verify install a WAB directly and passing in other web bundle url params
     * and this should result bundle exception
     */
    public void testWebContextPath006_4() throws Exception {
        final Map<String, Object> options = createOptions(null);
        options.put(Constants.BUNDLE_SYMBOLICNAME, "test passing in new symbolic name");
        try {
            this.b = super.installWar(options, "wmtw1.war", false);
            fail("should not be able to install the bundle correctly as there is no web-contextpath specified in webbundle url parms: " + options.toString());
        } catch (BundleException be) {
            // expected
        }
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath009() throws Exception {
		final Map<String, Object> options = createOptions(WEBCONTEXTPATH4
				+ "-1");
        this.b = super.installWar(options, "wmtw4.war", true);
        super.generalHeadersTest(options, "wmtw4.war", true, this.b);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath009_2() throws Exception {
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
		options.put(Constants.BUNDLE_SYMBOLICNAME, "OSGi TCK test wmtw4");
        try {
            this.b = super.installWar(options, "wmtw4.war", true);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        assertFalse("should not be able to access page", super.ableAccessPath(WEBCONTEXTPATH4));

    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath010() throws Exception {
        // wmtw5.war is not a wab since it doesn't have Import-Package
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
        this.b = super.installWar(options, "wmtw5.war", false);
        // install should succeed, but test won't work.
        assertFalse("should not be able to access page", super.ableAccessPath(WEBCONTEXTPATH5));

        // also verify that no other headers are modified
		Dictionary<String,String> d = this.b.getHeaders();
        Object classPath = d.get(Constants.BUNDLE_CLASSPATH);
        String cp = classPath == null ? "" : (String)classPath;
        assertTrue("Bundle-Classpath should be empty as it doesn't exist in the bundle before", cp.length() == 0);
    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath010_2() throws Exception {
        // wmtw5.war is not a wab since it doesn't have Import-Package
        final Map<String, Object> options = createOptions(WEBCONTEXTPATH5);
		options.put(Constants.BUNDLE_SYMBOLICNAME, "OSGi TCK test wmtw5");
        try {
            this.b = super.installWar(options, "wmtw5.war", false);
            fail("install bundle should fail");
        } catch (BundleException e){
            // expected since this is a bundle
        }
        assertFalse("should not be able to access page", super.ableAccessPath(WEBCONTEXTPATH5));

    }

    /*
     * verify valid deployOptions overwrite original manifest Web-ContextPath
     */
    public void testWebContextPath010_3() throws Exception {
        // wmtw5.war is not a wab since it doesn't have Import-Package
        final Map<String, Object> options = new HashMap<String, Object>();
		options.put(Constants.BUNDLE_SYMBOLICNAME, "OSGi TCK test wmtw5");
        try {
            this.b = super.installWar(options, "wmtw5.war", false);
            fail("install should fail because Web-contextPath url param is not specified");
        } catch (BundleException be) {
            // expected
        }
    }

    /*
     * verify Web-ContextPath doesn't start w/ forward slash still works
     */
	public void testWebContextPath011() throws Exception {
        this.b = super.installBundle("wmtw5.war", true);
        // this won't work
        assertFalse("should not be able to access " + WEBCONTEXTPATH5 + "_wm/BundleContextTestServlet", super.ableAccessPath(WEBCONTEXTPATH5 + "_wm/BundleCOntextTestServlet"));
        assertFalse("should not be able to access " + WEBCONTEXTPATH5 + "_wm/ClasspathTestServlet", super.ableAccessPath(WEBCONTEXTPATH5 + "_wm/ClasspathTestServlet"));
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath001() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH);
        this.b = super.installWar(options, "tw1.war", false);
        super.generalHeadersTest(options, "tw1.war", false, this.b);
    }

    /*
     * verify long Web-ContextPath specified in deployOptions
     */
    public void testLongWebContextPath004() throws Exception {
        final Map<String, Object> options = createOptions(LONGWEBCONTEXTPATH);
        this.b = super.installWar(options, "wmtw1.war", true);
        super.generalHeadersTest(options, "wmtw1.war", true, this.b);
    }

    /*
     * verify Web-ContextPath has to be unique
     */
    public void testWebContextPathError001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);

        // install the war file, reuse the same options as tw1
        log("attempt to install war file: tw4.war at context path " + WEBCONTEXTPATH4);
        Bundle b2 = null;
        options = createOptions(WEBCONTEXTPATH4);

        b2 = installBundle(super.getWarURL("tw4.war", options), true);
        // should only able to access TW1 home page, as web extender should emit a FAILED event
        // when web context path is not unique for TW4
        assertTrue("check the web context path " + WEBCONTEXTPATH4 + " in service registry",
                super.checkServiceRegistered(WEBCONTEXTPATH4));
        String response = super.getResponse(WEBCONTEXTPATH4);
        super.checkTW1HomeResponse(response);
        uninstallBundle(b2);

        b2 = null;
        // give a unique web context path
        options = createOptions(WEBCONTEXTPATH4 + "tw4.war");
        try {
            b2 = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, b2);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }
    }

    /*
     * verify Web-ContextPath is available after when the war that uses the same Web-ContextPath is uninstalled
     */
    public void testWebContextPathError002() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH4);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);

        // install the war file, reuse the same options as tw1
        log("attempt to install war file: tw4.war at context path " + WEBCONTEXTPATH4);
        Bundle b2 = null;
        options = createOptions(WEBCONTEXTPATH4);

        try {
            b2 = installBundle(super.getWarURL("tw4.war", options), true);
            // should only able to access TW1 home page, as web extender should emit a FAILED event
            // when web context path is not unique for TW4
            assertTrue("check the web context path " + WEBCONTEXTPATH4 + " in service registry",
                    super.checkServiceRegistered(WEBCONTEXTPATH4));
            String response = super.getResponse(WEBCONTEXTPATH4);
            super.checkTW1HomeResponse(response);
            uninstallBundle(this.b);
            this.b = null;

            // previously installed b2 should get started now after b is uninstalled
            // as the particular web-contextpath is avail now
            super.checkServiceRegistered(WEBCONTEXTPATH4);
			Sleep.sleep(OSGiTestCaseProperties.getTimeout()
					* OSGiTestCaseProperties.getScaling());
            response = super.getResponse(WEBCONTEXTPATH4);
            super.checkTW4HomeResponse(response);

            super.generalHeadersTest(options, "tw4.war", true, b2);
        } catch (Exception e) {
            fail("should install successfully and pick up the unused web contextpath " + WEBCONTEXTPATH4);
        } finally {
            if (b2 != null) {
                uninstallBundle(b2);
            }
        }

        // install bundle b back and should succeed
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);
    }

    /*
     * verify install 6 web applications
     */
    public void testMultipleWebContextPath001() throws Exception {
        Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
        this.b = super.installWar(options, "tw1.war", true);
        super.generalHeadersTest(options, "tw1.war", true, this.b);

        Bundle[] bundles = new Bundle[5];
        try {
            options = createOptions(WEBCONTEXTPATH4);
            bundles[0] = super.installWar(options, "tw4.war", true);
            super.generalHeadersTest(options, "tw4.war", true, bundles[0]);

            options = createOptions(WEBCONTEXTPATH5);
            bundles[1] = super.installWar(options, "tw5.war", false);
            super.generalHeadersTest(options, "tw5.war", false, bundles[1]);

            options = createOptions(LONGWEBCONTEXTPATH);
            bundles[2] = super.installWar(options, "wmtw1.war", false);
            super.generalHeadersTest(options, "wmtw1.war", false, bundles[2]);

            options = createOptions(WEBCONTEXTPATH4 + "_wm");
            bundles[3] = super.installWar(options, "wmtw4.war", false);
            super.generalHeadersTest(options, "wmtw4.war", false, bundles[3]);

            options = createOptions(WEBCONTEXTPATH5 + "_wm");
            bundles[4] = super.installWar(options, "wmtw5.war", true);
            assertFalse("should not be able to access " + WEBCONTEXTPATH5 + "_wm/BundleContextTestServlet", super.ableAccessPath(WEBCONTEXTPATH5 + "_wm/BundleCOntextTestServlet"));
            assertFalse("should not be able to access " + WEBCONTEXTPATH5 + "_wm/ClasspathTestServlet", super.ableAccessPath(WEBCONTEXTPATH5 + "_wm/ClasspathTestServlet"));
        } finally {
            for (int i = 0; i < bundles.length; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }

            // let's wait some time for the bundle to be uninstalled fully
			Sleep.sleep(OSGiTestCaseProperties.getTimeout()
					* OSGiTestCaseProperties.getScaling());
        }
    }

    /*
     * verify install 100 web applications
     */
    public void testMultipleWebContextPath002() throws Exception {
        Bundle[] bundles = new Bundle[100];
        try {
            for (int i = 0; i < 100; i++) {
                Map<String, Object> options = createOptions(WEBCONTEXTPATH1 + "_" + i);
                options.put(Constants.BUNDLE_SYMBOLICNAME, "tw1_" + i + "_test_war");
                bundles[i] = installWar(options, "tw1.war", true);
                super.generalHeadersTest(options, "tw1.war", true, bundles[i]);
            }
        } finally {
            for (int i = 0; i < 100; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
            // let's wait some time for the bundle to be uninstalled fully
			Sleep.sleep(OSGiTestCaseProperties.getTimeout()
					* OSGiTestCaseProperties.getScaling());
        }
    }

    /*
     * verify install 10 web applications with same web-contextpath
     * and the correct one are being deployed when the web-contextpath is available
     */
    public void testMultipleWebContextPath003() throws Exception {
        Bundle[] bundles = new Bundle[10];
        try {
            for (int i = 0; i < 10; i++) {
                Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
                options.put(Constants.BUNDLE_SYMBOLICNAME, "tw1_" + i + "_test_war");
                bundles[i] = installWar(options, "tw1.war", true);
            }

            // bundles[0] should be working fine
            Map<String, Object> options = createOptions(WEBCONTEXTPATH1);
            options.put(Constants.BUNDLE_SYMBOLICNAME, "tw1_0_test_war");
            super.generalHeadersQuickTest(options, "tw1.war", true, bundles[0]);

            for (int i = 0; i < 9; i++) {
                bundles[i].stop();
				Sleep.sleep(OSGiTestCaseProperties.getTimeout()
						* OSGiTestCaseProperties.getScaling());
                options = createOptions(WEBCONTEXTPATH1);
                options.put(Constants.BUNDLE_SYMBOLICNAME, "tw1_" + (i + 1) + "_test_war");
                super.generalHeadersQuickTest(options, "tw1.war", true, bundles[i+1]);
            }

        } finally {
            for (int i = 0; i < 10; i++) {
                if (bundles[i] != null) {
                    uninstallBundle(bundles[i]);
                }
            }
        }
    }
}
